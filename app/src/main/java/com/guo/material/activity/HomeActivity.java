package com.guo.material.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.guo.material.R;
import com.guo.material.widget.MainContentLayout;
import com.guo.material.widget.SlidingDragLayout;

public class HomeActivity extends AppCompatActivity implements SlidingDragLayout.OnSlidingDragListener, View.OnClickListener {
    private SlidingDragLayout sliding_pane;
    private MainContentLayout main_content;
    private Button button1, button2, button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sliding_pane = (SlidingDragLayout) findViewById(R.id.sliding_pane);
        main_content = (MainContentLayout) findViewById(R.id.main_content);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        main_content.setSlidingDragLayout(sliding_pane);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        sliding_pane.setOnSlidingDragListener(this);
    }


    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    @Override
    public void draging(float percent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
            case R.id.button2:
            case R.id.button3:
                sliding_pane.close();
                break;
        }
    }
}
