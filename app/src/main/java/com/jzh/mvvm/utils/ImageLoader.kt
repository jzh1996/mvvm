package com.jzh.mvvm.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseApplication
import java.io.File

object ImageLoader {

    /**
     * 加载图片
     * @param context
     * @param url
     * @param iv
     */
    fun load(context: Context?, url: String?, iv: ImageView?) {
        // 1.开启无图模式 2.非WiFi环境 不加载图片
        if (!SettingUtil.getIsNoPhotoMode() || NetWorkUtil.isWifi(BaseApplication.mContext)) {
            iv?.apply {
                visibility = View.VISIBLE
                Glide.with(context ?: BaseApplication.mContext).clear(iv)
                val options = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .placeholder(R.drawable.bg_placeholder)
                Glide.with(context ?: BaseApplication.mContext)
                    .load(url)
                    .transition(DrawableTransitionOptions().crossFade())
                    .apply(options)
                    .into(iv)
            }
        } else {
            iv?.visibility = View.GONE
        }
    }

    /**
     * 开启了无图模式也要加载图片的情况下使用
     */
    fun loadBanner(context: Context?, url: String?, iv: ImageView?) {
        iv?.apply {
            Glide.with(context ?: BaseApplication.mContext).clear(iv)
            val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.bg_placeholder)
            Glide.with(context ?: BaseApplication.mContext)
                .load(url)
                .transition(DrawableTransitionOptions().crossFade())
                .apply(options)
                .into(iv)
        }
    }

    /**
     * 禁用Glide缓存的时候使用;如头像,因为头像是用的同一路径，Glide会缓存同一地址的图片而导致不更新
     */
    fun loadByNoCache(context: Context?, file: File?, iv: ImageView?) {
        if (iv == null) return
        Glide.with(context ?: BaseApplication.mContext).clear(iv)
        Glide.with(context ?: BaseApplication.mContext).load(file)
            .skipMemoryCache(true)//跳过内存缓存
            .diskCacheStrategy(DiskCacheStrategy.NONE)//不要在disk硬盘缓存
            .placeholder(R.drawable.bg_placeholder).dontAnimate()
            .error(R.drawable.bg_placeholder)
            .into(iv)
    }
}