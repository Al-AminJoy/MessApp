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
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trustedoffer.messapp.ConstantClasses.SharedPref;
import com.trustedoffer.messapp.ConstantClasses.StoredValues;
import com.trustedoffer.messapp.HomeFragment;
import com.trustedoffer.messapp.MemberFragment;
import com.trustedoffer.messapp.ModelClass.DebitReqModelClass;
import com.trustedoffer.messapp.ModelClass.MemberInfoModelClass;
import com.trustedoffer.messapp.ModelClass.UserDataModelClass;
import com.trustedoffer.messapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        View view= LayoutInflater.from(context).inflate(R.layout.member_list_layout,parent,false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MemberViewHolder holder, final int position) {
        final MemberInfoModelClass dataList=list.get(position);
        String name=dataList.getUser_name();
        final String status=dataList.getUser_status();
        holder.tvName.setText(name);
        if (status.equals("admin")){
            holder.tvStatus.setText("Admin");
        }
        else {
            holder.tvStatus.setText("Member");
        }
        holder.tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popupMenu=new PopupMenu(context,holder.tvOption);
                popupMenu.inflate(R.menu.option_menu);
                SharedPreferences preferences=context.getSharedPreferences(SharedPref.AppPackage,Context.MODE_PRIVATE);
                if ((preferences.getString(SharedPref.SpStatus,"")).equals("member") || dataList.getUser_status().equals("admin")){
                    popupMenu.getMenu().findItem(R.id.optionMenuMakeAdmin).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.optionMenuRemove).setVisible(false);
                }
                else {
                    popupMenu.getMenu().findItem(R.id.optionMenuMakeAdmin).setVisible(true);
                    popupMenu.getMenu().findItem(R.id.optionMenuRemove).setVisible(true);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String number= dataList.getUser_number();
                        String email= dataList.getUser_email();
                        switch (menuItem.getItemId()){
                            case R.id.optionMenuMakeAdmin :
                                FirebaseFirestore.getInstance().document("messDatabase/userInfo/userInfoCollection/"+""+dataList.getKey()).update("user_status","admin").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
                                        MemberInfoModelClass updateData=new MemberInfoModelClass(dataList.getUser_name(),dataList.getUser_email(),"admin",dataList.getMess_key(),dataList.getUser_number(),dataList.getGender(),dataList.getKey());
                                        list.set(position,updateData);
                                        notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case R.id.optionMenuRemove :
                                String passedKey=dataList.getKey();
                                showDialog(position,passedKey);
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
    }

    private void showDialog(final int position, final String passedKey) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View dialogView = layoutInflater.inflate(R.layout.remove_dialog, null);
        MaterialButton btYes = dialogView.findViewById(R.id.btRemoveYesDialogId);
        MaterialButton btNo = dialogView.findViewById(R.id.btRemoveNoDialogId);
        final androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(dialogView).setCancelable(false).create();

        btYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().document("messDatabase/userInfo/userInfoCollection/"+""+passedKey).update("user_status","member","mess_key","","mess_name","").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
                        list.remove(position);
                        notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialog.cancel();
            }
        });
        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Cancelled",Toast.LENGTH_SHORT).show();
                alertDialog.cancel();
            }
        });
        alertDialog.show();


    }


    private void msgOp(String number) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null));
        smsIntent.putExtra("sms_body","");
        context.startActivity(smsIntent);
    }

    private void emailOp(String email) {
        String receiver=email;
        String[] recevername=receiver.split(",");
        String subject="Mess";
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,recevername);
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.setType("message/rfc822");
        context.startActivity(Intent.createChooser(intent,"Choose App For Send"));
    }

    private void phoneCallOp(String number) {

        Log.d("NUMBER","Number Is : "+number);
        Toast.makeText(context,number+"Call Selected",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" +number));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName,tvStatus,tvOption;
        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvMemberNameId);
            tvStatus=itemView.findViewById(R.id.tvMemberStatusId);
            tvOption=itemView.findViewById(R.id.tvMemberOptionId);

        }
    }
}
