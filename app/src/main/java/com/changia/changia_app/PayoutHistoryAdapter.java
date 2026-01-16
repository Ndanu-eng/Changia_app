package com.changia.changia_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PayoutHistoryAdapter extends RecyclerView.Adapter<PayoutHistoryAdapter.ViewHolder> {

    private List<PayoutHistory> historyList;

    public PayoutHistoryAdapter(List<PayoutHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payout_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PayoutHistory item = historyList.get(position);

        holder.tvMonth.setText(item.getMonth());
        holder.tvRecipient.setText("To: " + item.getRecipient());
        holder.tvAmount.setText(item.getAmount());

        if (item.isPaid()) {
            holder.tvStatus.setText("✅ Paid");
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.success));
        } else {
            holder.tvStatus.setText("⏳ Pending");
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.warning));
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonth, tvRecipient, tvAmount, tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvRecipient = itemView.findViewById(R.id.tv_recipient);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}