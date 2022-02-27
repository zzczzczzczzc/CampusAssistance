package com.example.campusassistance.account.servlet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText mAccount;
    private EditText mPassword;
    private Button login;

    private int mResultCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        login.setOnClickListener(this);
    }

    private void init() {
        mAccount = (EditText) findViewById(R.id.et_account);
        mPassword = (EditText) findViewById(R.id.et_password);
        login = (Button) findViewById(R.id.bt_login);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_login) {
            String account = mAccount.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            login(account, password);
        }
    }

    private void login(final String account, final String password) {
        final String path = "http://192.168.31.80:8080/Login/Login";
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
                    String params = "id=" + URLEncoder.encode(account, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");
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
                        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("isLogin", true);
                        editor.putString("userId", account);
                        editor.commit();
                        Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
                        setResult(mResultCode);
                        finish();
                    } else {
                        Toast.makeText(Login.this, "账号或密码错误，请重新输入！", Toast.LENGTH_SHORT).show();
                    }
                    Looper.loop();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
    }
}