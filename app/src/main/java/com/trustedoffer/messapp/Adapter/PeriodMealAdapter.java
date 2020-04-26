package com.trustedoffer.messapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trustedoffer.messapp.ModelClass.PeriodicalMealModelClass;
import com.trustedoffer.messapp.R;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PeriodMealAdapter extends RecyclerView.Adapter<PeriodMealAdapter.PeriodViewHolder> {
    private Context context;
    private List<PeriodicalMealModelClass> list;

    public PeriodMealAdapter(Context context, List<PeriodicalMealModelClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PeriodMealAdapter.PeriodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.period_meal_layout,parent,false);
        return new PeriodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeriodMealAdapter.PeriodViewHolder holder, int position) {
        PeriodicalMealModelClass data=list.get(position);
        String name=data.getName();
        int meal=data.getMeal();
        String date=data.getTime();
        showTime(date,holder.tvTime);

        holder.tvName.setText(name);
        holder.tvMeal.setText(Integer.toString(meal));
    }

    private void showTime(String date, TextView tvTime) {
        String[] dateSplit=new String[1];
        dateSplit=date.split(" ");
        String time=dateSplit[1];
        String[] times=new String[2];
        times=time.split(":");
        String hour=times[0];
        int hourTime=Integer.parseInt(hour);
        if (hourTime>=12){
            if (hourTime==12){
                tvTime.setText("12:"+times[1]+" PM");
            }
            else {
                int convertHour=(hourTime-12);
                if (convertHour<10){
                    tvTime.setText("0"+Integer.toString(convertHour)+":"+times[1]+" PM");
                }
                else {
                    tvTime.setText(Integer.toString(convertHour)+":"+times[1]+" PM");
                }
            }
        }
        else {
            if (hourTime==0){
                tvTime.setText("12:"+times[1]+" AM");
            }
            else {
                tvTime.setText(hour+":"+times[1]+" AM");
            }

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PeriodViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName,tvMeal,tvTime;
        public PeriodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvPeriodLayNameId);
            tvMeal=itemView.findViewById(R.id.tvPeriodLayMealId);
            tvTime=itemView.findViewById(R.id.tvPeriodLayTimeId);
        }
    }
}
