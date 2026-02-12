package com.rashed.mealify.ui.home.SearchFragment.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.rashed.mealify.R;
import com.rashed.mealify.domain.model.Meal;
import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<Meal> meals = new ArrayList<>();
    private final OnMealClickListener listener;

    public interface OnMealClickListener {
        void onClick(Meal meal);
    }

    public SearchAdapter(OnMealClickListener listener) {
        this.listener = listener;
    }

    public void setList(List<Meal> list) {
        this.meals = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(meals.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvName;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.img_meal_thumb);
            tvName = itemView.findViewById(R.id.tv_meal_name);
            card = itemView.findViewById(R.id.card_meal);
        }

        public void bind(Meal meal, OnMealClickListener listener) {
            tvName.setText(meal.getName());
            Glide.with(itemView.getContext())
                    .load(meal.getThumbUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgThumb);

            card.setOnClickListener(v -> listener.onClick(meal));
        }
    }
}