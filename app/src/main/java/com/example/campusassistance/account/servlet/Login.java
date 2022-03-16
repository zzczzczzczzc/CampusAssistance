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
import com.example.campusassistance.init.servlet.ServerConnection;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    final String path = "http://192.168.31.80:8080/Login/Login";
//                    String params = "id=" + URLEncoder.encode(account, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");
//                    String responseText = ServerConnection.connection(path, params);
//
//                    Looper.prepare();
//                    if (("True").equals(responseText)) {
//                        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sp.edit();
//                        editor.putBoolean("isLogin", true);
//                        editor.putString("userId", account);
//                        editor.commit();
//                        Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
//                        setResult(mResultCode);
//                        finish();
//                    } else if (("False").equals(responseText)) {
//                        Toast.makeText(Login.this, "账号或密码错误，请重新输入！", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(Login.this, "服务器连接出现错误", Toast.LENGTH_SHORT).show();
//                    }
//                    Looper.loop();
//                } catch (Exception exception) {
//                    exception.printStackTrace();
//                }
                try {
                    final String path = "http://192.168.31.80:8080/Login/Login";
                    OkHttpClient client = new OkHttpClient();
                    FormBody.Builder builder = new FormBody.Builder();
                    builder.add("id", account);
                    builder.add("password", password);
                    FormBody body = builder.build();
                    Request request = new Request.Builder().url(path).post(body).build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                            Looper.prepare();
//                            Toast.makeText(Login.this, "服务器连接出现错误", Toast.LENGTH_SHORT).show();
//                            Looper.loop();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Looper.prepare();
                            if (("True").equals(response.body().string())) {
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
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}