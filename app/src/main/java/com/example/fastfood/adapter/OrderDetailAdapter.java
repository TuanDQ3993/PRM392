package com.example.fastfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fastfood.Model.OrderDetail;
import com.example.fastfood.R;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {
    private Context context;
    private List<OrderDetail> orderDetails;
    private NumberFormat currencyFormatter;

    public OrderDetailAdapter(Context context, List<OrderDetail> orderDetails) {
        this.context = context;
        this.orderDetails = orderDetails;
        this.currencyFormatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetail detail = orderDetails.get(position);

        holder.tvItemName.setText(detail.getItemName());
        holder.tvItemPrice.setText(currencyFormatter.format(detail.getPrice()) + " VND");
        holder.tvItemQuantity.setText(String.format(Locale.getDefault(), "x%d", detail.getQuantity()));
        holder.tvItemSubtotal.setText(currencyFormatter.format(detail.getPrice() * detail.getQuantity()) + " VND");

        if (detail.getImageUrl() != null && !detail.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(detail.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.ivItemImage);
        }
    }

    @Override
    public int getItemCount() {
        return orderDetails != null ? orderDetails.size() : 0;
    }

    static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImage;
        TextView tvItemName;
        TextView tvItemPrice;
        TextView tvItemQuantity;
        TextView tvItemSubtotal;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvItemSubtotal = itemView.findViewById(R.id.tvItemSubtotal);
        }
    }
} 