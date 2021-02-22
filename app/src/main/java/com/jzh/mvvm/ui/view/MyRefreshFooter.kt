package com.jzh.mvvm.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.jzh.mvvm.R
import com.scwang.smart.refresh.footer.ClassicsFooter

/**
 * 设置自定义的ClassicsHeader样式
 * 用的是第三方recyclerview自带的加载更多，这个暂时没用
 * Created by jzh on 2020-12-29.
 */
class MyRefreshFooter(context: Context, attrs: AttributeSet? = null) :
    ClassicsFooter(context, attrs) {

    init {
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        setPrimaryColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        setAccentColor(ContextCompat.getColor(context, R.color.white))
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(ev)
    }
}