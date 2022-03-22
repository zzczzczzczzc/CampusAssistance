package com.example.campusassistance.init.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.campusassistance.R;
import com.example.campusassistance.account.fragment.MineFragment;
import com.example.campusassistance.goods.fragment.GoodsListFragment;
import com.example.campusassistance.lost.fragment.LostAndFoundListFragment;
import com.example.campusassistance.message.fragment.MessageFragment;
import com.example.campusassistance.message.service.MessageService;

public class NavigationFragment extends Fragment implements View.OnClickListener {

    private LinearLayout mGoods;
    private LinearLayout mLostAndFound;
    private LinearLayout mMine;
    private LinearLayout mMessage;
    private FragmentManager mFragmentManager;

    private ImageView iv_goods;
    private TextView tv_goods;
    private ImageView iv_lost_and_found;
    private TextView tv_lost_and_found;
    private ImageView iv_mine;
    private TextView tv_mine;
    private ImageView iv_message;
    private TextView tv_message;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MessageService.MessageBinder binder = (MessageService.MessageBinder) service;
            MessageService messageService = binder.getService();
            messageService.setReceiveMsgCallback(new MessageService.ReceiveMsgCallback() {
                @Override
                public void toastReceiveMsg() {
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation_fragment, container, false);
        init(view);
        mGoods.setOnClickListener(this);
        mLostAndFound.setOnClickListener(this);
        mMine.setOnClickListener(this);
        mMessage.setOnClickListener(this);
        Intent intentService = new Intent(getActivity(), MessageService.class);
        getActivity().bindService(intentService, connection, Context.BIND_AUTO_CREATE);
        return view;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                Toast.makeText(getActivity(), "收到新的信息", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void init(View view) {
        mGoods = (LinearLayout) view.findViewById(R.id.ll_goods);
        iv_goods = (ImageView) view.findViewById(R.id.iv_goods);
        tv_goods = (TextView) view.findViewById(R.id.tv_goods);
        mLostAndFound = (LinearLayout) view.findViewById(R.id.ll_lost_and_found);
        iv_lost_and_found = (ImageView) view.findViewById(R.id.iv_lost_and_found);
        tv_lost_and_found = (TextView) view.findViewById(R.id.tv_lost_and_found);
        mMine = (LinearLayout) view.findViewById(R.id.ll_mine);
        iv_mine = (ImageView) view.findViewById(R.id.iv_mine);
        tv_mine = (TextView) view.findViewById(R.id.tv_mine);
        mMessage = (LinearLayout) view.findViewById(R.id.ll_message);
        iv_message = (ImageView) view.findViewById(R.id.iv_message);
        tv_message = (TextView) view.findViewById(R.id.tv_message);

        mFragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_goods:
                replaceFragment(new GoodsListFragment());
                isGoodsSelect(true);
                isLostAndFoundSelect(false);
                isMessageSelect(false);
                isMineSelect(false);
                break;
            case R.id.ll_lost_and_found:
                replaceFragment(new LostAndFoundListFragment());
                isGoodsSelect(false);
                isLostAndFoundSelect(true);
                isMessageSelect(false);
                isMineSelect(false);
                break;
            case R.id.ll_mine:
                replaceFragment(new MineFragment());
                isGoodsSelect(false);
                isLostAndFoundSelect(false);
                isMessageSelect(false);
                isMineSelect(true);
                break;
            case R.id.ll_message:
                replaceFragment(new MessageFragment());
                isGoodsSelect(false);
                isLostAndFoundSelect(false);
                isMessageSelect(true);
                isMineSelect(false);
                break;
            default:
                Toast.makeText(getActivity(), "程序出错，请重新启动", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_display, fragment);
        mFragmentTransaction.commit();
    }

    private void isGoodsSelect(boolean isSelect) {
        if (isSelect) {
            iv_goods.setBackground(getResources().getDrawable(R.drawable.goods_select));
            tv_goods.setTextColor(getResources().getColor(R.color.red));
        } else {
            iv_goods.setBackground(getResources().getDrawable(R.drawable.goods_no_select));
            tv_goods.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void isLostAndFoundSelect(boolean isSelect) {
        if (isSelect) {
            iv_lost_and_found.setBackground(getResources().getDrawable(R.drawable.lost_and_found_select));
            tv_lost_and_found.setTextColor(getResources().getColor(R.color.red));
        } else {
            iv_lost_and_found.setBackground(getResources().getDrawable(R.drawable.lost_and_found_no_select));
            tv_lost_and_found.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void isMineSelect(boolean isSelect) {
        if (isSelect) {
            iv_mine.setBackground(getResources().getDrawable(R.drawable.mine_select));
            tv_mine.setTextColor(getResources().getColor(R.color.red));
        } else {
            iv_mine.setBackground(getResources().getDrawable(R.drawable.mine_no_select));
            tv_mine.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void isMessageSelect(boolean isSelect) {
        if (isSelect) {
            iv_message.setBackground(getResources().getDrawable(R.drawable.message_select));
            tv_message.setTextColor(getResources().getColor(R.color.red));
        } else {
            iv_message.setBackground(getResources().getDrawable(R.drawable.messge_no_select));
            tv_message.setTextColor(getResources().getColor(R.color.black));
        }
    }
}
