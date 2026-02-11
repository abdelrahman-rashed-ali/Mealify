package com.rashed.mealify.ui.home.PlannerFragment.adapters;

import android.content.Context;
import android.graphics.Color;
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
import com.rashed.mealify.domain.usecases.meal.ManagePlanUseCase;

import java.util.ArrayList;
import java.util.List;

public class PlannerAdapter extends RecyclerView.Adapter<PlannerAdapter.PlannerViewHolder> {

    private List<ManagePlanUseCase.PlannedMealDomain> items = new ArrayList<>();
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ManagePlanUseCase.PlannedMealDomain item);
    }

    public PlannerAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setData(List<ManagePlanUseCase.PlannedMealDomain> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public List<ManagePlanUseCase.PlannedMealDomain> getData() {
        return items;
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(int position, ManagePlanUseCase.PlannedMealDomain item) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public PlannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_planned_meal, parent, false);
        return new PlannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlannerViewHolder holder, int position) {
        ManagePlanUseCase.PlannedMealDomain item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PlannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvName, tvCategory, tvType;
        CardView cardView;

        public PlannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.img_planned_thumb);
            tvName = itemView.findViewById(R.id.tv_planned_name);
            tvCategory = itemView.findViewById(R.id.tv_planned_category);
            tvType = itemView.findViewById(R.id.tv_planned_type);
            cardView = itemView.findViewById(R.id.card_planned_meal);
        }

        void bind(ManagePlanUseCase.PlannedMealDomain item, OnItemClickListener listener) {
            tvName.setText(item.meal.getName());
            tvCategory.setText(item.meal.getArea() + " | " + item.meal.getCategory());
            tvType.setText(item.type);

            int color;
            switch (item.type) {
                case "Breakfast": color = Color.parseColor("#FF9800"); break;
                case "Lunch": color = Color.parseColor("#2196F3"); break;
                case "Dinner": color = Color.parseColor("#3F51B5"); break;
                default: color = Color.GRAY;
            }
            tvType.setTextColor(color);

            Glide.with(itemView.getContext())
                    .load(item.meal.getThumbUrl())
                    .centerCrop()
                    .into(imgThumb);

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}