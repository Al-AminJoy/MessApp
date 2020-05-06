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
import com.trustedoffer.messapp.ModelClass.UpdateReqModelClass;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;
import com.trustedoffer.messapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpdateReqAdapter extends RecyclerView.Adapter<UpdateReqAdapter.UpdateReqViewHolder> {
    private Context context;
    private List<UpdateReqModelClass> list;
    private NoMessageShowListener noMessageShowListener;
    private ProgressDialog progressDialog;

    public UpdateReqAdapter(NoMessageShowListener noMessageShowListener, Context context, List<UpdateReqModelClass> list) {
        this.context = context;
        this.list = list;
        this.noMessageShowListener = noMessageShowListener;
    }

    @NonNull
    @Override
    public UpdateReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.update_req_layout, parent, false);
        return new UpdateReqViewHolder(view);
    }

    private void progressOp() {
        progressDialog = new ProgressDialog(context, R.style.ProgressColor);
        progressDialog.setMessage("Processing...");
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void onBindViewHolder(@NonNull final UpdateReqViewHolder holder, final int position) {
        final DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("updateRequest");
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("userData");
        final UpdateReqModelClass data = list.get(position);
        int day = data.getDay();
        int month = data.getMonth();
        int year = data.getYear();
        String name = data.getUser_name();
        holder.tvName.setText(name);
        holder.tvDate.setText("Date : " + Integer.toString(day) + "-" + Integer.toString(month) + "-" + Integer.toString(year));

        final double preDebit = data.getPreDebit();
        final double currDebit = data.getDebit();
        int preBre = data.getPreBreakfast();
        final int currBre = data.getBreakfast();
        int preLunch = data.getPreLunch();
        final int currLunch = data.getLunch();
        int preDinner = data.getPreDinner();
        final int currDinner = data.getDinner();

        holder.tvPreBre.setText(Integer.toString(preBre));
        holder.tvCurrBre.setText(Integer.toString(currBre));
        holder.tvPreLunch.setText(Integer.toString(preLunch));
        holder.tvCurrLunch.setText(Integer.toString(currLunch));
        holder.tvPreDinner.setText(Integer.toString(preDinner));
        holder.tvCurrDinner.setText(Integer.toString(currDinner));
        holder.tvPreDebit.setText(String.format(Locale.US, "%.2f", preDebit));
        holder.tvCurrDebit.setText(String.format(Locale.US, "%.2f", currDebit));
        try {
            holder.btConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Update userData and updateRequest
                    progressOp();
                    final String approveTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    SharedPreferences preferences = context.getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                    final String approvedBy = preferences.getString(SharedPref.SpEmail, "");
                    final String userName = data.getUser_name();
                    final String userEmail = data.getUser_email();
                    final String messKey = data.getMess_key();
                    final String requestTime = data.getRequest_time();
                    final String updateKey = data.getKey();
                    final int day = data.getDay();
                    final int month = data.getMonth();
                    final int year = data.getYear();
                    final double debit = data.getDebit();
                    final int breakfast = data.getBreakfast();
                    final int lunch = data.getLunch();
                    final int dinner = data.getDinner();
                    final double preDebit = data.getPreDebit();
                    final int preBreakfast = data.getPreBreakfast();
                    final int preLunch = data.getPreLunch();
                    final int preDinner = data.getPreDinner();
                    dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                UserDataModelClass userData = dataSnapshot1.getValue(UserDataModelClass.class);
                                if (userEmail.equals(userData.getUser_email()) && day == userData.getDay() && month == userData.getMonth() && year == userData.getYear()) {
                                    String date = userData.getDate();
                                    dataRef.child(dataSnapshot1.getKey())
                                            .setValue(new UserDataModelClass(userName, userEmail, messKey, date, approveTime, breakfast, lunch, dinner, day, month, year, true, debit))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    updateRef.child(updateKey)
                                                            .setValue(new UpdateReqModelClass(userName, userEmail, messKey, requestTime, approvedBy, approveTime, breakfast, lunch, dinner, day, month, year, preBreakfast, preLunch, preDinner, debit, preDebit, true, updateKey))
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
            holder.btCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Delete updateRequest and Remove from List
                    progressOp();
                    String updateKey = data.getKey();
                    updateRef.child(updateKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            list.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            holder.linLayButtons.setVisibility(View.VISIBLE);
                            holder.pbConfirm.setVisibility(View.GONE);
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UpdateReqViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDate, tvPreBre, tvPreLunch, tvPreDinner, tvPreDebit, tvCurrBre, tvCurrLunch, tvCurrDinner, tvCurrDebit;
        private MaterialButton btConfirm, btCancel;
        private ProgressBar pbConfirm;
        private LinearLayout linLayButtons;

        public UpdateReqViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUpdateReqLayNameId);
            tvDate = itemView.findViewById(R.id.tvUpdateReqLayDateId);
            tvPreBre = itemView.findViewById(R.id.tvUpdateReqLayPreBreId);
            tvPreLunch = itemView.findViewById(R.id.tvUpdateReqLayPreLunchId);
            tvPreDinner = itemView.findViewById(R.id.tvUpdateReqPreDinnerId);
            tvPreDebit = itemView.findViewById(R.id.tvUpdateReqLayPreDebitId);
            tvCurrBre = itemView.findViewById(R.id.tvUpdateReqLayCurrBreId);
            tvCurrLunch = itemView.findViewById(R.id.tvUpdateReqCurrLunchId);
            tvCurrDinner = itemView.findViewById(R.id.tvUpdateReqLayCurrDinnerId);
            tvCurrDebit = itemView.findViewById(R.id.tvUpdateReqLayCurrDebitId);
            btConfirm = itemView.findViewById(R.id.btUpdateReqConfirmId);
            btCancel = itemView.findViewById(R.id.btUpdateReqCancelId);
            pbConfirm = itemView.findViewById(R.id.pbConfirmUpdateProgressBar);
            linLayButtons = itemView.findViewById(R.id.linLayButtonsUpdateReq);
        }
    }
}
