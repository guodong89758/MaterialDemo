package com.guo.material.widget.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.guo.material.utils.PhoneUtil;

/**
 * Created by guodong on 2017/2/10 15:24.
 */

public class TitleBehavior extends CoordinatorLayout.Behavior {
    private Context mContext;
    private int startSize = 0;
    private int startYPosition = 0;
    private int textStartXPosition = 0;
    private int textStartYPosition = 0;
    private int titleHeight = 0;
    private float maxScroll = 0;
    private float percentage = 0;

    public TitleBehavior(Context context, AttributeSet attr) {
        super(context, attr);
        this.mContext = context;
        startSize = PhoneUtil.dipToPixel(90, context);
        startYPosition = (PhoneUtil.dipToPixel(226, context) - startSize) / 2;
        titleHeight = PhoneUtil.dipToPixel(50, context);
        textStartYPosition = startYPosition + startSize + PhoneUtil.dipToPixel(10, mContext);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
        textStartXPosition = (PhoneUtil.getScreenWidth(mContext) - child.getWidth()) / 2;
        child.offsetLeftAndRight(textStartXPosition);
        child.offsetTopAndBottom(textStartYPosition);
        return true;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof RecyclerView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        initProperties(dependency);
        percentage = 1 - dependency.getY() / maxScroll;
        child.setTranslationX(-(textStartXPosition - PhoneUtil.dipToPixel(90, mContext)) * percentage);
        child.setTranslationY(-(textStartYPosition - (titleHeight - child.getHeight()) / 2) * percentage);
        return true;
    }

    private void initProperties(View dependency) {
        if (maxScroll == 0) {
            maxScroll = dependency.getY();
        }
    }
}
