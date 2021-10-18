package com.example.daily_cashbook.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.daily_cashbook.MainActivity;
import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.UserInfo;

import org.litepal.LitePal;

import java.util.Objects;


public class LoginFragment extends Fragment {

    private EditText account;
    private EditText password;
    private View view;
    private UserInfo userInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_login_content, container, false);
        Button loginButton = (Button) view.findViewById(R.id.login_loginButton);
        Button signupButton = (Button) view.findViewById(R.id.login_signupButton);
        account = (EditText) view.findViewById(R.id.login_username);
        password = (EditText) view.findViewById(R.id.login_password);
        Log.d("LoginFragment", "LoginFragment has been created");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputAccount = account.getText().toString();
                String inputPassword = password.getText().toString();
                if (loginCheck(inputAccount, inputPassword)) {
                    SharedPreferences.Editor userEditor = requireActivity().getSharedPreferences("currentUser", Context.MODE_PRIVATE).edit();
                    userEditor.putString("user", inputAccount);
                    userEditor.apply();
                    Toast.makeText(getActivity(), "登录成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.login_placeholder, new SignupFragment());
                transaction.commit();
            }
        });
        return view;
    }


    private boolean loginCheck (String inputAccount, String inputPassword){
        boolean isExist = LitePal.isExist(UserInfo.class, "userName=?", inputAccount);
        if (!isExist) {
            Toast.makeText(getActivity(), "该用户名不存在，请先注册！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            userInfo = LitePal.where("userName=?", inputAccount).findFirst(UserInfo.class);
            if (!inputPassword.equals(userInfo.getPassword())) {
                Toast.makeText(getActivity(), "密码错误", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }
    }

}
