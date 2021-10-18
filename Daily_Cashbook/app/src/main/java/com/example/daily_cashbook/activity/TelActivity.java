package com.example.daily_cashbook.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.UserInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private String currentUser;
    private UserInfo userInfo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tel_reset);
        Toolbar toolbar = (Toolbar)findViewById(R.id.reset_tel_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        editText = (EditText) findViewById(R.id.reset_tel_edit);
        button = (Button) findViewById(R.id.reset_tel_confirm);
        SharedPreferences pref = getSharedPreferences("currentUser", MODE_PRIVATE);
        currentUser = pref.getString("user", "");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputTel = editText.getText().toString();
                if (!isTel(inputTel)) {
                    Toast.makeText(getApplicationContext(), "请输入有效的电话号码", Toast.LENGTH_SHORT).show();
                } else {
                    userInfo = new UserInfo();
                    userInfo.setTelNumber(inputTel);
                    userInfo.updateAll("userName=?", currentUser);
                    Toast.makeText(getApplicationContext(), "电话号码重置成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean isTel(String telNumber) {
        if (telNumber.length() != 11) {
            return false;
        }
        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(telNumber);
        return (m.find() && m.group().equals(telNumber));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
        startActivity(intent);
        finish();
    }
}
