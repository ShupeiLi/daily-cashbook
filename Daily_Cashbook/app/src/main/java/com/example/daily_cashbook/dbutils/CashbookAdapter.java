package com.example.daily_cashbook.dbutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.daily_cashbook.MainActivity;
import com.example.daily_cashbook.R;
import com.example.daily_cashbook.activity.DisplayItemActivity;
import com.example.daily_cashbook.fragment.KeepAccountFragment;

import java.util.List;

public class CashbookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Cashbook> cashbookList;
    private Context mContext;

    public CashbookAdapter(List<Cashbook> cashbookList) {
        this.cashbookList = cashbookList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == CashbookCommon.VIEW_TYPE_GROUP) {
            ViewGroup group = (ViewGroup) inflater.inflate(R.layout.cashbook_home_page_date_group, parent, false);
            GroupViewHolder groupViewHolder = new GroupViewHolder(group);
            return groupViewHolder;
        } else if (viewType == CashbookCommon.VIEW_TYPE_CASHBOOK) {
            ViewGroup group = (ViewGroup) inflater.inflate(R.layout.cashbook_home_page_item, parent, false);
            CashbookViewHolder cashbookViewHolder = new CashbookViewHolder(group);
            cashbookViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = cashbookViewHolder.getAdapterPosition();
                    Cashbook cashbook = cashbookList.get(position);
                }
            });
            return cashbookViewHolder;
        } else {
            ViewGroup group = (ViewGroup) inflater.inflate(R.layout.cashbook_home_page_date_group, parent, false);
            GroupViewHolder groupViewHolder = new GroupViewHolder(group);
            return groupViewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return cashbookList.get(position).getViewType();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupViewHolder) {
            GroupViewHolder groupViewHolder = (GroupViewHolder)holder;
            groupViewHolder.dateGroup.setText(cashbookList.get(position).getTime());
        } else if (holder instanceof CashbookViewHolder) {
            Cashbook cashbook = cashbookList.get(position);
            CashbookViewHolder cashbookViewHolder = (CashbookViewHolder)holder;
            Glide.with(mContext).load(viewImageId(cashbook.getCategory(), cashbook.getInOrOut())).into(cashbookViewHolder.categoryImage);
            cashbookViewHolder.categoryName.setText(cashbook.getCategory());
            if (cashbook.getInOrOut().equals("收入")) {
                cashbookViewHolder.money.setText("+" + cashbook.getMoney());
                cashbookViewHolder.money.setTextColor(Color.GRAY);
            } else {
                cashbookViewHolder.money.setText("-" + cashbook.getMoney());
                cashbookViewHolder.money.setTextColor(Color.DKGRAY);
            }
            cashbookViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = cashbookViewHolder.getAdapterPosition();
                    Cashbook cashbook = cashbookList.get(position);
                    Intent intent = new Intent(mContext, DisplayItemActivity.class);
                    intent.putExtra(DisplayItemActivity.CATEGORY, cashbook.getCategory());
                    intent.putExtra(DisplayItemActivity.IN_OR_OUT, cashbook.getInOrOut());
                    intent.putExtra(DisplayItemActivity.MONEY, cashbook.getMoney());
                    intent.putExtra(DisplayItemActivity.TIME, cashbook.getTime());
                    intent.putExtra(DisplayItemActivity.COMMENT, cashbook.getComment());
                    intent.putExtra(DisplayItemActivity.IMAGE1, cashbook.getImage1());
                    intent.putExtra(DisplayItemActivity.IMAGE2, cashbook.getImage2());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cashbookList.size();
    }

    public static int viewImageId(String viewCategory, String viewInOrOut) {
        String[] outCategory = {"餐饮", "零食", "水果", "日用", "交通", "住房", "水电", "服饰", "购物",
                "娱乐", "电影", "门票", "数码", "美妆", "理发", "医疗", "红包", "母婴", "宠物", "话费",
                "礼物", "学习", "保险", "捐赠", "恋爱", "亏损", "其他"};
        Integer[] outImageId = {R.drawable.meal_1, R.drawable.snack_2, R.drawable.fruit_3, R.drawable.daily_4,
                R.drawable.transportation_5, R.drawable.house_6, R.drawable.water_electricity_7, R.drawable.cloth_8,
                R.drawable.shopping_9, R.drawable.entertain_10, R.drawable.movie_11, R.drawable.ticket_12,
                R.drawable.electrion_13, R.drawable.makeup_14, R.drawable.haircut_15, R.drawable.medicine_16,
                R.drawable.redbag_17, R.drawable.maternal_18, R.drawable.pet_19, R.drawable.phone_charge_20,
                R.drawable.gift_21, R.drawable.learning_22, R.drawable.insurance_23, R.drawable.donation_24,
                R.drawable.love_25, R.drawable.loss_26, R.drawable.other_27};
        String[] inCategory = {"工资", "奖金", "收款", "投资收益", "生活费", "零花钱", "外快", "退款",
                "红包", "意外所得", "其他"};
        Integer[] inImageId = {R.drawable.salary_1, R.drawable.bonus_2, R.drawable.checkin_3, R.drawable.invest_4,
                R.drawable.life_expense_5, R.drawable.pocket_money_6, R.drawable.extra_7, R.drawable.refund_8,
                R.drawable.redbag_9, R.drawable.accident_10, R.drawable.other_11};
        if (viewInOrOut.equals("支出")) {
            for (int i = 0; i < outCategory.length; i++) {
                if (viewCategory.equals(outCategory[i])) {
                    return outImageId[i];
                }
            }
        } else {
            for (int i = 0; i < inCategory.length; i++) {
                if (viewCategory.equals(inCategory[i])) {
                    return inImageId[i];
                }
            }
        }
        return 0;
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView dateGroup;
        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            dateGroup = (TextView) itemView.findViewById(R.id.home_page_date_group);
        }
    }

    static class CashbookViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;
        TextView money;

        public CashbookViewHolder(@NonNull View view) {
            super(view);
            categoryImage = (ImageView) view.findViewById(R.id.cashbook_category_image);
            categoryName = (TextView) view.findViewById(R.id.cashbook_category_name);
            money = (TextView) view.findViewById(R.id.cashbook_money);
        }

    }

}
