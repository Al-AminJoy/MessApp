package com.trustedoffer.messapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trustedoffer.messapp.ModelClass.PeriodicalMealModelClass;
import com.trustedoffer.messapp.R;

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
        holder.tvName.setText(name);
        holder.tvMeal.setText(Integer.toString(meal));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PeriodViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName,tvMeal;
        public PeriodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvPeriodLayNameId);
            tvMeal=itemView.findViewById(R.id.tvPeriodLayMealId);
        }
    }
}
