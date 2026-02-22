package com.changia.changia_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<Group> groupList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public GroupAdapter(List<Group> groupList, Context context) {
        this.groupList = groupList;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Use the position parameter directly - this is safe in onBindViewHolder
        Group group = groupList.get(position);

        // Set group name
        holder.tvGroupName.setText(group.getName());

        // Format member count
        String memberText = group.getCurrentMembers() + "/" + group.getTotalMembers() + " members";
        holder.tvMemberCount.setText(memberText);

        // Format currency amounts
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "KE"));
        String currentFormatted = formatter.format(group.getCurrentBalance()).replace("KSh", "KES");
        String targetFormatted = formatter.format(group.getTargetBalance()).replace("KSh", "KES");

        String amountText = currentFormatted + " of " + targetFormatted;
        holder.tvAmount.setText(amountText);

        // Calculate and set percentage
        int percentage = (int) group.getProgressPercentage();
        holder.tvPercentage.setText(percentage + "%");

        // Set progress bar
        if (holder.progressBar != null) {
            holder.progressBar.setProgress(percentage);
        }

        // Set lock status
        if (group.isLocked()) {
            holder.tvLockStatus.setText("🔒 Locked");
            holder.tvLockStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        } else {
            holder.tvLockStatus.setText("🔓 Unlocked");
            holder.tvLockStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        }

        // Hide extra fields
        holder.tvMonthlyContribution.setVisibility(View.GONE);
        holder.tvNextPayout.setVisibility(View.GONE);

        // Set click listener using final position variable
        final int adapterPosition = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    // Use the saved position
                    listener.onItemClick(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvGroupName;
        TextView tvMemberCount;
        TextView tvAmount;
        TextView tvPercentage;
        TextView tvLockStatus;
        TextView tvMonthlyContribution;
        TextView tvNextPayout;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
            tvMemberCount = itemView.findViewById(R.id.tv_member_count);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
            tvLockStatus = itemView.findViewById(R.id.tv_lock_status);
            tvMonthlyContribution = itemView.findViewById(R.id.tv_monthly_contribution);
            tvNextPayout = itemView.findViewById(R.id.tv_next_payout);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}