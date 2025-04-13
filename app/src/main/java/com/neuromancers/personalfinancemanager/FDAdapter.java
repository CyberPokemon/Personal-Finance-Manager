package com.neuromancers.personalfinancemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FDAdapter extends RecyclerView.Adapter<FDAdapter.FDViewHolder> {

    private List<FDModel> fdList;

    public FDAdapter(List<FDModel> fdList) {
        this.fdList = fdList;
    }

    @NonNull
    @Override
    public FDViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fd, parent, false);
        return new FDViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FDViewHolder holder, int position) {
        FDModel fd = fdList.get(position);
        holder.txtInfo.setText("Principal: ₹" + fd.getPrincipal() +
                "\nRate: " + fd.getRate() + "%" +
                "\nMonths: " + fd.getTimeMonths() +
                "\nMaturity: ₹" + fd.getMaturityAmount() +
                "\nDate: " + fd.getStartDate() + " → " + fd.getMaturityDate());
    }

    @Override
    public int getItemCount() {
        return fdList.size();
    }

    static class FDViewHolder extends RecyclerView.ViewHolder {
        TextView txtInfo;

        public FDViewHolder(@NonNull View itemView) {
            super(itemView);
            txtInfo = itemView.findViewById(R.id.txt_fd_info);
        }
    }
}