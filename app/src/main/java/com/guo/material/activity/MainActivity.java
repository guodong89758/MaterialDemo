package com.guo.material.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.guo.material.R;
import com.guo.material.fragment.FriendFragment;
import com.guo.material.fragment.HomeFragment;
import com.guo.material.fragment.SensorFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawer_layout;
    private NavigationView navigation_view;
    private FrameLayout fl_container;
    private HomeFragment homeFragment;
    private FriendFragment friendFragment;
    private SensorFragment sensorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigation_view = (NavigationView) findViewById(R.id.navigation_view);
        fl_container = (FrameLayout) findViewById(R.id.fl_container);

        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fl_container, homeFragment).commit();

        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                drawer_layout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        if (homeFragment == null) {
                            homeFragment = new HomeFragment();
                        }
                        switchFragment(homeFragment);
                        break;
                    case R.id.nav_message:
                        if (sensorFragment == null) {
                            sensorFragment = new SensorFragment();
                        }
                        switchFragment(sensorFragment);
                        break;
                    case R.id.nav_friend:
                        if (friendFragment == null) {
                            friendFragment = new FriendFragment();
                        }
                        switchFragment(friendFragment);
                        break;
                    case R.id.nav_forum:
                        break;
                    case R.id.sub_setting:
                        break;
                    case R.id.sub_about:
                        break;
                }
                return true;
            }
        });
        navigation_view.setCheckedItem(R.id.nav_home);


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


    @Override
    public void onClick(View v) {

    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, fragment).commit();
    }
}
