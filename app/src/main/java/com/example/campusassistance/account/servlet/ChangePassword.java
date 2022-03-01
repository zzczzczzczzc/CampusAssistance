package com.example.campusassistance.account.servlet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.campusassistance.R;
import com.example.campusassistance.init.servlet.ServerConnection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    private EditText mOldPassword;
    private EditText mNewPassword;
    private Button mChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        init();
        mChangePassword.setOnClickListener(this);
    }

    private void init() {
        mOldPassword = findViewById(R.id.et_old_password);
        mNewPassword = findViewById(R.id.et_new_password);
        mChangePassword = findViewById(R.id.change_password);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.change_password) {
            String account = getSharedPreferences("Login", MODE_PRIVATE).getString("userId", "");
            String oldPassword = mOldPassword.getText().toString().trim();
            String newPassword = mNewPassword.getText().toString().trim();
            changePassword(account, oldPassword, newPassword);
        }
    }

    private void changePassword(final String account, final String oldPassword, final String newPassword) {
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "系统错误，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "密码不能为空，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        if (oldPassword.equals(newPassword)) {
            Toast.makeText(this, "不能与原先的密码相同", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String path = "http://192.168.31.80:8080/Login/ChangePassword";
                    String params = "id=" + URLEncoder.encode(account, "UTF-8") +
                            "&password=" + URLEncoder.encode(oldPassword, "UTF-8")
                            + "&newPassword=" + URLEncoder.encode(newPassword, "UTF-8");
                    String responseText = ServerConnection.connection(path, params);

                    Looper.prepare();
                    if (("True").equals(responseText)) {
                        Toast.makeText(ChangePassword.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (("False").equals(responseText)) {
                        Toast.makeText(ChangePassword.this, "旧密码错误，请重新输入！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChangePassword.this, "服务器连接出现错误", Toast.LENGTH_SHORT).show();
                    }
                    Looper.loop();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
    }
}