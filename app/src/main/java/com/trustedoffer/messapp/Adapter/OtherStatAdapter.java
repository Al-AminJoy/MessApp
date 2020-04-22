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

public class OtherStatAdapter extends RecyclerView.Adapter<OtherStatAdapter.OtherStatViewHolder> {
    private Context context;
    private List<OthersStatModelClass> list;
    private ClickEvent clickevent;

    public void setClickEvent(ClickEvent clickevent) {
        this.clickevent=clickevent;
    }

    public OtherStatAdapter(Context context, List<OthersStatModelClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OtherStatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.others_stat_layout,parent,false);
        return new OtherStatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OtherStatViewHolder holder, int position) {
        final OthersStatModelClass data=list.get(position);
        holder.tvName.setText(data.getUser_name());
        holder.tvDebit.setText(String.format(Locale.US, "%.2f", data.getDebit()));
        holder.tvUsed.setText(String.format(Locale.US, "%.2f", data.getUsed()));
        holder.tvMeal.setText(Integer.toString(data.getMeal()));
        holder.tvUserStatus.setText(String.format(Locale.US, "%.2f", data.getMealStatus()));
        holder.cvClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickevent.clickEventItem(data.getUser_email());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class OtherStatViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName,tvDebit,tvMeal,tvUsed,tvUserStatus;
        private CardView cvClick;
        public OtherStatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvOtherStatNameId);
            tvDebit=itemView.findViewById(R.id.tvOtherStatDebitId);
            tvMeal=itemView.findViewById(R.id.tvOtherStatMealId);
            tvUsed=itemView.findViewById(R.id.tvOtherStatUsedId);
            tvUserStatus=itemView.findViewById(R.id.tvOtherStatLayStatusId);
            cvClick=itemView.findViewById(R.id.cvOtherStatLayId);
        }
    }
}
