package com.changia.changia_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivTransactionIcon;
        private final TextView tvTransactionDescription;
        private final TextView tvTransactionTimestamp;
        private final TextView tvTransactionAmount;
        private final Context context;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            ivTransactionIcon = itemView.findViewById(R.id.iv_transaction_icon);
            tvTransactionDescription = itemView.findViewById(R.id.tv_transaction_description);
            tvTransactionTimestamp = itemView.findViewById(R.id.tv_transaction_timestamp);
            tvTransactionAmount = itemView.findViewById(R.id.tv_transaction_amount);
        }

        public void bind(Transaction transaction) {
            tvTransactionDescription.setText(transaction.getDescription());
            tvTransactionTimestamp.setText(transaction.getTimestamp());
            tvTransactionAmount.setText(transaction.getAmount());

            if (transaction.isCredit()) {
                // Credit transaction (e.g., Add Money)
                tvTransactionAmount.setTextColor(ContextCompat.getColor(context, R.color.green));
                ivTransactionIcon.setImageResource(R.drawable.ic_arrow_downward);
                ivTransactionIcon.setColorFilter(ContextCompat.getColor(context, R.color.green));
            } else {
                // Debit transaction (e.g., Withdraw, Contribution)
                tvTransactionAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
                ivTransactionIcon.setImageResource(R.drawable.ic_arrow_upward);
                ivTransactionIcon.setColorFilter(ContextCompat.getColor(context, R.color.red));
            }
        }
    }
}
