package com.changia.changia_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RotationAdapter extends RecyclerView.Adapter<RotationAdapter.ViewHolder> {

    private List<RotationItem> rotationList;

    public RotationAdapter(List<RotationItem> rotationList) {
        this.rotationList = rotationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rotation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RotationItem item = rotationList.get(position);

        holder.tvMonth.setText(item.getMonth());
        holder.tvMemberName.setText(item.getMemberName());
        holder.tvAmount.setText(item.getAmount());

        if (item.isCompleted()) {
            holder.tvStatus.setText("✅ Completed");
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.success));
        } else if (position == 1) { // Current
            holder.tvStatus.setText("⏳ Current");
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.primary_color));
        } else {
            holder.tvStatus.setText("📅 Upcoming");
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.text_secondary));
        }
    }

    @Override
    public int getItemCount() {
        return rotationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonth, tvMemberName, tvAmount, tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvMemberName = itemView.findViewById(R.id.tv_member_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}