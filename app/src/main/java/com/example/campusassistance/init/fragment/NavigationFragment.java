package com.example.campusassistance.init.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.campusassistance.R;
import com.example.campusassistance.account.fragment.AccountFragment;
import com.example.campusassistance.goods.fragment.GoodsListFragment;
import com.example.campusassistance.lost.fragment.LostListFragment;

public class NavigationFragment extends Fragment implements View.OnClickListener {

    private LinearLayout mGoods;
    private LinearLayout mLostAndFound;
    private LinearLayout mMine;
    private FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation_fragment, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        mGoods = (LinearLayout) view.findViewById(R.id.ll_goods);
        mLostAndFound = (LinearLayout) view.findViewById(R.id.ll_lost_and_found);
        mMine = (LinearLayout) view.findViewById(R.id.ll_mine);
        mGoods.setOnClickListener(this);
        mLostAndFound.setOnClickListener(this);
        mMine.setOnClickListener(this);
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_goods:
                replaceFragment(new GoodsListFragment());
                break;
            case R.id.ll_lost_and_found:
                replaceFragment(new LostListFragment());
                break;
            case R.id.ll_mine:
                replaceFragment(new AccountFragment());
                break;
            default:
                Toast.makeText(getActivity(), "程序出错，请重新启动", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.fragment_display, fragment);
        ft.commit();
    }

}
