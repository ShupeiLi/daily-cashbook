package com.example.daily_cashbook.dbutils;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;
import android.util.Log;

import androidx.annotation.NonNull;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CashbookCommon {
    public static final int VIEW_TYPE_GROUP = 0;
    public static final int VIEW_TYPE_CASHBOOK = 1;

    public static List<String> dateAvailable = new ArrayList<>();

    public static List<Cashbook> sortList(List<Cashbook> cashbookList) {
        cashbookList.sort(new Comparator<Cashbook>() {
            @Override
            public int compare(Cashbook o1, Cashbook o2) {
                if ((o1.getTime() != null) && (o2.getTime() != null)) {
                    long time1 = dateCovert(o1.getTime());
                    long time2 = dateCovert(o2.getTime());
                    return Long.compare(time1, time2);
                } else if (o1.getTime() == null) {
                    return -1;
                } else if (o2.getTime() == null) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        Collections.reverse(cashbookList);
        return cashbookList;
    }

    @NonNull
    public static List<Cashbook> addDates(@NonNull List<Cashbook> list) {
        List<Cashbook> customList = new ArrayList<>();
        Cashbook firstCashbook = new Cashbook();
        if (list.size() != 0) {
            firstCashbook.setTime(list.get(0).getTime());
            firstCashbook.setViewType(VIEW_TYPE_GROUP);
            dateAvailable.add(list.get(0).getTime());
            int i;

            customList.add(firstCashbook);

            for (i = 0; i < list.size() - 1; i++) {
                Cashbook cashbook = new Cashbook();
                String time1 = list.get(i).getTime();
                String time2 = list.get(i + 1).getTime();
                if ((time1 != null) || (time2 != null)) {
                    if ((time1 != null) && (time2 != null)) {
                        if (time1.equals(time2)) {
                            list.get(i).setViewType(VIEW_TYPE_CASHBOOK);
                            customList.add(list.get(i));
                        } else {
                            list.get(i).setViewType(VIEW_TYPE_CASHBOOK);
                            customList.add(list.get(i));
                            cashbook.setTime(time2);
                            cashbook.setViewType(VIEW_TYPE_GROUP);
                            dateAvailable.add(time2);
                            customList.add(cashbook);
                        }
                    } else {
                        list.get(i).setViewType(VIEW_TYPE_CASHBOOK);
                        customList.add(list.get(i));
                        cashbook.setTime("无日期");
                        cashbook.setViewType(VIEW_TYPE_GROUP);
                        dateAvailable.add("无日期");
                        customList.add(cashbook);
                    }
                } else {
                    list.get(i).setViewType(VIEW_TYPE_CASHBOOK);
                    customList.add(list.get(i));
                }
            }

            list.get(i).setViewType(VIEW_TYPE_CASHBOOK);
            customList.add(list.get(i));
        }
        return customList;
    }

    public static List<Cashbook> selectMonthList(List<Cashbook> cashbookList, String month) {
        List<Cashbook> customList = new ArrayList<>();
        String time;
        if (cashbookList.size() != 0) {
            for (int i = 0; i < cashbookList.size(); i++) {
                time = cashbookList.get(i).getTime();
                if (((time.split("月")[0]) + "月").equals(month)){
                    customList.add(cashbookList.get(i));
                }
            }
        }
        return customList;
    }

    public static List<Cashbook> selectYearList(List<Cashbook> cashbookList, String year) {
        List<Cashbook> customList = new ArrayList<>();
        String time;
        if (cashbookList.size() != 0) {
            for (int i = 0; i < cashbookList.size(); i++) {
                time = cashbookList.get(i).getTime();
                if ((time.substring(0, 5)).equals(year)) {
                    customList.add(cashbookList.get(i));
                }
            }
        }
        return customList;
    }

    public static List<Cashbook> selectRangeList(List<Cashbook> cashbookList, String[] dateRange) {
        List<Cashbook> customList = new ArrayList<>();
        long dateLowerLimit = dateCovert(dateRange[0]);
        long dateUpperLimit = dateCovert(dateRange[1]);
        long time;

        if (cashbookList.size() != 0) {
            for (int i = 0; i < cashbookList.size(); i++) {
                time = dateCovert(cashbookList.get(i).getTime());
                if ((time >= dateLowerLimit) && (time <= dateUpperLimit)) {
                    customList.add(cashbookList.get(i));
                }
            }
        }

        return customList;
    }

    public static String[] moneyCalculator(List<Cashbook> cashbookList) {
        String[] result = new String[4];
        double expenditure = 0, income = 0, remain, count = 0;

        if (cashbookList.size() != 0) {
            for (int i = 0; i < cashbookList.size(); i++) {
                if (cashbookList.get(i).getInOrOut() != null) {
                    if ((cashbookList.get(i).getInOrOut()).equals("支出")) {
                        expenditure += Double.parseDouble(cashbookList.get(i).getMoney());
                    } else {
                        income += Double.parseDouble(cashbookList.get(i).getMoney());
                    }
                    count++;
                }
            }
        }

        remain = income - expenditure;
        result[0] = expenditure + "";
        result[1] = income + "";
        result[2] = remain + "";
        result[3] = count + "";
        return result;
    }

    private static long dateCovert(String dateString) {
        String year, month, day, temp1, temp2;
        Date date = null;

        if (dateString.contains("年")) {
            year = dateString.split("年")[0];
            temp1 = dateString.split("年")[1];
            month = temp1.split("月")[0];
        } else {
            year = Calendar.getInstance().get(Calendar.YEAR) + "";
            month = dateString.split("月")[0];
        }
        temp2 = dateString.split("月")[1];
        day = temp2.split("日")[0];
        if (month.length() == 1) {
            month = "0" + month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }

        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date = dateFormat.parse(year + "-" + month + "-" + day);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date.getTime();
    }

}
