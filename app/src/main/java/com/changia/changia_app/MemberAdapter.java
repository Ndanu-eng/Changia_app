package com.changia.changia_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    private List<Member> memberList = new ArrayList<>();

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = memberList.get(position);

        holder.tvMemberName.setText(member.getName());
        holder.tvMemberPhone.setText(member.getPhone());

        if (member.isAdmin()) {
            holder.tvMemberRole.setText("Admin");
            holder.tvMemberRole.setTextColor(holder.itemView.getContext().getColor(R.color.primary_color));
        } else {
            holder.tvMemberRole.setText("Member");
            holder.tvMemberRole.setTextColor(holder.itemView.getContext().getColor(R.color.text_secondary));
        }

        if (member.hasPaid()) {
            holder.tvPaymentStatus.setText("✅ Paid");
            holder.tvPaymentStatus.setTextColor(holder.itemView.getContext().getColor(R.color.success));
        } else {
            holder.tvPaymentStatus.setText("⏳ Pending");
            holder.tvPaymentStatus.setTextColor(holder.itemView.getContext().getColor(R.color.warning));
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMemberName, tvMemberPhone, tvMemberRole, tvPaymentStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMemberName = itemView.findViewById(R.id.tv_member_name);
            tvMemberPhone = itemView.findViewById(R.id.tv_member_phone);
            tvMemberRole = itemView.findViewById(R.id.tv_member_role);
            tvPaymentStatus = itemView.findViewById(R.id.tv_payment_status);
        }
    }
}