package com.example.daily_cashbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.daily_cashbook.activity.ChartActivity;
import com.example.daily_cashbook.activity.DisplayItemActivity;
import com.example.daily_cashbook.activity.KeepAccountActivity;
import com.example.daily_cashbook.activity.LoginActivity;
import com.example.daily_cashbook.activity.PersonActivity;
import com.example.daily_cashbook.dbutils.Cashbook;
import com.example.daily_cashbook.dbutils.CashbookAdapter;
import com.example.daily_cashbook.dbutils.CashbookCommon;
import com.example.daily_cashbook.dbutils.LinearLayoutManagerWithSmoothScroller;
import com.example.daily_cashbook.dbutils.UserInfo;
import com.example.daily_cashbook.entity.User;
import com.example.daily_cashbook.fragment.KeepAccountFragment;
import com.example.daily_cashbook.fragment.KeepAccountIncomeFragment;
import com.example.daily_cashbook.fragment.LoginFragment;
import com.example.daily_cashbook.fragment.MyFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.litepal.LitePal;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

// 阿里图标库：https://www.iconfont.cn/collections/index?spm=a313x.7781069.1998910419.7&type=4
public class MainActivity extends AppCompatActivity {
    private Toolbar txt_toolbar;
    private DrawerLayout mDrawerLayout;
    private UserInfo userInfo;
    private String currentUser;
    private FragmentManager fragmentManager;
    private List<Cashbook> cashbookList;
    private CashbookAdapter cashbookAdapter;
    private RecyclerView recyclerView;
    private TextView calendarDisplay;
    private TextView expenditureDisplay;
    private TextView incomeDisplay;
    private TextView remainDisplay;
    private String[] result;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        SharedPreferences pref = getSharedPreferences("currentUser", MODE_PRIVATE);
        currentUser = pref.getString("user", "");
        userInfo = LitePal.where("userName=?", currentUser).findFirst(UserInfo.class);
        bindViews();

