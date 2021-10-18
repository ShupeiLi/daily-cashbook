package com.example.daily_cashbook.category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.daily_cashbook.R;

import java.util.List;

public class ExpenditureAdapter extends RecyclerView.Adapter<ExpenditureAdapter.ViewHolder> {
    private Context mContext;
    private List<ExpenditureCategory> mExpenditureCategory;
    private int selectedPos = RecyclerView.NO_POSITION;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView expenditureImage;
        TextView expenditureName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            expenditureImage = (ImageView) view.findViewById(R.id.expenditure_image);
            expenditureName = (TextView) view.findViewById(R.id.expenditure_name);
        }
    }

    public ExpenditureAdapter(List<ExpenditureCategory> expenditureCategoryList) {
        mExpenditureCategory = expenditureCategoryList;
    }

    @NonNull
    @Override
    public ExpenditureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.expenditure_item, parent, false);
        ExpenditureAdapter.ViewHolder holder = new ExpenditureAdapter.ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                selectedPos = holder.getAdapterPosition();
                ExpenditureCategory expenditureCategory = mExpenditureCategory.get(selectedPos);
                SharedPreferences.Editor userEditor = mContext.getSharedPreferences("currentUser", Context.MODE_PRIVATE).edit();
                userEditor.putString("selectedItem2", expenditureCategory.getExpenditureName());
                userEditor.apply();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ExpenditureAdapter.ViewHolder holder, int position) {
        ExpenditureCategory expenditureCategory = mExpenditureCategory.get(position);
        holder.expenditureName.setText(expenditureCategory.getExpenditureName());
        Glide.with(mContext).load(expenditureCategory.getExpenditureImageId()).into(holder.expenditureImage);
    }

    @Override
    public int getItemCount() {
        return mExpenditureCategory.size();
    }

}
