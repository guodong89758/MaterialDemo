package com.guo.material.utils;

import android.content.Context;
import android.content.res.Resources;

/**
 * 设备相关工具类.
 */
public class PhoneUtil
{

    public static int dipToPixel(float dp, Context mContext)
    {
        float scale = mContext.getResources().getDisplayMetrics().density;
        int pixel = (int) (dp * scale + 0.5f);
        return pixel;
    }

    public static float pixelToDip(int pixel, Context mContext)
    {
        float scale = mContext.getResources().getDisplayMetrics().density;
        float dp = (float) (pixel - 0.5f) / scale;
        return dp;
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}