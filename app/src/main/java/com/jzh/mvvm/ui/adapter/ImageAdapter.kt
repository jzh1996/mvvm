package com.jzh.mvvm.ui.adapter

import android.content.Context
import com.jzh.mvvm.httpUtils.Banner
import com.jzh.mvvm.utils.ImageLoader
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder

/**
 * Banner适配器
 * Created by jzh on 2021-1-6.
 */
open class ImageAdapter(private val context: Context, imgList: List<Banner>) :
    BannerImageAdapter<Banner>(imgList) {

    override fun onBindView(holder: BannerImageHolder?, data: Banner?, position: Int, size: Int) {
        ImageLoader.loadBanner(context, data?.imagePath, holder?.imageView)
    }
}