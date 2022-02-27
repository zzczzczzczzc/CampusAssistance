package com.example.campusassistance.account.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.campusassistance.R;
import com.example.campusassistance.account.servlet.ChangePassword;
import com.example.campusassistance.account.servlet.Login;

public class MineFragment extends Fragment implements View.OnClickListener {

    private boolean mIsLogin = false;
    private String mUserId = "";
    private SharedPreferences sp;
    private final int mRequestCode = 0;
    private final int mResultCode = 1;

    private Button mLogin;
    private RelativeLayout mAvatarAndName;
    private LinearLayout mReleaseGoods;
    private LinearLayout mReleasedRecord;
    private LinearLayout mChangePassword;
    private LinearLayout mLogOut;
    private TextView mName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_fragment, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        sp = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);

        mLogin = (Button) view.findViewById(R.id.bt_login);
        mAvatarAndName = (RelativeLayout) view.findViewById(R.id.rl_avatar_and_name);
        mReleaseGoods = (LinearLayout) view.findViewById(R.id.ll_release_goods);
        mReleasedRecord = (LinearLayout) view.findViewById(R.id.ll_released_record);
        mChangePassword = (LinearLayout) view.findViewById(R.id.ll_change_password);
        mLogOut = (LinearLayout) view.findViewById(R.id.ll_log_out);
        mName = (TextView) view.findViewById(R.id.tv_name);

        mLogin.setOnClickListener(this);
        mAvatarAndName.setOnClickListener(this);
        mReleaseGoods.setOnClickListener(this);
        mReleasedRecord.setOnClickListener(this);
        mChangePassword.setOnClickListener(this);
        mLogOut.setOnClickListener(this);

        reflashLoginState();
    }

    private void reflashLoginState() {
        mIsLogin = sp.getBoolean("isLogin", false);
        if (!mIsLogin) {
            mAvatarAndName.setVisibility(View.GONE);
            mLogin.setVisibility(View.VISIBLE);
        } else {
            mAvatarAndName.setVisibility(View.VISIBLE);
            mLogin.setVisibility(View.GONE);
            mName.setText(sp.getString("userId", "昵称显示异常"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                Intent intent = new Intent(getActivity(), Login.class);
                startActivityForResult(intent, mRequestCode);
                break;
            case R.id.ll_release_goods:
                if (!mIsLogin) {
                    toastLoginTip();
                } else {

                }
                break;
            case R.id.ll_released_record:
                if (!mIsLogin) {
                    toastLoginTip();
                } else {

                }
                break;
            case R.id.ll_change_password:
                if (!mIsLogin) {
                    toastLoginTip();
                } else {
                    Intent intent1 = new Intent(getActivity(), ChangePassword.class);
                    startActivity(intent1);
                }
                break;
            case R.id.ll_log_out:
                logOut();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == mRequestCode && resultCode == mResultCode) {
            reflashLoginState();
        }
    }

    private void toastLoginTip() {
        Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
    }

    private void logOut() {
        if (!mIsLogin) {
            toastLoginTip();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.tip));
            builder.setMessage(getString(R.string.is_sure_log_out));
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isLogin", false);
                    editor.putString("userId", "");
                    editor.commit();
                    reflashLoginState();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
