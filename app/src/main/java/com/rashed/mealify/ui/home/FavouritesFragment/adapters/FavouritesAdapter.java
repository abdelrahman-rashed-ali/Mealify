package com.rashed.mealify.ui.home.FavouritesFragment.adapters;

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

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {

    private List<Meal> meals = new ArrayList<>();
    private final OnMealClickListener listener;

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    public FavouritesAdapter(OnMealClickListener listener) {
        this.listener = listener;
    }

    public void setList(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    public Meal getItem(int position) {
        return meals.get(position);
    }

    public void removeItem(int position) {
        meals.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Meal meal, int position) {
        meals.add(position, meal);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.tvName.setText(meal.getName());

        Glide.with(holder.itemView.getContext())
                .load(meal.getThumbUrl())
                .placeholder(R.drawable.ic_launcher_icon)
                .into(holder.imgThumbnail);

        holder.itemView.setOnClickListener(v -> listener.onMealClick(meal));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_meal_thumb);
            tvName = itemView.findViewById(R.id.tv_meal_name);
        }
    }
}