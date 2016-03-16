package com.guo.material.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.GlideModule;

import java.io.File;

/**
 * Created by admin on 2016/3/16.
 */
public class GlideConfiguration implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        File file = StorageUtils.getOwnCacheDirectory(context, "material/image");
        builder.setDiskCache(new DiskLruCacheFactory(file.getAbsolutePath(), 50 * 1024 * 1024));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }

}
