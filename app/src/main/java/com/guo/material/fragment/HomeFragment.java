package com.guo.material.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.guo.material.R;
import com.guo.material.adapter.CheeseListAdapter;

/**
 * Created by admin on 2016/2/23.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private Toolbar toolbar;
    private TabLayout tab_layout;
    private ViewPager viewpager;
    private FloatingActionButton float_button;
    private CheeseListAdapter mPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.include_drawer_content, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        tab_layout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        float_button = (FloatingActionButton) view.findViewById(R.id.float_button);

        float_button.setOnClickListener(this);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setupViewPager();

        tab_layout.setupWithViewPager(viewpager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.float_button:
                Snackbar.make(v, "this is a snackbar", Snackbar.LENGTH_LONG).setAction("点击Snackbar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "点击Snackbar", Toast.LENGTH_SHORT).show();
                    }
                }).show();
                break;
        }
    }

    private void setupViewPager() {
        mPagerAdapter = new CheeseListAdapter(getChildFragmentManager());
        mPagerAdapter.addFragment(new CheeseListFragment(), "title1");
        mPagerAdapter.addFragment(new ScrollingImageFragment(), "title2");
        mPagerAdapter.addFragment(new CheeseListFragment(), "title3");
        mPagerAdapter.addFragment(new CheeseListFragment(), "title4");
        mPagerAdapter.addFragment(new CheeseListFragment(), "title5");
        viewpager.setAdapter(mPagerAdapter);
        viewpager.setCurrentItem(0);
    }
}