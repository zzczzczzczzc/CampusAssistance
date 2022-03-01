package com.example.campusassistance.init.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.campusassistance.R;
import com.example.campusassistance.account.fragment.MineFragment;
import com.example.campusassistance.goods.fragment.GoodsListFragment;
import com.example.campusassistance.lost.fragment.LostListFragment;

public class NavigationFragment extends Fragment implements View.OnClickListener {

    private LinearLayout mGoods;
    private LinearLayout mLostAndFound;
    private LinearLayout mMine;
    private FragmentManager mFragmentManager;

    private ImageView iv_goods;
    private TextView tv_goods;
    private ImageView iv_lost_and_found;
    private TextView tv_lost_and_found;
    private ImageView iv_mine;
    private TextView tv_mine;

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
        return view;
    }

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

        mFragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_goods:
                replaceFragment(new GoodsListFragment());
                isGoodsSelect(true);
                isLostAndFoundSelect(false);
                isMineSelect(false);
                break;
            case R.id.ll_lost_and_found:
                replaceFragment(new LostListFragment());
                isGoodsSelect(false);
                isLostAndFoundSelect(true);
                isMineSelect(false);
                break;
            case R.id.ll_mine:
                replaceFragment(new MineFragment());
                isGoodsSelect(false);
                isLostAndFoundSelect(false);
                isMineSelect(true);
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
}
