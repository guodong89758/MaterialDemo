package com.guo.material.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.guo.material.R;

/**
 * Created by admin on 2016/3/1.
 */
public class CheeseDetailActivity extends AppCompatActivity {
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";
    private CollapsingToolbarLayout collapsing_toolbar;
    private Toolbar toolbar;
    private ImageView iv_cheese;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheese_detail);
        collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_cheese = (ImageView) findViewById(R.id.iv_cheese);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int resId = getIntent().getIntExtra("image", R.drawable.cheese_1);
        iv_cheese.setImageResource(resId);

        collapsing_toolbar.setTitle("cheese");
        ViewCompat.setTransitionName(iv_cheese, VIEW_NAME_HEADER_IMAGE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
