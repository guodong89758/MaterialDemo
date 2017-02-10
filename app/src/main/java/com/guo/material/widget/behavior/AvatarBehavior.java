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

public class AvatarBehavior extends CoordinatorLayout.Behavior {
    private Context mContext;
    private int startSize = 0;
    private int finalSize = 0;
    private int startXPosition = 0;
    private int startYPosition = 0;
    private float maxScroll = 0;
    private float percentage = 0;

    public AvatarBehavior(Context context, AttributeSet attr) {
        super(context, attr);
        this.mContext = context;
        startSize = PhoneUtil.dipToPixel(90, context);
        finalSize = PhoneUtil.dipToPixel(50, context);
        startXPosition = (PhoneUtil.getScreenWidth(context) - startSize) / 2;
        startYPosition = (PhoneUtil.dipToPixel(226, context) - startSize) / 2;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
        child.offsetTopAndBottom(startYPosition);
        child.offsetLeftAndRight(startXPosition);
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
        child.setTranslationY(-(startYPosition + (startSize - finalSize) / 2) * percentage);
        child.setTranslationX(-(startXPosition - PhoneUtil.dipToPixel(40, mContext) + (startSize - finalSize) / 2) * percentage);
        percentage = 1 - ((float) finalSize / startSize) * percentage;
        child.setScaleX(percentage);
        child.setScaleY(percentage);
        return true;
    }

    private void initProperties(View dependency) {
        if (maxScroll == 0) {
            maxScroll = dependency.getY();
        }
    }
}
