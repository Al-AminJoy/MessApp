package com.trustedoffer.messapp.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trustedoffer.messapp.ConstantClasses.SharedPref;
import com.trustedoffer.messapp.Interface.NoMessageShowListener;
import com.trustedoffer.messapp.ModelClass.UpdateReqModelClass;
import com.trustedoffer.messapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpdateReqAdapter extends RecyclerView.Adapter<UpdateReqAdapter.UpdateReqViewHolder> {
    private Context context;
    private List<UpdateReqModelClass> list;
    private NoMessageShowListener noMessageShowListener;

    public UpdateReqAdapter(NoMessageShowListener noMessageShowListener,Context context, List<UpdateReqModelClass> list) {
        this.context = context;
        this.list = list;
        this.noMessageShowListener=noMessageShowListener;
    }

    @NonNull
    @Override
    public UpdateReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.update_req_layout,parent,false);
        return new UpdateReqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UpdateReqViewHolder holder, final int position) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final UpdateReqModelClass data=list.get(position);
        int day=data.getDay();
        int month=data.getMonth();
        int year=data.getYear();
        String name=data.getUser_name();
        holder.tvName.setText(name);
        holder.tvDate.setText("Date : "+Integer.toString(day)+"-"+Integer.toString(month)+"-"+Integer.toString(year));

        final double preDebit=data.getPreDebit();
        final double currDebit=data.getDebit();
        int preBre=data.getPreBreakfast();
        final int currBre=data.getBreakfast();
        int preLunch=data.getPreLunch();
        final int currLunch=data.getLunch();
        int preDinner=data.getPreDinner();
        final int currDinner=data.getDinner();

        holder.tvPreBre.setText(Integer.toString(preBre));
        holder.tvCurrBre.setText(Integer.toString(currBre));
        holder.tvPreLunch.setText(Integer.toString(preLunch));
        holder.tvCurrLunch.setText(Integer.toString(currLunch));
        holder.tvPreDinner.setText(Integer.toString(preDinner));
        holder.tvCurrDinner.setText(Integer.toString(currDinner));
        holder.tvPreDebit.setText(String.format(Locale.US, "%.2f", preDebit));
        holder.tvCurrDebit.setText(String.format(Locale.US, "%.2f", currDebit));

        holder.btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.linLayButtons.setVisibility(View.GONE);
                holder.pbConfirm.setVisibility(View.VISIBLE);
                final String approveTime= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                SharedPreferences preferences=context.getSharedPreferences(SharedPref.AppPackage,Context.MODE_PRIVATE);
                String approvedBy=preferences.getString(SharedPref.SpEmail,"");
                Task tast1=db.document("messDatabase/updateRequest/updateReqCollection/"+""+data.getKey()).update("approve_time",approveTime,"approved_by",approvedBy,"approved",true);
                Task task2=db.collection("messDatabase").document("userData")
                        .collection("userDataCollection")
                        .whereEqualTo("user_email", data.getUser_email())
                        .whereEqualTo("day",data.getDay())
                        .whereEqualTo("month",data.getMonth())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        db.document("messDatabase/userData/userDataCollection/"+""+document.getId()).update("debit",currDebit,"debit",currDebit,"lunch",currLunch,"dinner",currDinner,"breakfast",currBre,"updated",true,"update_time",approveTime);

                                    }
                                }
                            }
                        });
                Tasks.whenAllSuccess(tast1,task2).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> objects) {
                        list.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
                        holder.linLayButtons.setVisibility(View.VISIBLE);
                        holder.pbConfirm.setVisibility(View.GONE);
                        if (list.size()==0){
                            noMessageShowListener.NoMessageListener(false);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.linLayButtons.setVisibility(View.VISIBLE);
                        holder.pbConfirm.setVisibility(View.GONE);
                        Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        holder.btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.linLayButtons.setVisibility(View.GONE);
                holder.pbConfirm.setVisibility(View.VISIBLE);
               // Toast.makeText(context,"Key : "+data.getKey(),Toast.LENGTH_LONG).show();
                db.document("messDatabase/updateRequest/updateReqCollection/"+""+data.getKey()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        list.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
                        holder.linLayButtons.setVisibility(View.VISIBLE);
                        holder.pbConfirm.setVisibility(View.GONE);
                        if (list.size()==0){
                            noMessageShowListener.NoMessageListener(false);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.linLayButtons.setVisibility(View.VISIBLE);
                        holder.pbConfirm.setVisibility(View.GONE);
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

    public class UpdateReqViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName,tvDate,tvPreBre,tvPreLunch,tvPreDinner,tvPreDebit,tvCurrBre,tvCurrLunch,tvCurrDinner,tvCurrDebit;
        private MaterialButton btConfirm,btCancel;
        private ProgressBar pbConfirm;
        private LinearLayout linLayButtons;
        public UpdateReqViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvUpdateReqLayNameId);
            tvDate=itemView.findViewById(R.id.tvUpdateReqLayDateId);
            tvPreBre=itemView.findViewById(R.id.tvUpdateReqLayPreBreId);
            tvPreLunch=itemView.findViewById(R.id.tvUpdateReqLayPreLunchId);
            tvPreDinner=itemView.findViewById(R.id.tvUpdateReqPreDinnerId);
            tvPreDebit=itemView.findViewById(R.id.tvUpdateReqLayPreDebitId);
            tvCurrBre=itemView.findViewById(R.id.tvUpdateReqLayCurrBreId);
            tvCurrLunch=itemView.findViewById(R.id.tvUpdateReqCurrLunchId);
            tvCurrDinner=itemView.findViewById(R.id.tvUpdateReqLayCurrDinnerId);
            tvCurrDebit=itemView.findViewById(R.id.tvUpdateReqLayCurrDebitId);
            btConfirm=itemView.findViewById(R.id.btUpdateReqConfirmId);
            btCancel=itemView.findViewById(R.id.btUpdateReqCancelId);
            pbConfirm=itemView.findViewById(R.id.pbConfirmUpdateProgressBar);
            linLayButtons=itemView.findViewById(R.id.linLayButtonsUpdateReq);
        }
    }
}
