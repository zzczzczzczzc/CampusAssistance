package com.example.campusassistance.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.campusassistance.R;

import java.util.Base64;

public class StringToBitmap {

    public static Bitmap stringToBitmap(Context context, String str) {
        byte[] imageData = Base64.getDecoder().decode(str);
        Bitmap imageBitmap = null;
        if (imageData.length > 0) {
            //将字节数组转化为位图
            imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        } else {
            //没有上传图片，使用系统默认图片
            imageBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar);
        }
        return imageBitmap;
    }
}
