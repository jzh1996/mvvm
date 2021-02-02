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
}