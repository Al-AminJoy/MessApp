package com.trustedoffer.messapp.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.trustedoffer.messapp.ConstantClasses.SharedPref;
import com.trustedoffer.messapp.Interface.NoMessageShowListener;
import com.trustedoffer.messapp.ModelClass.DebitReqModelClass;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;
import com.trustedoffer.messapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DebitReqAdapter extends RecyclerView.Adapter<DebitReqAdapter.DebitReqViewHolder> {
    private Context context;
    private List<DebitReqModelClass> list;
    private NoMessageShowListener noMessageShowListener;

    public DebitReqAdapter(NoMessageShowListener noMessageShowListener,Context context, List<DebitReqModelClass> list) {
        this.context = context;
        this.list = list;
        this.noMessageShowListener=noMessageShowListener;
    }

    @NonNull
    @Override
    public DebitReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.debit_request_layout,parent,false);
        return new DebitReqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DebitReqViewHolder holder, final int position) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DebitReqModelClass data=list.get(position);
        int day=data.getDay();
        int month=data.getMonth();
        int year=data.getYear();
        final double debit=data.getDebit();

        String name=data.getUser_name();
        holder.tvDebit.setText("Debit : "+String.format(Locale.US, "%.2f", debit));
        holder.tvDate.setText("Date : "+Integer.toString(day)+"-"+Integer.toString(month)+"-"+Integer.toString(year));
        holder.tvName.setText(name);
        holder.btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.linLayButtons.setVisibility(View.GONE);
                holder.pbConfirmDebit.setVisibility(View.VISIBLE);
                String approveTime= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                SharedPreferences preferences=context.getSharedPreferences(SharedPref.AppPackage,Context.MODE_PRIVATE);
                String approvedBy=preferences.getString(SharedPref.SpEmail,"");
                Task task1=db.document("messDatabase/debitRequest/debitReqCollection/"+""+data.getKey()).update("approve_time",approveTime,"approved_by",approvedBy,"approved",true);

                Task task2=db.collection("messDatabase").document("userData")
                        .collection("userDataCollection")
                        .whereEqualTo("user_email", data.getUser_email())
                        .whereEqualTo("date",data.getRequest_time())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                db.document("messDatabase/userData/userDataCollection/"+""+document.getId()).update("debit",debit);

                            }
                        }
                    }
                });
                Tasks.whenAllSuccess(task1,task2)
                        .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> objects) {
                                list.remove(position);
                                if (list.size()==0){
                                    noMessageShowListener.NoMessageListener(false);
                                }
                                notifyDataSetChanged();
                                Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                holder.linLayButtons.setVisibility(View.VISIBLE);
                                holder.pbConfirmDebit.setVisibility(View.GONE);
                                Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });
        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.linLayButtons.setVisibility(View.GONE);
                holder.pbConfirmDebit.setVisibility(View.VISIBLE);
                Toast.makeText(context,"Key : "+data.getKey(),Toast.LENGTH_LONG).show();
                db.document("messDatabase/debitRequest/debitReqCollection/"+""+data.getKey()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                list.remove(position);
                                if (list.size()==0){
                                    noMessageShowListener.NoMessageListener(false);
                                }
                                notifyDataSetChanged();
                                Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                holder.linLayButtons.setVisibility(View.VISIBLE);
                                holder.pbConfirmDebit.setVisibility(View.GONE);
                                Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DebitReqViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName,tvDate,tvDebit;
        private MaterialButton btConfirm,btDelete;
        private LinearLayout linLayButtons;
        private ProgressBar pbConfirmDebit;
        public DebitReqViewHolder(@NonNull View itemView) {
            super(itemView);
            btConfirm=itemView.findViewById(R.id.btDebitReqConfirmId);
            btDelete=itemView.findViewById(R.id.btDebitReqCancelId);
            tvName=itemView.findViewById(R.id.tvDebitReqNameId);
            tvDate=itemView.findViewById(R.id.tvDebitReqDateId);
            tvDebit=itemView.findViewById(R.id.tvDebitReqDebitId);
            pbConfirmDebit=itemView.findViewById(R.id.pbConfirmDebitProgressBar);
            linLayButtons=itemView.findViewById(R.id.linLayButtonsDebitReq);
        }
    }
}
