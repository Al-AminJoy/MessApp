package com.trustedoffer.messapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trustedoffer.messapp.ModelClass.UserDataModelClass;
import com.trustedoffer.messapp.R;

import java.util.List;
import java.util.Locale;

public class DailyStatAdapter extends RecyclerView.Adapter<DailyStatAdapter.DailyStatViewHolder> {
    private Context context;
    private List<UserDataModelClass> list;

    public DailyStatAdapter(Context context, List<UserDataModelClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DailyStatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.daily_overview_layout, parent, false);
        return new DailyStatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyStatViewHolder holder, int position) {
        UserDataModelClass data = list.get(position);
        int date = data.getDay();
        int breakfast = data.getBreakfast();
        int lunch = data.getLunch();
        int dinner = data.getDinner();
        double debit = data.getDebit();
        holder.tvDate.setText(Integer.toString(date));
        holder.tvBreakfast.setText(Integer.toString(breakfast));
        holder.tvLunch.setText(Integer.toString(lunch));
        holder.tvDinner.setText(Integer.toString(dinner));
        holder.tvDebit.setText(String.format(Locale.US, "%.2f", debit));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DailyStatViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvBreakfast, tvLunch, tvDinner, tvDebit;

        public DailyStatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBreakfast = itemView.findViewById(R.id.tvDailyMealBreakFastId);
            tvDate = itemView.findViewById(R.id.tvDailyMealDateId);
            tvLunch = itemView.findViewById(R.id.tvDailyMealLunchId);
            tvDinner = itemView.findViewById(R.id.tvDailyMealDinnerId);
            tvDebit = itemView.findViewById(R.id.tvDailyMealDebitId);
        }
    }
}
