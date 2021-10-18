package com.example.daily_cashbook.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.daily_cashbook.MainActivity;
import com.example.daily_cashbook.R;
import com.example.daily_cashbook.fragment.KeepAccountFragment;

public class KeepAccountActivity extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.keep_account_blank);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.keep_account_blank_frame, new KeepAccountFragment());
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor userEditor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
        userEditor.putString("selectedItem", "");
        userEditor.putString("selectedItem2", "");
        userEditor.apply();
        for (Fragment fragment: getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}