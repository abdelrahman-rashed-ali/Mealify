package com.rashed.mealify.ui.home.HomeFragment.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.rashed.mealify.R;
import com.rashed.mealify.domain.model.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private List<Category> list = new ArrayList<>();
    private int selectedPosition = 0;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public void setListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setList(List<Category> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public Category getSelectedCategory() {
        if (list != null && !list.isEmpty() && selectedPosition < list.size()) {
            return list.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category item = list.get(position);
        holder.name.setText(item.getName());

        Glide.with(holder.itemView.getContext())
                .load(item.getThumbUrl())
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_icon)
                .into(holder.img);

        if (position == selectedPosition) {
            holder.cardRoot.setStrokeColor(Color.parseColor("#00D949"));
            holder.cardRoot.setStrokeWidth(6);
            holder.overlay.setVisibility(View.GONE);
            holder.name.setTextColor(Color.parseColor("#00D949"));
            holder.name.setTypeface(null, android.graphics.Typeface.BOLD);

            holder.cardRoot.setScaleX(1.1f);
            holder.cardRoot.setScaleY(1.1f);
        } else {
            holder.cardRoot.setStrokeWidth(0);
            holder.overlay.setVisibility(View.VISIBLE);
            holder.name.setTextColor(Color.WHITE);
            holder.name.setTypeface(null, android.graphics.Typeface.NORMAL);

            holder.cardRoot.setScaleX(1.0f);
            holder.cardRoot.setScaleY(1.0f);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPos);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onCategoryClick(item);
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // CHANGE TYPE HERE:
        MaterialCardView cardRoot;
        ImageView img;
        View overlay;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // CAST HERE:
            cardRoot = itemView.findViewById(R.id.card_root);
            img = itemView.findViewById(R.id.img_category);
            overlay = itemView.findViewById(R.id.view_overlay);
            name = itemView.findViewById(R.id.tv_category_name);
        }
    }
}