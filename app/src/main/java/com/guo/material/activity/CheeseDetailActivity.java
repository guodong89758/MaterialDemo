package com.guo.material.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.guo.material.R;
import com.guo.material.widget.reveal.RevealFrameLayout;
import com.guo.material.widget.reveal.animation.SupportAnimator;
import com.guo.material.widget.reveal.animation.ViewAnimationUtils;

/**
 * Created by admin on 2016/3/1.
 */
public class CheeseDetailActivity extends AppCompatActivity implements ViewTreeObserver.OnPreDrawListener {
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";
    private RevealFrameLayout reveal_layout;
    private CoordinatorLayout detail_content;
    private CollapsingToolbarLayout collapsing_toolbar;
    private Toolbar toolbar;
    private ImageView iv_cheese;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheese_detail);
        reveal_layout = (RevealFrameLayout) findViewById(R.id.reveal_layout);
        detail_content = (CoordinatorLayout) findViewById(R.id.detail_content);
        collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_cheese = (ImageView) findViewById(R.id.iv_cheese);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        detail_content.getViewTreeObserver().addOnPreDrawListener(this);
        String image_url = getIntent().getStringExtra("image");
        Glide.with(this).load(image_url).placeholder(R.drawable.ic_def_cover).into(iv_cheese);
//        iv_cheese.setImageResource(resId);

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

    @Override
    public boolean onPreDraw() {
        detail_content.bringToFront();
        detail_content.getViewTreeObserver().removeOnPreDrawListener(this);
        startRevealTransition();
        return true;
    }

    public void startRevealTransition() {
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(detail_content, detail_content.getRight(), detail_content.getBottom(), 0, hypo(detail_content.getHeight(), detail_content.getWidth()), View.LAYER_TYPE_SOFTWARE);
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    public static float hypo(float a, float b) {
        return (float) Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }
}
