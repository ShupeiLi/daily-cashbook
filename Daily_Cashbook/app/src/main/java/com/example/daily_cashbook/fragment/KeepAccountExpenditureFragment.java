package com.example.daily_cashbook.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daily_cashbook.MainActivity;
import com.example.daily_cashbook.R;
import com.example.daily_cashbook.category.ExpenditureAdapter;
import com.example.daily_cashbook.category.ExpenditureCategory;
import com.example.daily_cashbook.dbutils.Cashbook;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class KeepAccountExpenditureFragment extends Fragment {
    private View view;
    private int count = 1;
    private String imgName;
    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;
    public static final int SELECT_PICTURE = 2;
    private ImageView chargeImage1;
    private ImageView chargeImage2;
    private TextView categoryDisplay;
    private String image1Path = "";
    private String image2Path = "";
    private String inputTime;
    private Cashbook cashbook;
    private Handler handler;
    private Runnable runnable;

    private ExpenditureCategory[] expenditureCategories = {new ExpenditureCategory("工资", R.drawable.salary_1),
            new ExpenditureCategory("奖金", R.drawable.bonus_2), new ExpenditureCategory("收款", R.drawable.checkin_3),
            new ExpenditureCategory("投资收益", R.drawable.invest_4), new ExpenditureCategory("生活费", R.drawable.life_expense_5),
            new ExpenditureCategory("零花钱", R.drawable.pocket_money_6), new ExpenditureCategory("外快", R.drawable.extra_7),
            new ExpenditureCategory("退款", R.drawable.refund_8), new ExpenditureCategory("红包", R.drawable.redbag_9),
            new ExpenditureCategory("意外所得", R.drawable.accident_10), new ExpenditureCategory("其他", R.drawable.other_11)
    };

    private List<ExpenditureCategory> expenditureCategoryList = new ArrayList<>(Arrays.asList(expenditureCategories));
    private ExpenditureAdapter expenditureAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.keep_account_expenditure, container, false);
        EditText money = (EditText) view.findViewById(R.id.keep_account_expenditure_amount);
        EditText comment = (EditText) view.findViewById(R.id.keep_account_expenditure_comment);
        Button backButton = (Button) view.findViewById(R.id.keep_account_expenditure_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor userEditor = getActivity().getSharedPreferences("currentUser", Context.MODE_PRIVATE).edit();
                userEditor.putString("selectedItem", "");
                userEditor.putString("selectedItem2", "");
                userEditor.apply();
                for (Fragment fragment: getActivity().getSupportFragmentManager().getFragments()) {
                    if (fragment != null) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                }
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        // 类别滚动视图
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.keep_account_expenditure_recycle);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        expenditureAdapter = new ExpenditureAdapter(expenditureCategoryList);
        recyclerView.setAdapter(expenditureAdapter);
        categoryDisplay = (TextView) view.findViewById(R.id.keep_account_expenditure_category_display);
        categoryDisplay.setText("点击下列图标选择类别");
        handler = new Handler();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(500);
                        if(getActivity() == null)
                            return;
                        runnable = new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                SharedPreferences pref = getActivity().getSharedPreferences("currentUser", Context.MODE_PRIVATE);
                                String currentItem = pref.getString("selectedItem2", "");
                                if (!currentItem.equals("")) {
                                    categoryDisplay.setText("已选类别：" + currentItem);
                                }
                            }
                        };
                        getActivity().runOnUiThread(runnable);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        // 选择日期
        TextView calendarDisplay = (TextView)view.findViewById(R.id.keep_account_expenditure_calendar_display);
        Button calendarButton = (Button)view.findViewById(R.id.keep_account_expenditure_calendar_select);
        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("选择日期");
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();
        calendarButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                });

        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        calendarDisplay.setText(materialDatePicker.getHeaderText());
                        inputTime = materialDatePicker.getHeaderText();
                    }
                });

        // 账单照片：拍照
        Button pictureButton = (Button) view.findViewById(R.id.keep_account_expenditure_camera);
        chargeImage1 = (ImageView) view.findViewById(R.id.keep_account_expenditure_pic1);
        chargeImage2 = (ImageView) view.findViewById(R.id.keep_account_expenditure_pic2);
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 2) {
                    Toast.makeText(getContext(), "最多添加两张照片", Toast.LENGTH_SHORT).show();
                } else {
                    imgName = "" + System.currentTimeMillis() + ".jpg";
                    File outputImage = new File(getActivity().getExternalCacheDir(), imgName);
                    imageUri = FileProvider.getUriForFile(getContext(),
                            "com.example.daily_cashbook.fileProvider", outputImage);
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, TAKE_PHOTO);
                }
            }
        });

        // 账单照片：相册
        Button albumButton = (Button) view.findViewById(R.id.keep_account_expenditure_album);
        albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 2) {
                    Toast.makeText(getContext(), "最多添加两张照片", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                }
            }
        });

        // 保存结果
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.keep_account_expenditure_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存数据到数据库
                String inputMoney = money.getText().toString();
                String inputComment = comment.getText().toString();
                SharedPreferences pref = getActivity().getSharedPreferences("currentUser", Context.MODE_PRIVATE);
                String inputCategory = pref.getString("selectedItem2", "");
                String inputUserName = pref.getString("user", "");
                String inOrOut = "收入";
                if (inputMoney.isEmpty()) {
                    Toast.makeText(getContext(), "金额不能为空", Toast.LENGTH_SHORT).show();
                } else if (inputCategory.equals("")) {
                    Toast.makeText(getContext(), "类别不能为空", Toast.LENGTH_SHORT).show();
                } else if (!isNumeric(inputMoney)) {
                    Toast.makeText(getContext(), "金额必须为数字", Toast.LENGTH_SHORT).show();
                } else {
                    cashbook = new Cashbook();
                    cashbook.setUserName(inputUserName);
                    cashbook.setCategory(inputCategory);
                    cashbook.setInOrOut(inOrOut);
                    cashbook.setMoney(inputMoney);
                    cashbook.setTime(inputTime);
                    cashbook.setComment(inputComment);
                    cashbook.setImage1(image1Path);
                    cashbook.setImage2(image2Path);
                    cashbook.save();
                    // 跳转页面
                    SharedPreferences.Editor userEditor = getContext().getSharedPreferences("currentUser", Context.MODE_PRIVATE).edit();
                    userEditor.putString("selectedItem2", "");
                    userEditor.apply();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                    for (Fragment fragment : getActivity().getSupportFragmentManager().getFragments()) {
                        if (fragment != null) {
                            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        }
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (count == 1) {
                    Picasso.get().load(imageUri).into(chargeImage1);
                    image1Path = imageUri.toString();
                } else {
                    Picasso.get().load(imageUri).into(chargeImage2);
                    image2Path = imageUri.toString();
                }
                count++;
            }
        }
        if (requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                if (imageUri != null) {
                    if (count == 1) {
                        Picasso.get().load(imageUri).into(chargeImage1);
                        image1Path = imageUri.toString();
                    } else {
                        Picasso.get().load(imageUri).into(chargeImage2);
                        image2Path = imageUri.toString();
                    }
                    count++;
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

}
