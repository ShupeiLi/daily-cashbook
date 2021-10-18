package com.example.daily_cashbook.fragment;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daily_cashbook.MetaApplication;
import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.DatabaseHelper;
import com.example.daily_cashbook.entity.User;

import org.w3c.dom.Text;

public class CashFragment extends Fragment implements View.OnClickListener {
    //cash 界面分别加载不同的fragment
    private int flag;   //0 支出， 1 收入
    private String content;     //显示内容

    //当前用户
    private User user;

    public CashFragment(int flag, String content){
        this.flag = flag;
        this.content = content;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取当前用户,从全局变量中获取
        MetaApplication metaApplication = (MetaApplication) getActivity().getApplicationContext();
        user = metaApplication.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;
        if (this.flag == 0){    //支出页面
            //支出
            view = inflater.inflate(R.layout.cash_content_outcash, container, false);
            //可以在view上放置一些EditText  和  Button
            //类别，金额，日期，备注
            //点击保存，即可插入到sqlLite数据库中
            Button submit = view.findViewById(R.id.out_submit);
            EditText title = view.findViewById(R.id.out_title_et);
            EditText time = view.findViewById(R.id.out_time_et);
            EditText money = view.findViewById(R.id.out_type_et);
            EditText type = view.findViewById(R.id.out_type_et);
            EditText comment = view.findViewById(R.id.out_comment_et);
            EditText image = view.findViewById(R.id.out_image_et);
            //out, this.user

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //将获取的数据存储到数据库中
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("title", title.getText().toString());
                    //插入数据
                    DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), "daily_cashbook.db", null, 1);
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    db.insert("cashbook", null, contentValues); //向cashbook里面插入数据
                    //提示
                    Toast.makeText(getContext(), user.getUserName() + title.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }else if (this.flag == 1){  //收入页面
            //收入
            view = inflater.inflate(R.layout.cash_content_incash, container, false);
            TextView textView = view.findViewById(R.id.cash_content_incash_text);
            textView.setText(this.content);
        }

        return view;
    }

    @Override
    public void onClick(View v) {

    }
}