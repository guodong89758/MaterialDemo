package com.guo.material.app;

import android.app.Application;

import com.guo.material.RT;
import com.karumi.dexter.Dexter;

/**
 * Created by guodong on 2016/8/31 16:55.
 */
public class MaterialApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RT.application = this;
        Dexter.initialize(this);
    }
}
