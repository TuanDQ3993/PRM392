package com.example.fastfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fastfood.Model.Order;
import com.example.fastfood.R;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orders;
    private NumberFormat currencyFormatter;
    private SimpleDateFormat dateFormat;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
        this.currencyFormatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        
        holder.tvOrderId.setText("Đơn hàng #" + order.getOrderId());
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvOrderTotal.setText(currencyFormatter.format(order.getTotalPrice()) + " VND");
        holder.tvOrderDate.setText(dateFormat.format(order.getOrderDate()));

        // Set status background based on statusId
        int statusBackground;
        switch (order.getStatusId()) {
            case 1: // Pending
                statusBackground = R.drawable.status_pending_background;
                break;
            case 2: // Processing
                statusBackground = R.drawable.status_processing_background;
                break;
            case 3: // Delivered
                statusBackground = R.drawable.status_delivered_background;
                break;
            case 4: // Canceled
                statusBackground = R.drawable.status_canceled_background;
                break;
            default:
                statusBackground = R.drawable.status_pending_background;
                break;
        }
        holder.tvOrderStatus.setBackgroundResource(statusBackground);

        // Set up nested RecyclerView for order details
        OrderDetailAdapter detailAdapter = new OrderDetailAdapter(context, order.getOrderDetails());
        holder.rvOrderDetails.setLayoutManager(new LinearLayoutManager(context));
        holder.rvOrderDetails.setAdapter(detailAdapter);
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId;
        TextView tvOrderStatus;
        TextView tvOrderTotal;
        TextView tvOrderDate;
        RecyclerView rvOrderDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            rvOrderDetails = itemView.findViewById(R.id.rvOrderDetails);
        }
    }
} 