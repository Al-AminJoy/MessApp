package com.trustedoffer.messapp.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trustedoffer.messapp.Constant.SharedPref;
import com.trustedoffer.messapp.Interface.NoMessageShowListener;
import com.trustedoffer.messapp.ModelClass.DebitReqModelClass;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;
import com.trustedoffer.messapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DebitReqAdapter extends RecyclerView.Adapter<DebitReqAdapter.DebitReqViewHolder> {
    private Context context;
    private List<DebitReqModelClass> list;
    private NoMessageShowListener noMessageShowListener;
    private ProgressDialog progressDialog;

    public DebitReqAdapter(NoMessageShowListener noMessageShowListener, Context context, List<DebitReqModelClass> list) {
        this.context = context;
        this.list = list;
        this.noMessageShowListener = noMessageShowListener;
    }

    @NonNull
    @Override
    public DebitReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.debit_request_layout, parent, false);
        return new DebitReqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DebitReqViewHolder holder, final int position) {
        final DatabaseReference debitRef = FirebaseDatabase.getInstance().getReference("debitRequest");
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("userData");
        final DebitReqModelClass data = list.get(position);
        int day = data.getDay();
        int month = data.getMonth();
        int year = data.getYear();
        final double debit = data.getDebit();
        String name = data.getUser_name();
        holder.tvDebit.setText("Debit : " + String.format(Locale.US, "%.2f", debit));
        holder.tvDate.setText("Date : " + Integer.toString(day) + "-" + Integer.toString(month) + "-" + Integer.toString(year));
        holder.tvName.setText(name);
        try {

            holder.btConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Update Debit Request and userData Database and Remove Item From List
                    progressOp();
                    final String approveTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    SharedPreferences preferences = context.getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                    final String approvedBy = preferences.getString(SharedPref.SpEmail, "");
                    final String userName = data.getUser_name();
                    final String email = data.getUser_email();
                    final String messKey = data.getMess_key();
                    final String requestTime = data.getRequest_time();
                    final String debitKey = data.getKey();
                    final int day = data.getDay();
                    final int month = data.getMonth();
                    final int year = data.getYear();
                    final double debit = data.getDebit();
                    dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                UserDataModelClass userData = dataSnapshot1.getValue(UserDataModelClass.class);
                                int breakfast = userData.getBreakfast();
                                int lunch = userData.getLunch();
                                int dinner = userData.getDinner();
                                String date = userData.getDate();
                                if (email.equals(userData.getUser_email()) && requestTime.equals(date)) {
                                    dataRef.child(dataSnapshot1.getKey())
                                            .setValue(new UserDataModelClass(userName, email, messKey, date, approveTime, breakfast, lunch, dinner, day, month, year, true, debit))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    debitRef.child(debitKey)
                                                            .setValue(new DebitReqModelClass(userName, email, messKey, requestTime, approvedBy, approveTime, day, month, year, debit, true, debitKey))
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    list.remove(position);
                                                                    notifyItemRemoved(position);
                                                                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                                                    if (list.size() == 0) {
                                                                        noMessageShowListener.NoMessageListener(false);
                                                                    }
                                                                    progressDialog.dismiss();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                                                    progressDialog.dismiss();
                                                                }
                                                            });
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                }
            });
            holder.btDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Delete Debit Request Remove Item From List
                    progressOp();
                    String debitKey = data.getKey();
                    debitRef.child(debitKey)
                            .removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                    if (list.size() == 0) {
                                        noMessageShowListener.NoMessageListener(false);
                                    }
                                    progressDialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                }
            });
        } catch (Exception e) {

        }
    }

    private void progressOp() {
        progressDialog = new ProgressDialog(context, R.style.ProgressColor);
        progressDialog.setMessage("Processing...");
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DebitReqViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDate, tvDebit;
        private MaterialButton btConfirm, btDelete;
        private LinearLayout linLayButtons;
        private ProgressBar pbConfirmDebit;

        public DebitReqViewHolder(@NonNull View itemView) {
            super(itemView);
            btConfirm = itemView.findViewById(R.id.btDebitReqConfirmId);
            btDelete = itemView.findViewById(R.id.btDebitReqCancelId);
            tvName = itemView.findViewById(R.id.tvDebitReqNameId);
            tvDate = itemView.findViewById(R.id.tvDebitReqDateId);
            tvDebit = itemView.findViewById(R.id.tvDebitReqDebitId);
            pbConfirmDebit = itemView.findViewById(R.id.pbConfirmDebitProgressBar);
            linLayButtons = itemView.findViewById(R.id.linLayButtonsDebitReq);
        }
    }
}
