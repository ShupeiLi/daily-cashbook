package com.example.daily_cashbook.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.daily_cashbook.CustomMarkerView;
import com.example.daily_cashbook.MainActivity;
import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.Cashbook;
import com.example.daily_cashbook.dbutils.CashbookAdapter;
import com.example.daily_cashbook.dbutils.CashbookCommon;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.litepal.LitePal;
import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ChartActivity extends AppCompatActivity {
    private BarChart barChartIncome;
    private PieChart pieChartIncome;
    private BarChart barChartExpenditure;
    private PieChart pieChartExpenditure;
    private List<Cashbook> cashbookList;
    private String currentUser;
    private ArrayList<String> xLabel;
    private TextView timeDisplay;
    private TextView incomeSummary;
    private TextView expenditureSummary;
    private TextView remainSummary;
    private TextView countSummary;
    private String[] result;
    private Button selectMonth;
    private Button selectYear;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chart_main);
        SharedPreferences pref = getSharedPreferences("currentUser", MODE_PRIVATE);
        currentUser = pref.getString("user", "");
        timeDisplay = findViewById(R.id.chart_time_display);
        incomeSummary = findViewById(R.id.chart_summary_income);
        expenditureSummary = findViewById(R.id.chart_summary_expenditure);
        remainSummary = findViewById(R.id.chart_summary_remain);
        countSummary = findViewById(R.id.chart_summary_count);
        selectMonth = findViewById(R.id.chart_month);
        selectYear = findViewById(R.id.chart_year);
        Toolbar toolbar = (Toolbar)findViewById(R.id.chart_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        LocalDate currentDate = LocalDate.now();
        String currentMonth = currentDate.getYear() + "年" + currentDate.getMonthValue() + "月";
        cashbookList = initCashbook();
        cashbookList = CashbookCommon.selectMonthList(cashbookList, currentMonth);
        cashbookList = CashbookCommon.sortList(cashbookList);

        // 设置文本框
        timeDisplay.setText("时间： 本月");
        result = CashbookCommon.moneyCalculator(cashbookList);
        incomeSummary.setText("收入\n" + result[1]);
        expenditureSummary.setText("支出\n" + result[0]);
        remainSummary.setText("结余\n" + result[2]);
        countSummary.setText("总笔数\n" + result[3].split("\\.")[0]);

        // 收入
        barChartIncome = findViewById(R.id.chart_barchart_income);
        barChartIncome.getDescription().setEnabled(false);
        setIncomeDataBar(cashbookList, "月");
        pieChartIncome = (PieChart) findViewById(R.id.chart_pie_chart_income);
        setIncomeDataPie(cashbookList);

        // 支出
        barChartExpenditure = findViewById(R.id.chart_barchart_expenditure);
        barChartExpenditure.getDescription().setEnabled(false);
        setExpenditureDataBar(cashbookList, "月");
        pieChartExpenditure = (PieChart) findViewById(R.id.chart_pie_chart_expenditure);
        setExpenditureDataPie(cashbookList);

        // 选择月
        selectMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar today = Calendar.getInstance();
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(ChartActivity.this,
                        new MonthPickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                timeDisplay.setText("时间：    " + selectedYear + "年" + (selectedMonth + 1) + "月");
                                cashbookList = CashbookCommon.selectMonthList(initCashbook(), selectedYear + "年" + (selectedMonth + 1) + "月");
                                cashbookList = CashbookCommon.sortList(cashbookList);
                                setIncomeDataBar(cashbookList, "月");
                                setIncomeDataPie(cashbookList);
                                setExpenditureDataBar(cashbookList, "月");
                                setExpenditureDataPie(cashbookList);
                                result = CashbookCommon.moneyCalculator(cashbookList);
                                incomeSummary.setText("收入\n" + result[1]);
                                expenditureSummary.setText("支出\n" + result[0]);
                                remainSummary.setText("结余\n" + result[2]);
                                countSummary.setText("总笔数\n" + result[3].split("\\.")[0]);
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

        // 选择年
        selectYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar today = Calendar.getInstance();
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(ChartActivity.this,
                        new MonthPickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                timeDisplay.setText("时间：    " + selectedYear + "年");
                                cashbookList = CashbookCommon.selectYearList(initCashbook(), selectedYear + "年");
                                cashbookList = CashbookCommon.sortList(cashbookList);
                                setIncomeDataBar(cashbookList, "年");
                                setIncomeDataPie(cashbookList);
                                setExpenditureDataBar(cashbookList, "年");
                                setExpenditureDataPie(cashbookList);
                                result = CashbookCommon.moneyCalculator(cashbookList);
                                incomeSummary.setText("收入\n" + result[1]);
                                expenditureSummary.setText("支出\n" + result[0]);
                                remainSummary.setText("结余\n" + result[2]);
                                countSummary.setText("总笔数\n" + result[3].split("\\.")[0]);
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
    }

    // 收入
    private void setIncomeDataBar(List<Cashbook> list, String strDivider) {
        if (list.size() != 0) {
            ArrayList<BarEntry> yValues = new ArrayList<>();
            XAxis xAxis = barChartIncome.getXAxis();
            if (strDivider.equals("月")) {
                xLabel = getDaysInMonth(list);
            } else {
                xLabel = getMonths();
            }
            xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabel));

            Integer[] incomeSum = new Integer[xLabel.size()];
            Arrays.fill(incomeSum, 0);

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getInOrOut().equals("收入")){
                    int value = Integer.parseInt(list.get(i).getMoney());
                    String x;
                    if (strDivider.equals("月")) {
                        x = (list.get(i).getTime().split(strDivider)[1]).split("日")[0];
                    } else {
                        x = (list.get(i).getTime().split(strDivider)[1]).split("月")[0];
                    }
                    for (int j = 0; j < xLabel.size(); j++) {
                        if (x.equals(xLabel.get(j))) {
                            incomeSum[j] += value;
                        }
                    }
                }
            }

            for (int i = 0; i < incomeSum.length; i++) {
                yValues.add(new BarEntry(Integer.parseInt(xLabel.get(i)), incomeSum[i], xLabel.get(i)));
            }

            BarDataSet incomeSet = new BarDataSet(yValues, "收入数据");
            incomeSet.setColors(Color.LTGRAY);
            incomeSet.setDrawValues(false);
            BarData incomeData = new BarData(incomeSet);

            YAxis leftAxis = barChartIncome.getAxisLeft();
            leftAxis.setAxisMinimum(0f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(7);
            barChartIncome.animateY(500);
            barChartIncome.setData(incomeData);
            barChartIncome.invalidate();
            barChartIncome.getAxisRight().setEnabled(false);
            barChartIncome.getAxisLeft().setDrawGridLines(false);
            barChartIncome.getXAxis().setDrawGridLines(false);
            barChartIncome.setTouchEnabled(true);
            IMarker marker = new CustomMarkerView(getApplicationContext(), R.layout.tv_content);
            barChartIncome.setMarker(marker);
            barChartIncome.setFitBars(true);
        }
    }

    private void setIncomeDataPie(List<Cashbook> list) {
        if (list.size() != 0) {
            ArrayList<PieEntry> yValues = new ArrayList<>();
            ArrayList<Float> categorySum = new ArrayList<>();
            ArrayList<String> categoryName = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getInOrOut().equals("收入")){
                    int value = Integer.parseInt(list.get(i).getMoney());
                    String category = list.get(i).getCategory();
                    if (categoryName.contains(category)) {
                        categorySum.set(categoryName.indexOf(category), (categorySum.get(categoryName.indexOf(category)) + value));
                    } else {
                        categoryName.add(category);
                        categorySum.add((float)value);
                    }
                }
            }

            for (int i = 0; i < categoryName.size(); i++) {
                yValues.add(new PieEntry(categorySum.get(i), categoryName.get(i)));
            }

            PieDataSet pieDataSet = new PieDataSet(yValues, "类别");
            pieDataSet.setSliceSpace(3f);
            pieDataSet.setSelectionShift(5f);
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            PieData data = new PieData(pieDataSet);
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.WHITE);

            pieChartIncome.animateY(500, Easing.EaseInOutCubic);
            pieChartIncome.setData(data);
            pieChartIncome.setUsePercentValues(true);
            pieChartIncome.getDescription().setEnabled(false);
            pieChartIncome.setExtraOffsets(5, 10, 5, 5);
            pieChartIncome.setDragDecelerationFrictionCoef(0.95f);
            pieChartIncome.setDrawHoleEnabled(true);
            pieChartIncome.setHoleColor(Color.WHITE);
            pieChartIncome.setTransparentCircleRadius(61f);
        }
    }

    // 支出
    private void setExpenditureDataBar(List<Cashbook> list, String strDivider) {
        if (list.size() != 0) {
            ArrayList<BarEntry> yValues = new ArrayList<>();
            XAxis xAxis = barChartExpenditure.getXAxis();
            if (strDivider.equals("月")) {
                xLabel = getDaysInMonth(list);
            } else {
                xLabel = getMonths();
            }
            xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabel));

            Integer[] incomeSum = new Integer[xLabel.size()];
            Arrays.fill(incomeSum, 0);

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getInOrOut().equals("支出")){
                    int value = Integer.parseInt(list.get(i).getMoney());
                    String x;
                    if (strDivider.equals("月")) {
                        x = (list.get(i).getTime().split(strDivider)[1]).split("日")[0];
                    } else {
                        x = (list.get(i).getTime().split(strDivider)[1]).split("月")[0];
                    }
                    for (int j = 0; j < xLabel.size(); j++) {
                        if (x.equals(xLabel.get(j))) {
                            incomeSum[j] += value;
                        }
                    }
                }
            }

            for (int i = 0; i < incomeSum.length; i++) {
                yValues.add(new BarEntry(Integer.parseInt(xLabel.get(i)), incomeSum[i], xLabel.get(i)));
            }

            BarDataSet expenditureSet = new BarDataSet(yValues, "支出数据");
            expenditureSet.setColors(Color.LTGRAY);
            expenditureSet.setDrawValues(false);
            BarData incomeData = new BarData(expenditureSet);

            YAxis leftAxis = barChartExpenditure.getAxisLeft();
            leftAxis.setAxisMinimum(0f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(7);
            barChartExpenditure.animateY(500);
            barChartExpenditure.setData(incomeData);
            barChartExpenditure.invalidate();
            barChartExpenditure.getAxisRight().setEnabled(false);
            barChartExpenditure.getAxisLeft().setDrawGridLines(false);
            barChartExpenditure.getXAxis().setDrawGridLines(false);
            barChartExpenditure.setTouchEnabled(true);
            IMarker marker = new CustomMarkerView(getApplicationContext(), R.layout.tv_content);
            barChartExpenditure.setMarker(marker);
            barChartExpenditure.setFitBars(true);
        }
    }

    private void setExpenditureDataPie(List<Cashbook> list) {
        if (list.size() != 0) {
            ArrayList<PieEntry> yValues = new ArrayList<>();
            ArrayList<Float> categorySum = new ArrayList<>();
            ArrayList<String> categoryName = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getInOrOut().equals("支出")){
                    int value = Integer.parseInt(list.get(i).getMoney());
                    String category = list.get(i).getCategory();
                    if (categoryName.contains(category)) {
                        categorySum.set(categoryName.indexOf(category), (categorySum.get(categoryName.indexOf(category)) + value));
                    } else {
                        categoryName.add(category);
                        categorySum.add((float)value);
                    }
                }
            }

            for (int i = 0; i < categoryName.size(); i++) {
                yValues.add(new PieEntry(categorySum.get(i), categoryName.get(i)));
            }

            PieDataSet pieDataSet = new PieDataSet(yValues, "类别");
            pieDataSet.setSliceSpace(3f);
            pieDataSet.setSelectionShift(5f);
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            PieData data = new PieData(pieDataSet);
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.WHITE);

            pieChartExpenditure.animateY(500, Easing.EaseInOutCubic);
            pieChartExpenditure.setData(data);
            pieChartExpenditure.setUsePercentValues(true);
            pieChartExpenditure.getDescription().setEnabled(false);
            pieChartExpenditure.setExtraOffsets(5, 10, 5, 5);
            pieChartExpenditure.setDragDecelerationFrictionCoef(0.95f);
            pieChartExpenditure.setDrawHoleEnabled(true);
            pieChartExpenditure.setHoleColor(Color.WHITE);
            pieChartExpenditure.setTransparentCircleRadius(61f);
        }
    }

    private List<Cashbook> initCashbook() {
        return LitePal.where("userName=?", currentUser).find(Cashbook.class);
    }

    private ArrayList<String> getDaysInMonth(List<Cashbook> list){
        int targetYear = Integer.parseInt(list.get(0).getTime().split("年")[0]);
        int targetMonth = Integer.parseInt((list.get(0).getTime().split("年")[1]).split("月")[0]);
        YearMonth yearMonthObject = YearMonth.of(targetYear, targetMonth);
        int daysInMonth = yearMonthObject.lengthOfMonth();
        ArrayList<String> xLabel = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            xLabel.add(i + "");
        }
        return xLabel;
    }

    private ArrayList<String> getMonths() {
        ArrayList<String> xLabel = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            xLabel.add(i + "");
        }
        return xLabel;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
