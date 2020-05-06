package com.trustedoffer.messapp.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.trustedoffer.messapp.Interface.NoMessageShowListener;
import com.trustedoffer.messapp.ModelClass.JoinReqModelClass;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class JoinReqAdapter extends RecyclerView.Adapter<JoinReqAdapter.JoinReqViewHolder> {
    private Context context;
    private List<JoinReqModelClass> list;
    private NoMessageShowListener noMessageShowListener;
    private ProgressDialog progressDialog;

    public JoinReqAdapter(NoMessageShowListener noMessageShowListener, Context context, List<JoinReqModelClass> list) {
        this.noMessageShowListener = noMessageShowListener;
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public JoinReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.join_req_layout, parent, false);
        return new JoinReqViewHolder(view);
    }

    private void progressOp() {
        progressDialog = new ProgressDialog(context, R.style.ProgressColor);
        progressDialog.setMessage("Processing...");
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void onBindViewHolder(@NonNull final JoinReqViewHolder holder, final int position) {
        final DatabaseReference joinRef = FirebaseDatabase.getInstance().getReference("joinRequest");
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("userInfo");
        final JoinReqModelClass data = list.get(position);
        final String name = data.getUser_name();
        final String email = data.getUser_email();
        final String gender = data.getUser_gender();
        final String image = data.getUser_image();
        holder.tvName.setText(name);
        holder.tvEmail.setText(email);
        holder.tvGender.setText(gender);
        Picasso.get().load(image).into(holder.ivUser);
        try {
            holder.btConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressOp();
                    final String joinKey = data.getKey();
                    final String userKey = data.getUser_key();
                    final String userName = data.getUser_name();
                    final String reqTime = data.getSend_time();
                    final String userEmail = data.getUser_email();
                    final String messName = data.getMess_name();
                    final String messKey = data.getMess_key();
                    final String userNumber = data.getUser_number();
                    final String userGender = data.getUser_gender();
                    final String userImage = data.getUser_image();
                    userRef.child(userKey).setValue(new MemberInfoModelClass(userName, userEmail, "member", messName, messKey, userNumber, userGender, userImage, userKey))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    joinRef.child(joinKey).setValue(new JoinReqModelClass(userName, userEmail, userGender, reqTime, messKey, messName, userKey, userNumber, userImage, true, joinKey))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    list.remove(position);
                                                    notifyItemRemoved(position);
                                                    Toast.makeText(context, "New Member Added", Toast.LENGTH_SHORT).show();
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
                                                    Log.d("Accept_Failed", "Reason : " + e);
                                                    //holder.pbConfirm.setVisibility(View.GONE);
                                                    //holder.btCancel.setVisibility(View.VISIBLE);
                                                    // holder.btConfirm.setVisibility(View.VISIBLE);
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }
                            });
                }
            });
            holder.btCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelOp(joinRef, data.getKey(), holder.pbConfirm, holder.linLayButtons, position, holder.btConfirm, holder.btCancel);
                }
            });
        } catch (Exception e) {

        }
    }

    private void cancelOp(DatabaseReference joinRef, String key, final ProgressBar pbConfirm, final LinearLayout linLayButtons, final int position, final MaterialButton btConfirm, final MaterialButton btCancel) {
        //Delete Request From joinRequest Database and Update List
        progressOp();
        joinRef.child(key).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        list.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show();
                        if (list.size() == 0) {
                            noMessageShowListener.NoMessageListener(false);
                        }
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class JoinReqViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvEmail, tvGender;
        private MaterialButton btConfirm, btCancel;
        private ProgressBar pbConfirm;
        private LinearLayout linLayButtons;
        private CircleImageView ivUser;

        public JoinReqViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvJoinReqNameId);
            tvEmail = itemView.findViewById(R.id.tvJoinReqEmailId);
            tvGender = itemView.findViewById(R.id.tvJoinReqGenderId);
            btConfirm = itemView.findViewById(R.id.btJoinReqConfirmId);
            btCancel = itemView.findViewById(R.id.btJoinReqCancelId);
            pbConfirm = itemView.findViewById(R.id.pbJoinReqId);
            linLayButtons = itemView.findViewById(R.id.linLayButtonsJoinReq);
            ivUser = itemView.findViewById(R.id.ivJoinReqImageId);
        }
    }
}
