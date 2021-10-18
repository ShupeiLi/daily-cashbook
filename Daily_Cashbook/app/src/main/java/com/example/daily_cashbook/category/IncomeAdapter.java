package com.example.daily_cashbook.category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.daily_cashbook.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder> {

    private Context mContext;
    private List<IncomeCategory> mIncomeCategory;
    private int selectedPos = RecyclerView.NO_POSITION;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView incomeImage;
        TextView incomeName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            incomeImage = (ImageView) view.findViewById(R.id.income_image);
            incomeName = (TextView) view.findViewById(R.id.income_name);
        }
    }

    public IncomeAdapter(List<IncomeCategory> incomeCategoryList) {
       mIncomeCategory = incomeCategoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.income_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                selectedPos = holder.getAdapterPosition();
                IncomeCategory incomeCategory = mIncomeCategory.get(selectedPos);
                SharedPreferences.Editor userEditor = mContext.getSharedPreferences("currentUser", Context.MODE_PRIVATE).edit();
                userEditor.putString("selectedItem", incomeCategory.getIncomeName());
                userEditor.apply();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        IncomeCategory incomeCategory = mIncomeCategory.get(position);
        holder.incomeName.setText(incomeCategory.getIncomeName());
        Glide.with(mContext).load(incomeCategory.getIncomeImageId()).into(holder.incomeImage);
    }

    @Override
    public int getItemCount() {
        return mIncomeCategory.size();
    }

}
