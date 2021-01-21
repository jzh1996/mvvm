package com.jzh.mvvm.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jzh.mvvm.base.BaseApplication

/**
 * 自定义Toast
 * Created by jzh on 2021-1-12.
 */
fun Context.toast(resId: Int) {
    val toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT)
    toast.run {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Context.toast(text: CharSequence) {
    val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
    toast.run {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Context.longToast(resId: Int) {
    val toast = Toast.makeText(this, resId, Toast.LENGTH_LONG)
    toast.run {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Context.longToast(text: CharSequence) {
    val toast = Toast.makeText(this, text, Toast.LENGTH_LONG)
    toast.run {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun View.toast(resId: Int) = context.toast(resId)

fun View.toast(text: CharSequence) = context.toast(text)

fun View.longToast(resId: Int) = context.longToast(resId)

fun View.longToast(text: CharSequence) = context.longToast(text)

fun Fragment.toast(resId: Int) = (activity ?: BaseApplication.mContext).toast(resId)

fun Fragment.toast(text: CharSequence) = (activity ?: BaseApplication.mContext).toast(text)

fun Fragment.longToast(resId: Int) = (activity ?: BaseApplication.mContext).longToast(resId)

fun Fragment.longToast(text: CharSequence) = (activity ?: BaseApplication.mContext).longToast(text)
