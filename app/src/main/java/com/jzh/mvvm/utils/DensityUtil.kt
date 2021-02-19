package com.jzh.mvvm.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import com.jzh.mvvm.utils.MyMMKV.Companion.mmkv

object DensityUtil {
    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Int): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dpValue.toFloat() * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 很多手机都可以从屏幕旁边拖动出小窗应用
     * 所以这时候设置toolbar的高度不用计算状态栏的高度
     * Created by jzh on 2021-1-14.
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun isSmallWindow(activity: Activity): Boolean {
        //这个是拿到的当前activity的宽高
        val metrics = activity.resources.displayMetrics
        //保证存的是屏幕宽度最大值
        if (metrics.heightPixels > mmkv.decodeInt("max_size")) {
            mmkv.encode("max_size", (metrics.heightPixels))
        }
        //拿到的宽始终是一样的，高度也一样，但是小屏的高度比全屏小，所以这里首次存一下高度，后面用高度判断
        return metrics.heightPixels < mmkv.decodeInt("max_size", metrics.heightPixels)
        //下面的方法拿到的宽高始终一样
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            metrics.widthPixels < activity.windowManager.currentWindowMetrics.bounds.width() || metrics.heightPixels < activity.windowManager.currentWindowMetrics.bounds.height()
//        } else {
//            metrics.widthPixels < BaseApplication.mContext.resources.displayMetrics.widthPixels || metrics.heightPixels < BaseApplication.mContext.resources.displayMetrics.heightPixels
//        }
    }

    /**
     * 修改颜色透明度
     *
     * @param color
     * @param alpha
     * @return
     */
    fun changeColorAlpha(color: Int, alpha: Int): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    fun getAlphaPercent(argb: Int): Float {
        return Color.alpha(argb) / 255f
    }

    fun alphaValueAsInt(alpha: Float): Int {
        return Math.round(alpha * 255)
    }

    fun adjustAlpha(alpha: Float, color: Int): Int {
        return alphaValueAsInt(alpha) shl 24 or (0x00ffffff and color)
    }

    fun colorAtLightness(color: Int, lightness: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = lightness
        return Color.HSVToColor(hsv)
    }

    fun lightnessOfColor(color: Int): Float {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return hsv[2]
    }
}