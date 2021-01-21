package com.jzh.mvvm.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.jzh.mvvm.R
import com.scwang.smart.refresh.header.ClassicsHeader

/**
 * 设置自定义的ClassicsHeader样式
 * Created by jzh on 2020-12-29.
 */
class MyRefreshHeader(context: Context, attrs: AttributeSet? = null) :
    ClassicsHeader(context, attrs) {

    init {
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        setPrimaryColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        setAccentColor(ContextCompat.getColor(context, R.color.white))
    }
}