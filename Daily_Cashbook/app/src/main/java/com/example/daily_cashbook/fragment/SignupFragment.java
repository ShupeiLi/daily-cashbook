package com.example.daily_cashbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.UserInfo;

import org.litepal.LitePal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupFragment extends Fragment {

    private EditText account;
    private EditText email;
    private EditText telNumber;
    private EditText password;
    private UserInfo userInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sign_up, container, false);
        account = (EditText) view.findViewById(R.id.signup_username);
        email = (EditText) view.findViewById(R.id.signup_email);
        telNumber = (EditText) view.findViewById(R.id.signup_tel);
        password = (EditText) view.findViewById(R.id.signup_password);
        Button signupButton = (Button) view.findViewById(R.id.signup_signupButton);
        Button backButton = (Button) view.findViewById(R.id.signup_go_back);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputAccount = account.getText().toString();
                String inputEmail = email.getText().toString();
                String inputTelNumber = telNumber.getText().toString();
                String inputPassword = password.getText().toString();
                if (ruleCheck(inputAccount, inputEmail, inputTelNumber, inputPassword)) {
                    userInfo = new UserInfo();
                    userInfo.setUserName(inputAccount);
                    userInfo.setEmailAddress(inputEmail);
                    userInfo.setTelNumber(inputTelNumber);
                    userInfo.setPassword(inputPassword);
                    userInfo.setProfile("");
                    userInfo.save();
                    Toast.makeText(getActivity(), "注册成功！", Toast.LENGTH_SHORT).show();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.remove(SignupFragment.this);
                    transaction.commit();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Fragment fragment: getActivity().getSupportFragmentManager().getFragments()) {
                    if (fragment != null) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.login_placeholder, new LoginFragment());
                transaction.commit();
            }
        });

        return view;
    }

    private boolean ruleCheck(String inputAccount, String inputEmail, String inputTelNumber, String inputPassword) {
        if (inputAccount.length() < 5) {
            Toast.makeText(getActivity(), "账号名称不得少于五个字符", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!inputEmail.contains("@")) {
            Toast.makeText(getActivity(), "请输入有效的邮箱地址", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isTel(inputTelNumber)) {
            Toast.makeText(getActivity(), "请输入有效的电话号码", Toast.LENGTH_SHORT).show();
            return false;
        } else if (inputPassword.length() < 6) {
            Toast.makeText(getActivity(), "密码长度不得小于6位", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            boolean isExist = LitePal.isExist(UserInfo.class, "userName=?", inputAccount);
            if (isExist) {
                Toast.makeText(getActivity(), "该用户名已存在！", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }
    }

    private boolean isTel(String telNumber) {
        if (telNumber.length() != 11) {
            return false;
        }
        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(telNumber);
        return (m.find() && m.group().equals(telNumber));
    }
}
