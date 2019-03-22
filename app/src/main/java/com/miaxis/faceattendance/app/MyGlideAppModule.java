package com.miaxis.faceattendance.app;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * Created by tang.yf on 2018/8/22.
 */

/**
 * 设置缓存路径
 */
@GlideModule
public class MyGlideAppModule extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
//        int memoryCacheSizeBytes = 1024 * 1024 * 100; // 100mb
//        //        设置内存缓存大小
//        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
//        builder.setDiskCache(new DiskLruCacheFactory(FileUtil.IMG_PATH, memoryCacheSizeBytes));
    }

    //    针对V4用户可以提升速度
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

}
