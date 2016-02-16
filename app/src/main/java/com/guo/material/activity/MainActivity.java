package com.guo.material.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.guo.material.R;
import com.guo.material.adapter.CheeseListAdapter;
import com.guo.material.fragment.CheeseListFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawer_layout;
    private Toolbar toolbar;
    private TabLayout tab_layout;
    private ViewPager viewpager;
    private FloatingActionButton float_button;
    private NavigationView navigation_view;
    private CheeseListAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        float_button = (FloatingActionButton) findViewById(R.id.float_button);
        navigation_view = (NavigationView) findViewById(R.id.navigation_view);

        float_button.setOnClickListener(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                drawer_layout.closeDrawers();
                return true;
            }
        });

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
                        Toast.makeText(getApplicationContext(), "点击Snackbar", Toast.LENGTH_SHORT).show();
                    }
                }).show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer_layout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager() {
        mPagerAdapter = new CheeseListAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new CheeseListFragment(), "title1");
        mPagerAdapter.addFragment(new CheeseListFragment(), "title2");
        mPagerAdapter.addFragment(new CheeseListFragment(), "title3");
        mPagerAdapter.addFragment(new CheeseListFragment(), "title4");
        mPagerAdapter.addFragment(new CheeseListFragment(), "title5");
        viewpager.setAdapter(mPagerAdapter);
        viewpager.setCurrentItem(0);
    }

}
