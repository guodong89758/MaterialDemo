package com.guo.material.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.guo.material.R;
import com.guo.material.widget.ViewDraggerLayout;
import com.guo.material.widget.reveal.RevealBackgroundView;

/**
 * Created by admin on 2016/2/23.
 */
public class FriendFragment extends Fragment implements RevealBackgroundView.OnStateChangeListener {

    private RevealBackgroundView reveal_view;
    private ViewDraggerLayout dragger_layout;
    private int screen_width, screen_height;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reveal_view = (RevealBackgroundView) view.findViewById(R.id.reveal_view);
        dragger_layout = (ViewDraggerLayout) view.findViewById(R.id.dragger_layout);
        dragger_layout.setVisibility(View.GONE);
        reveal_view.setOnStateChangeListener(this);
        reveal_view.setFillPaintColor(getResources().getColor(R.color.rect));
        screen_width = getResources().getDisplayMetrics().widthPixels;
        screen_height = getResources().getDisplayMetrics().heightPixels;
        reveal_view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                reveal_view.getViewTreeObserver().removeOnPreDrawListener(this);
                reveal_view.startFromLocation(new int[]{screen_width / 2, screen_height / 2});
                return true;
            }
        });
    }

    @Override
    public void onStateChange(int state) {
        switch (state) {
            case RevealBackgroundView.STATE_FILL_STARTED:
                break;
            case RevealBackgroundView.STATE_FINISHED:
                dragger_layout.setVisibility(View.VISIBLE);
                break;
        }
    }
}
