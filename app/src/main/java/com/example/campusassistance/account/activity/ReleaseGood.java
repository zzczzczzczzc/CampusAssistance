package com.example.campusassistance.account.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.campusassistance.R;
import com.example.campusassistance.account.servlet.Login;
import com.example.campusassistance.init.servlet.ServerConnection;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ReleaseGood extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "ReleaseGood";
    //图库请求码
    private final int REQUEST_CODE_ALBUM = 1001;
    //裁剪请求码
    private final int REQUEST_CODE_CROP = 1002;
    private Bitmap mBitmap;
//    //图片存放绝对路径
//    private String imagePath = "D:/apache-tomcat-8.5.66/webapps/picture/goods/";
//    //图片名称
//    private String imageName;

    private EditText mTele;
    private EditText mPrices;
    private EditText mDescription;
    private ImageView mAddPicture;
    private Button mRelease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_good);
        init();
        requestPermissions();
        mAddPicture.setOnClickListener(this);
        mRelease.setOnClickListener(this);
    }

    private void init() {
        mTele = findViewById(R.id.et_tele);
        mPrices = findViewById(R.id.et_prices);
        mDescription = findViewById(R.id.et_description);
        mAddPicture = findViewById(R.id.iv_add_picture);
        mRelease = findViewById(R.id.bt_release_good);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_picture:
                choosePictureFromAlbum();
                break;
            case R.id.bt_release_good:
                release();
                break;
            default:
                break;
        }
    }

    private void choosePictureFromAlbum() {
        if (!hasReadAndWritePermission()) {
            Toast.makeText(ReleaseGood.this, "请在系统设置中打开存储权限", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent openPicturetent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openPicturetent, REQUEST_CODE_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_ALBUM:
                //获取图片路径
                Uri selectedImageUri = data.getData();
//                String[] filePathColumns = {MediaStore.Images.Media.DATA};
//                Cursor c = getContentResolver().query(selectedImageUri, filePathColumns, null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePathColumns[0]);
//                String photoPath = c.getString(columnIndex);
//                c.close();
//                imageName = getImageName(photoPath);
                //根据路径加载图片
//                mBitmap = BitmapFactory.decodeFile(photoPath);
                cropPicture(selectedImageUri);
                break;
            case REQUEST_CODE_CROP:
                mBitmap = data.getParcelableExtra("data");
//                bitmapToFile();
                mAddPicture.setImageBitmap(mBitmap);
                break;
            default:
                break;
        }
    }

//    private String getImageName(String path) {
//        String[] temp = path.split("/");
//        if (temp.length > 1) {
//            return temp[temp.length - 1];
//        }
//        return "";
//    }

//    private void bitmapToFile() {
//        try {
//            if (mBitmap != null) {
////                String path = imagePath + imageName;
//                String path = imagePath + "123.txt";
//                File file = new File(path);
//                file.createNewFile();
////                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
////                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
////                bos.flush();
////                bos.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    private void cropPicture(Uri uri) {
        Intent cropPictureIntent = new Intent("com.android.camera.action.CROP");
        cropPictureIntent.setDataAndType(uri, "image/*");
        cropPictureIntent.putExtra("crop", true);
        cropPictureIntent.putExtra("aspectX", 1);
        cropPictureIntent.putExtra("aspectY", 1);
        cropPictureIntent.putExtra("outputX", (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()));
        cropPictureIntent.putExtra("outputY", (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()));
        cropPictureIntent.putExtra("return-data", true);
        cropPictureIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        cropPictureIntent.putExtra("noFaceDetection", true);
        startActivityForResult(cropPictureIntent, REQUEST_CODE_CROP);
    }

    private void release() {
        String tele = mTele.getText().toString().trim();
        String prices = mPrices.getText().toString().trim();
        String description = mDescription.getText().toString().trim();
        String picture = Base64.encodeToString(bitmapToByteArray(), Base64.NO_WRAP);
        String userId = getSharedPreferences("Login", MODE_PRIVATE).getString("userId", "");
        String goodId = userId;

        if (TextUtils.isEmpty(tele) || TextUtils.isEmpty(prices) || TextUtils.isEmpty(description)
                || TextUtils.isEmpty(picture) || TextUtils.isEmpty(userId) || TextUtils.isEmpty(goodId)) {
            Toast.makeText(ReleaseGood.this, "请输入完整的信息", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = "http://192.168.31.80:8080/Goods/InsertGoodMessage";
                    StringBuilder params = new StringBuilder();
                    params.append("tele=" + URLEncoder.encode(tele, "UTF-8"))
                            .append("&prices=" + URLEncoder.encode(prices, "UTF-8"))
                            .append("&description=" + URLEncoder.encode(description, "UTF-8"))
                            .append("&userId=" + URLEncoder.encode(userId, "UTF-8"))
                            .append("&goodId=" + URLEncoder.encode(goodId, "UTF-8"))
                            .append("&picture=" + URLEncoder.encode(picture, "UTF-8"));

                    String responseText = ServerConnection.connection(path, params.toString());

                    Looper.prepare();
                    if ("True".equals(responseText)) {
                        Toast.makeText(ReleaseGood.this, "发布成功", Toast.LENGTH_SHORT).show();
                    } else if (("False").equals(responseText)) {
                        Toast.makeText(ReleaseGood.this, "发布失败，请重新发布", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ReleaseGood.this, "服务器连接出现错误", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private byte[] bitmapToByteArray() {
        if (mBitmap == null) {
            return new byte[0];
        }
        int size = mBitmap.getHeight() * mBitmap.getWidth() * 4;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size);
        try {
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] imageData = outputStream.toByteArray();
            return imageData;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mBitmap.recycle();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d(TAG, "写权限已授权");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d(TAG, "读权限已授权");
        }
    }

    private boolean hasReadAndWritePermission() {
        PackageManager pm = getPackageManager();
        if (checkCallingOrSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
                || checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private Bitmap byteToBitmap(byte[] imageData) {
        Bitmap imageBitmap = null;
        if (imageData.length > 0) {
            //将字节数组转化为位图
            imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        } else {
            //没有上传图片，使用系统默认图片
            imageBitmap = BitmapFactory.decodeResource(ReleaseGood.this.getResources(), R.drawable.avatar);
        }
        return imageBitmap;
    }
}