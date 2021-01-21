package com.jzh.mvvm.utils

import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView

object AnimatorUtils {

    /**
     * 创建一个从start到end平滑过渡的动画
     */
    fun doIntAnimator(view: TextView, start: Int, end: Int, duration: Long = 300) {
        val animator = ValueAnimator.ofInt(start, end)
        animator.run {
            addUpdateListener {
                view.text = String.format("%d", it.animatedValue)
            }
            setDuration(duration)
            interpolator = DecelerateInterpolator()
            start()
        }
    }
}