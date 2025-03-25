package com.example.fastfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fastfood.Model.CartItem;
import com.example.fastfood.R;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;
    private CartItemListener listener;
    private boolean isEditable;

    public interface CartItemListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onItemDeleted(CartItem item);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
        this.isEditable = true;
    }

    public CartAdapter(Context context, List<CartItem> cartItems, boolean isEditable) {
        this.context = context;
        this.cartItems = cartItems;
        this.isEditable = isEditable;
        this.listener = null;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        
        holder.itemName.setText(item.getItemName());
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.itemPrice.setText(formatter.format(item.getPrice()));
        
        holder.quantityText.setText(String.valueOf(item.getQuantity()));
        
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                .load(item.getImageUrl())
                .centerCrop()
                .into(holder.itemImage);
        }

        // Hide control buttons if not editable
        if (!isEditable) {
            holder.decreaseButton.setVisibility(View.GONE);
            holder.increaseButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
            return;
        }

        holder.decreaseButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1 && listener != null) {
                listener.onQuantityChanged(item, item.getQuantity() - 1);
            }
        });

        holder.increaseButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityChanged(item, item.getQuantity() + 1);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemDeleted(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public void updateItems(List<CartItem> newItems) {
        this.cartItems = newItems;
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName;
        TextView itemPrice;
        TextView quantityText;
        ImageButton decreaseButton;
        ImageButton increaseButton;
        ImageButton deleteButton;

        CartViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 