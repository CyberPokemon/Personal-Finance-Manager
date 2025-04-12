package com.neuromancers.personalfinancemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context context;
    private List<Transaction> transactionList;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.amount.setText("₹" + transaction.getAmount());
        holder.type.setText(transaction.getType());
        holder.type.setTextColor(transaction.getType().equalsIgnoreCase("Incoming") ?
                context.getResources().getColor(android.R.color.holo_green_dark) :
                context.getResources().getColor(android.R.color.holo_red_dark));
        holder.dateTime.setText(transaction.getDatetime());
        holder.description.setText(transaction.getDescription());
        holder.party.setText((transaction.getType().equalsIgnoreCase("Incoming") ? "From: " : "To: ") + transaction.getPerson());
        holder.account.setText((transaction.getType().equalsIgnoreCase("Incoming") ? "To: " : "From: ") + transaction.getAccount());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView amount, type, dateTime, description, party, account;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.transaction_amount);
            type = itemView.findViewById(R.id.transaction_type);
            dateTime = itemView.findViewById(R.id.transaction_datetime);
            description = itemView.findViewById(R.id.transaction_description);
            party = itemView.findViewById(R.id.transaction_party);
            account = itemView.findViewById(R.id.transaction_account);
        }
    }
}
