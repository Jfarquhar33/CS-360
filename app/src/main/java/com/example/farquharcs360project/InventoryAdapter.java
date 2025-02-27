package com.example.farquharcs360project;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.content.ContextCompat;


public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {
    private Context context;
    private List<Item> itemList;
    private DatabaseHelper dbHelper;

    // Constructor
    public InventoryAdapter(Context context, List<Item> items, DatabaseHelper dbHelper) {
        this.context = context;
        this.itemList = itemList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Item item = itemList.get(position);
        // Set the item name and stock count
        holder.itemName.setText(item.getName());
        holder.stockCount.setText(String.valueOf(item.getStock()));

        // Increase button logic
        holder.buttonIncrease.setOnClickListener(view -> {
            int newStock = item.getStock() + 1;
            if (dbHelper.updateStock(item.getId(), newStock)) {
                item.setStock(newStock);
                notifyItemChanged(position);
            } else {
                Toast.makeText(view.getContext(), "Error updating stock", Toast.LENGTH_SHORT).show();
            }
        });

        // Decrease button logic
        holder.buttonDecrease.setOnClickListener(view -> {
            int newStock = item.getStock() - 1;
            if (newStock >= 0) {
                item.setStock(newStock);
                dbHelper.updateStock(item.getId(), newStock);
                notifyItemChanged(position);

                // If stock hits zero, trigger SMS
                if (newStock == 0) {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).sendLowStockSms(item.getName());
                    }
                }
            }
        });

       /* Old Decrease button logic in case I need to adjust
       holder.buttonDecrease.setOnClickListener(view -> {
            if (item.getStock() > 0) {
                int newStock = item.getStock() - 1;
                if (dbHelper.updateStock(item.getId(), newStock)) {
                    item.setStock(newStock);
                    notifyItemChanged(position);
                } else {
                    Toast.makeText(view.getContext(), "Error updating stock", Toast.LENGTH_SHORT).show();
                }
            }
        }); */

        // Delete button logic
        holder.buttonDelete.setOnClickListener(view -> {
            // Check if deletion was successful
            if (dbHelper.deleteItem(item.getId())) {
                // Remove the item from the item list
                itemList.remove(position);
                // Notify adapter of removal
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, itemList.size());
            } else {
                Toast.makeText(view.getContext(), "Error deleting item", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        if (itemList == null) {
            return 0;
        }
        return itemList.size();
    }

    public void updateItems(List<Item> newItems) {
        this.itemList = newItems;
        notifyDataSetChanged();
    }

    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, stockCount;
        Button buttonIncrease, buttonDecrease, buttonDelete;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            itemName = itemView.findViewById(R.id.itemName);
            stockCount = itemView.findViewById(R.id.stockCount);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
            buttonDelete = itemView.findViewById(R.id.buttonDelete); // Add reference to the delete button
        }
    }
}
