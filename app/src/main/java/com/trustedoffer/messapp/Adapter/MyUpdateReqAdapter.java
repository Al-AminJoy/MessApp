package com.trustedoffer.messapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trustedoffer.messapp.ModelClass.UpdateReqModelClass;
import com.trustedoffer.messapp.R;

import java.util.List;
import java.util.Locale;

public class MyUpdateReqAdapter extends RecyclerView.Adapter<MyUpdateReqAdapter.MyUpdateReqViewHolder> {
    private Context context;
    private List<UpdateReqModelClass> list;

    public MyUpdateReqAdapter(Context context, List<UpdateReqModelClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyUpdateReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.my_update_req_layout,parent,false);
        return new MyUpdateReqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyUpdateReqViewHolder holder, int position) {
        final UpdateReqModelClass data=list.get(position);
        int day=data.getDay();
        int month=data.getMonth();
        int year=data.getYear();
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
        if (data.isApproved()==true){
            holder.tvStatus.setText("Approved");
            holder.tvStatus.setBackgroundColor(Color.parseColor("#5BC0D8"));
        }
        else {
            holder.tvStatus.setText("Pending");
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FF0000"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyUpdateReqViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStatus,tvDate,tvPreBre,tvPreLunch,tvPreDinner,tvPreDebit,tvCurrBre,tvCurrLunch,tvCurrDinner,tvCurrDebit;
        public MyUpdateReqViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus=itemView.findViewById(R.id.tvMyUpdateReqStatusId);
            tvDate=itemView.findViewById(R.id.tvMyUpdateReqLayDateId);
            tvPreBre=itemView.findViewById(R.id.tvMyUpdateReqLayPreBreId);
            tvPreLunch=itemView.findViewById(R.id.tvMyUpdateReqLayPreLunchId);
            tvPreDinner=itemView.findViewById(R.id.tvMyUpdateReqPreDinnerId);
            tvPreDebit=itemView.findViewById(R.id.tvMyUpdateReqLayPreDebitId);
            tvCurrBre=itemView.findViewById(R.id.tvMyUpdateReqLayCurrBreId);
            tvCurrLunch=itemView.findViewById(R.id.tvMyUpdateReqCurrLunchId);
            tvCurrDinner=itemView.findViewById(R.id.tvMyUpdateReqLayCurrDinnerId);
            tvCurrDebit=itemView.findViewById(R.id.tvMyUpdateReqLayCurrDebitId);
        }
    }
}
