package com.example.daily_cashbook.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.daily_cashbook.MainActivity;
import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.UserInfo;
import com.squareup.picasso.Picasso;

import org.litepal.LitePal;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonActivity extends AppCompatActivity {
    public static final int SELECT_PICTURE = 2;
    private static final int RQS_OPEN_IMAGE = 1;
    private Button imageReset;
    private Button passwordReset;
    private Button emailReset;
    private Button telReset;
    private Button reminder;
    private Uri imageUri;
    private String imagePath;
    private String currentUser;
    private UserInfo userInfo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.person_page_main);
        imageReset = (Button) findViewById(R.id.person_page_set_image);
        passwordReset = (Button) findViewById(R.id.person_page_set_password);
        emailReset = (Button) findViewById(R.id.person_page_set_email);
        telReset = (Button) findViewById(R.id.person_page_set_tel);
        reminder= (Button) findViewById(R.id.person_page_alarm);
        Toolbar toolbar = (Toolbar)findViewById(R.id.person_page_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        SharedPreferences pref = getSharedPreferences("currentUser", MODE_PRIVATE);
        currentUser = pref.getString("user", "");
        userInfo = LitePal.where("userName=?", currentUser).findFirst(UserInfo.class);
        TextView userView = (TextView) findViewById(R.id.person_page_username);
        userView.setText(userInfo.getUserName());

        imageReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

        passwordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        emailReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

        telReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TelActivity.class);
                startActivity(intent);
                finish();
            }
        });

        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReminderActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                if (imageUri != null) {
                    imagePath = imageUri.toString();
                    userInfo = new UserInfo();
                    userInfo.setProfile(imagePath);
                    userInfo.updateAll("userName=?", currentUser);
                    Toast.makeText(getApplicationContext(), "头像设置成功！", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
