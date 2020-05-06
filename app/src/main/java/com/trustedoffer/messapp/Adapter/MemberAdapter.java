package com.trustedoffer.messapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.trustedoffer.messapp.Constant.SharedPref;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    private Context context;
    private List<MemberInfoModelClass> list;

    public MemberAdapter(Context context, List<MemberInfoModelClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.member_list_layout, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MemberViewHolder holder, final int position) {
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("userInfo");
        final MemberInfoModelClass dataList = list.get(position);
        String name = dataList.getUser_name();
        final String status = dataList.getUser_status();
        holder.tvName.setText(name);
        Picasso.get().load(dataList.getUser_image_url()).into(holder.ivUser);

        if (status.equals("admin")) {
            holder.tvStatus.setText("Admin");
        } else {
            holder.tvStatus.setText("Member");
        }
        try {
            holder.tvOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final PopupMenu popupMenu = new PopupMenu(context, holder.tvOption);
                    popupMenu.inflate(R.menu.option_menu);
                    SharedPreferences preferences = context.getSharedPreferences(SharedPref.AppPackage, Context.MODE_PRIVATE);
                    if ((preferences.getString(SharedPref.SpStatus, "")).equals("member") || dataList.getUser_status().equals("admin")) {
                        popupMenu.getMenu().findItem(R.id.optionMenuMakeAdmin).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.optionMenuRemove).setVisible(false);
                    } else {
                        popupMenu.getMenu().findItem(R.id.optionMenuMakeAdmin).setVisible(true);
                        popupMenu.getMenu().findItem(R.id.optionMenuRemove).setVisible(true);
                    }

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            final String number = dataList.getUser_number();
                            final String email = dataList.getUser_email();
                            final String userName = dataList.getUser_name();
                            final String messKey = dataList.getMess_key();
                            final String messName = dataList.getMess_name();
                            final String userGender = dataList.getGender();
                            final String userImage = dataList.getUser_image_url();
                            final String userKey = dataList.getKey();
                            switch (menuItem.getItemId()) {
                                case R.id.optionMenuMakeAdmin:
                                    //Update userInfo status to admin from member
                                    userRef.child(userKey)
                                            .setValue(new MemberInfoModelClass(userName, email, "admin", messName, messKey, number, userGender, userImage, userKey))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                                    MemberInfoModelClass updateData = new MemberInfoModelClass(userName, email, "admin", messName, messKey, number, userGender, userImage, userKey);
                                                    list.set(position, updateData);
                                                    notifyDataSetChanged();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    break;
                                case R.id.optionMenuRemove:
                                    showDialog(userRef, userName, email, number, userGender, userImage, userKey, position);
                                    break;
                                case R.id.optionMenuCall:
                                    phoneCallOp(number);
                                    break;
                                case R.id.optionMenuEmail:
                                    emailOp(email);
                                    break;
                                case R.id.optionMenuMessage:
                                    msgOp(number);
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        } catch (Exception e) {

        }

    }

    private void showDialog(final DatabaseReference userRef, final String userName, final String email, final String number, final String userGender, final String userImage, final String userKey, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View dialogView = layoutInflater.inflate(R.layout.remove_dialog, null);
        MaterialButton btYes = dialogView.findViewById(R.id.btRemoveYesDialogId);
        MaterialButton btNo = dialogView.findViewById(R.id.btRemoveNoDialogId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(dialogView).setCancelable(false).create();

        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove User From List and Update userInfo
                userRef.child(userKey)
                        .setValue(new MemberInfoModelClass(userName, email, "member", "", "", number, userGender, userImage, userKey))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                list.remove(position);
                                notifyItemRemoved(position);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                alertDialog.cancel();
            }
        });
        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void msgOp(String number) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null));
        smsIntent.putExtra("sms_body", "");
        context.startActivity(smsIntent);
    }

    private void emailOp(String email) {
        String receiver = email;
        String[] recevername = receiver.split(",");
        String subject = "Mess";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recevername);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.setType("message/rfc822");
        context.startActivity(Intent.createChooser(intent, "Choose App For Send"));
    }

    private void phoneCallOp(String number) {
        Log.d("NUMBER", "Number Is : " + number);
        Toast.makeText(context, number + "Call Selected", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvStatus, tvOption;
        private CircleImageView ivUser;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMemberNameId);
            tvStatus = itemView.findViewById(R.id.tvMemberStatusId);
            tvOption = itemView.findViewById(R.id.tvMemberOptionId);
            ivUser = itemView.findViewById(R.id.ivMemberImageId);
        }
    }
}
