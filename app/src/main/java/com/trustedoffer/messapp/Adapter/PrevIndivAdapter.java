package com.trustedoffer.messapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.trustedoffer.messapp.Interface.ClickEvent;
import com.trustedoffer.messapp.ModelClass.OthersStatModelClass;
import com.trustedoffer.messapp.R;

import java.util.List;
import java.util.Locale;

public class PrevIndivAdapter extends RecyclerView.Adapter<PrevIndivAdapter.PrivIndivViewHolder> {
    private Context context;
    private List<OthersStatModelClass> list;
    public PrevIndivAdapter(Context context, List<OthersStatModelClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PrivIndivViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.others_stat_layout,parent,false);
        return new PrivIndivViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrivIndivViewHolder holder, int position) {
        final OthersStatModelClass data=list.get(position);
        holder.tvName.setText(data.getUser_name());
        holder.tvDebit.setText(String.format(Locale.US, "%.2f", data.getDebit()));
        holder.tvUsed.setText(String.format(Locale.US, "%.2f", data.getUsed()));
        holder.tvMeal.setText(Integer.toString(data.getMeal()));
        holder.tvUserStatus.setText(String.format(Locale.US, "%.2f", data.getMealStatus()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PrivIndivViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName,tvDebit,tvMeal,tvUsed,tvUserStatus;
        public PrivIndivViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvOtherStatNameId);
            tvDebit=itemView.findViewById(R.id.tvOtherStatDebitId);
            tvMeal=itemView.findViewById(R.id.tvOtherStatMealId);
            tvUsed=itemView.findViewById(R.id.tvOtherStatUsedId);
            tvUserStatus=itemView.findViewById(R.id.tvOtherStatLayStatusId);
        }
    }
}