        // 选择日期：天
        calendarDisplay = (TextView)findViewById(R.id.home_page_current_date);
        Button calendarButtonDay = (Button)findViewById(R.id.home_page_day);
        calendarDisplay.setText("时间：所有时间");
        MaterialDatePicker.Builder<Long> materialDateBuilder1 = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder1.setTitleText("选择日期");
        final MaterialDatePicker<Long> materialDatePicker1 = materialDateBuilder1.build();
        calendarButtonDay.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDatePicker1.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                });

        materialDatePicker1.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        calendarDisplay.setText("时间：" + materialDatePicker1.getHeaderText());
                        cashbookList = LitePal.where("userName=? and time=?", currentUser, materialDatePicker1.getHeaderText()).find(Cashbook.class);
                        cashbookList = CashbookCommon.sortList(cashbookList);
                        cashbookList = CashbookCommon.addDates(cashbookList);
                        cashbookAdapter = new CashbookAdapter(cashbookList);
                        recyclerView.setAdapter(cashbookAdapter);
                        result = CashbookCommon.moneyCalculator(cashbookList);
                        expenditureDisplay.setText("支出：" + result[0]);
                        incomeDisplay.setText("收入：" + result[1]);
                        remainDisplay.setText("结余：" + result[2]);
                    }
                });

        // 选择日期：月
        Button calendarButtonMonth = (Button)findViewById(R.id.home_page_month);
        calendarButtonMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar today = Calendar.getInstance();
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(MainActivity.this,
                        new MonthPickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                calendarDisplay.setText("时间：" + selectedYear + "年" + (selectedMonth + 1) + "月");
                                cashbookList = CashbookCommon.selectMonthList(initCashbook(), selectedYear + "年" + (selectedMonth + 1) + "月");
                                cashbookList = CashbookCommon.sortList(cashbookList);
                                cashbookList = CashbookCommon.addDates(cashbookList);
                                cashbookAdapter = new CashbookAdapter(cashbookList);
                                recyclerView.setAdapter(cashbookAdapter);
                                result = CashbookCommon.moneyCalculator(cashbookList);
                                expenditureDisplay.setText("支出：" + result[0]);
                                incomeDisplay.setText("收入：" + result[1]);
                                remainDisplay.setText("结余：" + result[2]);
                            }
                        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

                builder.setActivatedMonth(Calendar.JULY)
                        .setMinYear(1990)
                        .setActivatedYear(today.get(Calendar.YEAR))
                        .setMaxYear(2030)
                        .setMinMonth(Calendar.FEBRUARY)
                        .setTitle("选择月份")
                        .build().show();
            }
        });

        // 选择日期：年
        Button calendarButtonYear = (Button)findViewById(R.id.home_page_year);
        calendarButtonYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar today = Calendar.getInstance();
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(MainActivity.this,
                        new MonthPickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                calendarDisplay.setText("时间：" + selectedYear + "年");
                                cashbookList = CashbookCommon.selectYearList(initCashbook(), selectedYear + "年");
                                cashbookList = CashbookCommon.sortList(cashbookList);
                                cashbookList = CashbookCommon.addDates(cashbookList);
                                cashbookAdapter = new CashbookAdapter(cashbookList);
                                recyclerView.setAdapter(cashbookAdapter);
                                result = CashbookCommon.moneyCalculator(cashbookList);
                                expenditureDisplay.setText("支出：" + result[0]);
                                incomeDisplay.setText("收入：" + result[1]);
                                remainDisplay.setText("结余：" + result[2]);
                            }
                        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

                builder.setActivatedMonth(Calendar.JULY)
                        .setMinYear(1990)
                        .setActivatedYear(today.get(Calendar.YEAR))
                        .setMaxYear(2030)
                        .setMinMonth(Calendar.FEBRUARY)
                        .setTitle("选择年份")
                        .showYearOnly()
                        .build().show();
            }
        });

        // 选择日期：周期
        Button calendarButtonPeriod = (Button)findViewById(R.id.home_page_period);
        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder4 = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder4.setTitleText("选择范围");
        final MaterialDatePicker<Pair<Long, Long>> materialDatePicker4 = materialDateBuilder4.build();
        calendarButtonPeriod.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDatePicker4.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                });

        materialDatePicker4.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        calendarDisplay.setText("时间：" + materialDatePicker4.getHeaderText());
                        cashbookList = CashbookCommon.selectRangeList(initCashbook(), materialDatePicker4.getHeaderText().split(" - "));
                        cashbookList = CashbookCommon.sortList(cashbookList);
                        cashbookList = CashbookCommon.addDates(cashbookList);
                        cashbookAdapter = new CashbookAdapter(cashbookList);
                        recyclerView.setAdapter(cashbookAdapter);
                        result = CashbookCommon.moneyCalculator(cashbookList);
                        expenditureDisplay.setText("支出：" + result[0]);
                        incomeDisplay.setText("收入：" + result[1]);
                        remainDisplay.setText("结余：" + result[2]);
                    }
                });

        // 清除日期
        Button calendarButtonClear = (Button)findViewById(R.id.home_page_clear_time);
        calendarButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarDisplay.setText("时间：所有时间");
                cashbookList = initCashbook();
                cashbookList = CashbookCommon.sortList(cashbookList);
                cashbookList = CashbookCommon.addDates(cashbookList);
                cashbookAdapter = new CashbookAdapter(cashbookList);
                recyclerView.setAdapter(cashbookAdapter);
                expenditureDisplay.setText("支出：" + result[0]);
                incomeDisplay.setText("收入：" + result[1]);
                remainDisplay.setText("结余：" + result[2]);
            }
        });

        // RecycleView
        // 初始化
        cashbookList = initCashbook();
        recyclerView = (RecyclerView) findViewById(R.id.home_page_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManagerWithSmoothScroller(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        cashbookList = CashbookCommon.sortList(cashbookList);
        cashbookList = CashbookCommon.addDates(cashbookList);
        cashbookAdapter = new CashbookAdapter(cashbookList);
        recyclerView.setAdapter(cashbookAdapter);

        // 设置计算页面
        expenditureDisplay = (TextView)findViewById(R.id.home_page_text_income);
        incomeDisplay = (TextView)findViewById(R.id.home_page_text_expenditure);
        remainDisplay = (TextView)findViewById(R.id.home_page_text_remain);
        result = CashbookCommon.moneyCalculator(cashbookList);
        expenditureDisplay.setText("支出：" + result[0]);
        incomeDisplay.setText("收入：" + result[1]);
        remainDisplay.setText("结余：" + result[2]);
    }

    @SuppressLint("SetTextI18n")
    private void bindViews() {
        txt_toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(txt_toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        View navHeader = navView.getHeaderView(0);
        CircleImageView userImage = (CircleImageView)navHeader.findViewById(R.id.menu_header_icon_image);
        if (userInfo.getProfile() != null) {
            String imagePath = userInfo.getProfile();
            if (!imagePath.equals("")) {
                Glide.with(getApplicationContext()).load(Uri.parse(imagePath)).into(userImage);
            }
        }
        ((TextView)navHeader.findViewById(R.id.menu_header_username)).setText(userInfo.getUserName());
        ((TextView)navHeader.findViewById(R.id.menu_header_mail)).setText("e-mail: " + userInfo.getEmailAddress());
        ((TextView)navHeader.findViewById(R.id.menu_header_tel)).setText("tel: " + userInfo.getTelNumber());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        }
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_chart:
                        Intent intent1 = new Intent(getApplicationContext(), ChartActivity.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.nav_account:
                        Intent intent3 = new Intent(getApplicationContext(), PersonActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                    case R.id.nav_logout:
                        Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                }
                return true;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), KeepAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    private List<Cashbook> initCashbook() {
        return LitePal.where("userName=?", currentUser).find(Cashbook.class);
    }

}
