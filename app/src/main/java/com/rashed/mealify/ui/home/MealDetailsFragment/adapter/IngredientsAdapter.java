package com.rashed.mealify.ui.home.MealDetailsFragment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.rashed.mealify.R;
import com.rashed.mealify.domain.model.Meal.Ingredient;
import java.util.ArrayList;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private List<Ingredient> ingredients = new ArrayList<>();

    public void setList(List<Ingredient> list) {
        this.ingredients = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient item = ingredients.get(position);

        holder.name.setText(item.name);
        holder.measure.setText(item.measure);

        String imageUrl = "https://www.themealdb.com/images/ingredients/" + item.name + "-small.png";

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_icon)
                .error(R.drawable.ic_launcher_icon)
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, measure;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_ingredient);
            name = itemView.findViewById(R.id.tv_ingredient_name);
            measure = itemView.findViewById(R.id.tv_ingredient_measure);
        }
    }
}