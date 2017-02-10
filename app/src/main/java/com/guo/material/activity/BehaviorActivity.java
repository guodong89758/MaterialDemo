package com.guo.material.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guo.material.R;
import com.guo.material.adapter.CheeseAdapter;

public class BehaviorActivity extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {
    private AppBarLayout appbar;
    private ImageButton ib_back;
    private TextView tv_title, tv_name;
    private RecyclerView rv_list;
    private View title_line;
    private RelativeLayout rl_avatar;
    private RelativeLayout rl_title;
    private CheeseAdapter mAdapter;
    private static int[] resIds = {R.drawable.cheese_1, R.drawable.cheese_2, R.drawable.cheese_3, R.drawable.cheese_4, R.drawable.cheese_5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_name = (TextView) findViewById(R.id.tv_name);
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        title_line = findViewById(R.id.title_line);
        rl_avatar = (RelativeLayout) findViewById(R.id.rl_avatar);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);

        appbar.addOnOffsetChangedListener(this);
        ib_back.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(layoutManager);

        mAdapter = new CheeseAdapter(this, resIds);
        rv_list.setAdapter(mAdapter);

        tv_name.setText("异果嘉肴瑟瑟鲜");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage > 0.8) {
            title_line.setVisibility(View.VISIBLE);
        } else {
            title_line.setVisibility(View.GONE);
        }
        rl_title.setBackgroundColor(Color.argb((int) (255 * percentage), 255, 255, 255));
    }
}
