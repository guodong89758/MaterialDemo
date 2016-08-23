package com.guo.material.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.guo.material.R;
import com.guo.material.activity.CheeseDetailActivity;
import com.guo.material.adapter.CheeseAdapter;

/**
 * Created by admin on 2016/2/16.
 */
public class CheeseListFragment extends Fragment implements CheeseAdapter.OnItemClickListener, View.OnTouchListener {

    private RecyclerView rv_cheese;
    private CheeseAdapter mAdapter;
    private Activity mContext;
    private static int[] resIds = {R.drawable.cheese_1, R.drawable.cheese_2, R.drawable.cheese_3, R.drawable.cheese_4, R.drawable.cheese_5};
    private int downX, downY;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cheese_list, null);
        rv_cheese = (RecyclerView) rootView.findViewById(R.id.rv_cheese);
        mContext = getActivity();
        rv_cheese.setOnTouchListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_cheese.setLayoutManager(layoutManager);

        mAdapter = new CheeseAdapter(getActivity(), resIds);
        mAdapter.setOnItemClickListener(this);
        rv_cheese.setAdapter(mAdapter);
    }

    @Override
    public void itemClick(View view, String url) {
        Intent intent = new Intent(getActivity(), CheeseDetailActivity.class);
        intent.putExtra("image", url);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                new Pair<View, String>(view.findViewById(R.id.iv_cheese), CheeseDetailActivity.VIEW_NAME_HEADER_IMAGE));
        ActivityCompat.startActivity(getActivity(), intent, optionsCompat.toBundle());
//        int[] location = new int[2];
//        view.getLocationOnScreen(location);
//        location[0] += view.getWidth()/2;
//        location[1] += view.getHeight()/2;
//        Intent intent = new Intent(getActivity(), OptionsActivity.class);
//        intent.putExtra("location", location);
//        startActivity(intent);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getRawX();
                downY = (int) event.getRawY();
                break;
        }
        return false;
    }
}
