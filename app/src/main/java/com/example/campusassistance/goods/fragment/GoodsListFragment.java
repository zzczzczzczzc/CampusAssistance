package com.example.campusassistance.goods.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusassistance.R;
import com.example.campusassistance.goods.adapter.GoodsListAdapter;

import java.util.ArrayList;
import java.util.List;

public class GoodsListFragment extends Fragment {

    private RecyclerView mGoodsListView;

    private List<String> mDataList;
    private RecyclerView.LayoutManager mLayoutManager;
    private GoodsListAdapter mGoodListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goods_fragment, container, false);
        initView(view);
        mDataList = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            mDataList.add("item" + i);
        }
        mGoodsListView.setLayoutManager(new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL, false));
        mGoodListAdapter = new GoodsListAdapter(mDataList);
        mGoodsListView.setAdapter(mGoodListAdapter);
        return view;
    }

    private void initView(View view) {
        mGoodsListView = (RecyclerView) view.findViewById(R.id.rv_goods);
    }

}
