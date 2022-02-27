package com.example.campusassistance.account.servlet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.campusassistance.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    private EditText mAccount;
    private EditText mOldPassword;
    private EditText mNewPassword;
    private Button mChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        init();
    }

    private void init() {
        mAccount = findViewById(R.id.et_account);
        mOldPassword = findViewById(R.id.et_old_password);
        mNewPassword = findViewById(R.id.et_new_password);
        mChangePassword = findViewById(R.id.change_password);
        mChangePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.change_password) {
            String account = mAccount.getText().toString().trim();
            String oldPassword = mOldPassword.getText().toString().trim();
            String newPassword = mNewPassword.getText().toString().trim();
            changePassword(account, oldPassword, newPassword);
        }
    }

    private void changePassword(final String account, final String oldPassword, final String newPassword) {
        final String path = "http://192.168.31.80:8080/Login/ChangePassword";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setReadTimeout(5000);//设置超时信息
                    conn.setConnectTimeout(5000);//设置超时信息

                    conn.setDoInput(true);//设置输入流，允许输入
                    conn.setDoOutput(true);//设置输出流，允许输出
                    conn.setUseCaches(false);//设置POST请求方式不能够使用缓存
                    conn.connect();

                    DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                    String params = "id=" + URLEncoder.encode(account, "UTF-8") +
                            "&password=" + URLEncoder.encode(oldPassword, "UTF-8")
                            + "&newPassword=" + URLEncoder.encode(newPassword, "UTF-8");
                    dataOutputStream.write(params.getBytes("UTF-8"));
                    dataOutputStream.flush();
                    dataOutputStream.close();

                    InputStream inputStream = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuffer sb = new StringBuffer();
                    String message = "";
                    while ((message = br.readLine()) != null) {
                        sb.append(message);
                    }

                    String responseText = sb.toString();
                    inputStream.close();
                    conn.disconnect();

                    Looper.prepare();
                    if (("True").equals(responseText)) {
                        Toast.makeText(ChangePassword.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if(("False").equals(responseText)){
                        Toast.makeText(ChangePassword.this, "账号或密码错误，请重新输入！", Toast.LENGTH_SHORT).show();
                    }
                    Looper.loop();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
    }
}