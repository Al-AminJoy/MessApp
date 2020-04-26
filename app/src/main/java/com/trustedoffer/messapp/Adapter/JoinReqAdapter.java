package com.trustedoffer.messapp.Adapter;

import android.content.Context;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trustedoffer.messapp.ConstantClasses.SharedPref;
import com.trustedoffer.messapp.Interface.NoMessageShowListener;
import com.trustedoffer.messapp.ModelClass.JoinReqModelClass;
import com.trustedoffer.messapp.R;

import java.util.List;

public class JoinReqAdapter extends RecyclerView.Adapter<JoinReqAdapter.JoinReqViewHolder> {
    private Context context;
    private List<JoinReqModelClass> list;
    private NoMessageShowListener noMessageShowListener;

    public JoinReqAdapter(NoMessageShowListener noMessageShowListener,Context context, List<JoinReqModelClass> list) {
        this.noMessageShowListener=noMessageShowListener;
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public JoinReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.join_req_layout,parent,false);
        return new JoinReqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final JoinReqViewHolder holder, final int position) {
        final FirebaseFirestore db=FirebaseFirestore.getInstance();
        final JoinReqModelClass data=list.get(position);
       final String name=data.getUser_name();
       final String email=data.getUser_email();
       final String gender=data.getUser_gender();
       holder.tvName.setText(name);
       holder.tvEmail.setText(email);
       holder.tvGender.setText(gender);
       holder.btConfirm.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               confirmOp(db,email,holder.pbConfirm,holder.linLayButtons,position,data.getKey(),data.getMess_key(),data.getMess_name(),data.getUser_key());
           }
       });
       holder.btCancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               cancelOp(db,data.getKey(),holder.pbConfirm,holder.linLayButtons,position);
           }
       });
    }

    private void cancelOp(FirebaseFirestore db, String key, final ProgressBar pbConfirm, final LinearLayout linLayButtons, final int position) {
        pbConfirm.setVisibility(View.VISIBLE);
        linLayButtons.setVisibility(View.GONE);
        db.document("messDatabase/joinRequest/joinReqCollection/"+""+key).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        list.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context,"Rejected",Toast.LENGTH_SHORT).show();
                        pbConfirm.setVisibility(View.GONE);
                        linLayButtons.setVisibility(View.VISIBLE);
                        if (list.size()==0){
                            noMessageShowListener.NoMessageListener(false);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pbConfirm.setVisibility(View.GONE);
                        linLayButtons.setVisibility(View.VISIBLE);
                        Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void confirmOp(FirebaseFirestore db, String email, final ProgressBar pbConfirm, final LinearLayout linLayButtons, final int position, String reqKey, String mess_key, String mess_name, String user_key) {
        pbConfirm.setVisibility(View.VISIBLE);
        linLayButtons.setVisibility(View.GONE);
        Task task1=db.document("messDatabase/joinRequest/joinReqCollection/"+""+reqKey).update("approved",true);
        Task task2=db.document("messDatabase/userInfo/userInfoCollection/"+""+ user_key).update("mess_key",mess_key,"mess_name",mess_name,"user_status","member");
        Tasks.whenAllSuccess(task1,task2).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                list.remove(position);
                notifyDataSetChanged();
                pbConfirm.setVisibility(View.GONE);
                linLayButtons.setVisibility(View.VISIBLE);
                Toast.makeText(context,"New Member Added",Toast.LENGTH_SHORT).show();
                if (list.size()==0){
                    noMessageShowListener.NoMessageListener(false);
                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
                        Log.d("Accept_Failed","Reason : "+e);
                        pbConfirm.setVisibility(View.GONE);
                        linLayButtons.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class JoinReqViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName,tvEmail,tvGender;
        private MaterialButton btConfirm,btCancel;
        private ProgressBar pbConfirm;
        private LinearLayout linLayButtons;
        public JoinReqViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvJoinReqNameId);
            tvEmail=itemView.findViewById(R.id.tvJoinReqEmailId);
            tvGender=itemView.findViewById(R.id.tvJoinReqGenderId);
            btConfirm=itemView.findViewById(R.id.btJoinReqConfirmId);
            btCancel=itemView.findViewById(R.id.btJoinReqCancelId);
            pbConfirm=itemView.findViewById(R.id.pbJoinReqId);
            linLayButtons=itemView.findViewById(R.id.linLayButtonsJoinReq);
        }
    }
}
