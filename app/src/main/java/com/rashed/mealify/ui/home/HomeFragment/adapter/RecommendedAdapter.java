package com.rashed.mealify.ui.home.HomeFragment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rashed.mealify.R;
import com.rashed.mealify.domain.model.Meal;

import java.util.ArrayList;
import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> {

    // 1. Define the Listener Interface
    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    private List<Meal> meals = new ArrayList<>();
    private OnMealClickListener listener;

    // 2. Public Setter for the Listener
    public void setListener(OnMealClickListener listener) {
        this.listener = listener;
    }

    public void setList(List<Meal> list) {
        this.meals = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ensure you have a layout file named 'item_meal_card.xml' or similar
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommended_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meal meal = meals.get(position);

        holder.tvName.setText(meal.getName());

        Glide.with(holder.itemView.getContext())
                .load(meal.getThumbUrl())
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background) // Add a placeholder drawable
                .into(holder.imgThumb);

        // 3. Trigger the Listener on Click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMealClick(meal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ensure these IDs match your 'item_meal_card.xml'
            imgThumb = itemView.findViewById(R.id.img_meal);
            tvName = itemView.findViewById(R.id.tv_meal_name);
        }
    }
}