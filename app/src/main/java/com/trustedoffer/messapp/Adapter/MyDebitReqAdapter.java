package com.trustedoffer.messapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trustedoffer.messapp.ModelClass.DebitReqModelClass;
import com.trustedoffer.messapp.R;

import java.util.List;
import java.util.Locale;

public class MyDebitReqAdapter extends RecyclerView.Adapter<MyDebitReqAdapter.MyDebitReqViewHolder> {
    private Context context;
    private List<DebitReqModelClass> list;

    public MyDebitReqAdapter(Context context, List<DebitReqModelClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyDebitReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.my_debit_req_layout,parent,false);
        return new MyDebitReqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDebitReqViewHolder holder, int position) {
        final DebitReqModelClass data=list.get(position);
        int day=data.getDay();
        int month=data.getMonth();
        int year=data.getYear();
        final double debit=data.getDebit();
        holder.tvDebit.setText("Debit : "+String.format(Locale.US, "%.2f", debit));
        holder.tvDate.setText("Date : "+Integer.toString(day)+"-"+Integer.toString(month)+"-"+Integer.toString(year));
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

    public class MyDebitReqViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStatus,tvDate,tvDebit;
        public MyDebitReqViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus=itemView.findViewById(R.id.tvMyDebitReqStatusId);
            tvDate=itemView.findViewById(R.id.tvMyDebitReqDateId);
            tvDebit=itemView.findViewById(R.id.tvMyDebitReqDebitId);
        }
    }
}
