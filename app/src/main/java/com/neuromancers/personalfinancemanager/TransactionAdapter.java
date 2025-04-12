package com.neuromancers.personalfinancemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.tvType.setText(transaction.getType() + " - " + transaction.getAccountName());
        holder.tvAmount.setText("₹ " + transaction.getAmount());
        holder.tvDescription.setText(transaction.getDescription() + " (With: " + transaction.getPerson() + ")");
        holder.tvDateTime.setText(transaction.getDatetime());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvAmount, tvDescription, tvDateTime;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tv_type);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvDateTime = itemView.findViewById(R.id.tv_datetime);
        }
    }
}
