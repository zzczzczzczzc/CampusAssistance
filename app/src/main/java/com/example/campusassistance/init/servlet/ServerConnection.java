package com.example.campusassistance.init.servlet;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ServerConnection {

    public static String connection(String path, String params) {
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

//            if (!TextUtils.isEmpty(params)) {
                DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                dataOutputStream.write(params.getBytes("UTF-8"));
                dataOutputStream.flush();
                dataOutputStream.close();
//            }

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

            return responseText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
