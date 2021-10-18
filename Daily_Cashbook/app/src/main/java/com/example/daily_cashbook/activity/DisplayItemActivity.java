package com.example.daily_cashbook.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.daily_cashbook.MainActivity;
import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.Cashbook;
import com.example.daily_cashbook.dbutils.CashbookAdapter;
import com.squareup.picasso.Picasso;

import org.litepal.LitePal;

import java.util.List;

public class DisplayItemActivity extends AppCompatActivity {

    public static final String CATEGORY = "category";
    public static final String IN_OR_OUT = "in_or_out";
    public static final String MONEY = "money";
    public static final String TIME = "time";
    public static final String COMMENT = "comment";
    public static final String IMAGE1 = "image_1";
    public static final String IMAGE2 = "image_2";

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_cashbook_display);
        Intent intent = getIntent();
        String category = intent.getStringExtra(CATEGORY);
        String inOrOut = intent.getStringExtra(IN_OR_OUT);
        String money = intent.getStringExtra(MONEY);
        String time = intent.getStringExtra(TIME);
        String comment = intent.getStringExtra(COMMENT);
        String image1 = intent.getStringExtra(IMAGE1);
        String image2 = intent.getStringExtra(IMAGE2);

        // 设置标题栏
        Toolbar toolbar = (Toolbar)findViewById(R.id.home_page_cashbook_display_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ImageView categoryImage = (ImageView)findViewById(R.id.home_page_cashbook_display_category_image);
        TextView categoryText = (TextView)findViewById(R.id.home_page_cashbook_display_category_text);
        TextView moneyText = (TextView)findViewById(R.id.home_page_cashbook_display_amount);
        TextView inOrOutText = (TextView)findViewById(R.id.home_page_cashbook_display_inOrOut);
        TextView timeText = (TextView)findViewById(R.id.home_page_cashbook_display_time);
        TextView commentText = (TextView)findViewById(R.id.home_page_cashbook_display_comment);
        ImageView picView1 = (ImageView)findViewById(R.id.home_page_cashbook_display_img1);
        ImageView picView2 = (ImageView)findViewById(R.id.home_page_cashbook_display_img2);
        Button deleteButton = (Button)findViewById(R.id.home_page_cashbook_delete);

        categoryText.setText(category);
        moneyText.setText(money);
        inOrOutText.setText("类型：    " + inOrOut);
        timeText.setText("时间：   " + time);
        commentText.setText("备注：    " + comment);
        if (!image1.equals("")) {
            Picasso.get().load(Uri.parse(image1)).into(picView1);
        }
        if (!image2.equals("")) {
            Picasso.get().load(Uri.parse(image2)).into(picView2);
        }
        Glide.with(this).load(CashbookAdapter.viewImageId(category, inOrOut)).into(categoryImage);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LitePal.deleteAll(Cashbook.class, "category=? and money=? and time=? and comment=?", category,  money, time, comment);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
