package com.example.campusassistance.lost.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.campusassistance.R;
import com.example.campusassistance.account.activity.ReleaseGood;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReleaseLostAndFoundGood extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ReleaseLostAndFoundGood";

    private ImageView picture;
    private ImageView addPicture;
    private EditText description;
    private EditText contactWay;
    private Button release;

    //图库请求码
    private final int REQUEST_CODE_ALBUM = 1001;
    //裁剪请求码
    private final int REQUEST_CODE_CROP = 1002;
    private Bitmap mBitmap;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_releas_lost_and_found_good);
        init();
        requestPermissions();
        addPicture.setOnClickListener(this);
        release.setOnClickListener(this);
    }

    private void init() {
        picture = findViewById(R.id.iv_lost_and_found_good_picture);
        addPicture = findViewById(R.id.iv_add_picture);
        description = findViewById(R.id.et_description);
        contactWay = findViewById(R.id.et_contact_way);
        release = findViewById(R.id.bt_release_good);

        userId = getSharedPreferences("Login", MODE_PRIVATE).getString("userId", "");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_release_good) {
            releaseMessage();
        } else if (v.getId() == R.id.iv_add_picture) {
            choosePictureFromAlbum();
        }
    }

    private void releaseMessage() {
        String telephone = contactWay.getText().toString().trim();
        String desciptionMessage = description.getText().toString().trim();
        String goodPicture = Base64.encodeToString(bitmapToByteArray(), Base64.NO_WRAP);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String path = "http://192.168.31.80:8080/LostAndFound/InsertLostAndFoundGoodMsg";
                OkHttpClient client = new OkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("userId", userId);
                builder.add("picture", goodPicture);
                builder.add("telephone", telephone);
                builder.add("description", desciptionMessage);
                FormBody body = builder.build();
                Request request = new Request.Builder().url(path).post(body).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        //TODO
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseText = response.body().string();
                        responseText = responseText.replace("\r", "");
                        responseText = responseText.replace("\n", "");
                        Looper.prepare();
                        if (responseText.equals("True")) {
                            Toast.makeText(ReleaseLostAndFoundGood.this, "发布成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ReleaseLostAndFoundGood.this, "发布失败", Toast.LENGTH_SHORT).show();
                        }
                        Looper.loop();
                        //TODO：此处finish无效
                        finish();
                    }
                });
            }
        }).start();
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
                cropPicture(selectedImageUri);
                break;
            case REQUEST_CODE_CROP:
                mBitmap = data.getParcelableExtra("data");
                picture.setImageBitmap(mBitmap);
                break;
            default:
                break;
        }
    }

    private void choosePictureFromAlbum() {
        if (!hasReadAndWritePermission()) {
            Toast.makeText(ReleaseLostAndFoundGood.this, "请在系统设置中打开存储权限", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent openPicturetent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openPicturetent, REQUEST_CODE_ALBUM);
    }

    private boolean hasReadAndWritePermission() {
        PackageManager pm = getPackageManager();
        if (checkCallingOrSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
                || checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
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

    //TODO：更改裁剪尺寸
    private void cropPicture(Uri uri) {
        Intent cropPictureIntent = new Intent("com.android.camera.action.CROP");
        cropPictureIntent.setDataAndType(uri, "image/*");
        cropPictureIntent.putExtra("crop", true);
        cropPictureIntent.putExtra("aspectX", 1);
        cropPictureIntent.putExtra("aspectY", 1);
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        cropPictureIntent.putExtra("outputY", height / 3);
        cropPictureIntent.putExtra("outputX", width);
//        cropPictureIntent.putExtra("outputX", (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics()));
//        cropPictureIntent.putExtra("outputY", (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height / 3, getResources().getDisplayMetrics()));
        cropPictureIntent.putExtra("return-data", true);
        cropPictureIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        cropPictureIntent.putExtra("noFaceDetection", true);
        startActivityForResult(cropPictureIntent, REQUEST_CODE_CROP);
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
}