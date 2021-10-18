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

public class EmailActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private String currentUser;
    private UserInfo userInfo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.email_reset);
        Toolbar toolbar = (Toolbar)findViewById(R.id.reset_email_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        editText = (EditText) findViewById(R.id.reset_email_edit);
        button = (Button) findViewById(R.id.reset_email_confirm);
        SharedPreferences pref = getSharedPreferences("currentUser", MODE_PRIVATE);
        currentUser = pref.getString("user", "");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEmail = editText.getText().toString();
                if (!inputEmail.contains("@")) {
                    Toast.makeText(getApplicationContext(), "请输入有效的邮箱地址", Toast.LENGTH_SHORT).show();
                } else {
                    userInfo = new UserInfo();
                    userInfo.setEmailAddress(inputEmail);
                    userInfo.updateAll("userName=?", currentUser);
                    Toast.makeText(getApplicationContext(), "邮箱重置成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

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
