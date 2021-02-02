package com.jzh.mvvm.utils

import com.chad.library.adapter.base.BaseQuickAdapter

object RvAnimUtils {

    fun getName(anim: Int): String {
        var name = ""
        when (anim) {
            RvAnim.NONE -> name = "无"
            RvAnim.ALPHAIN -> name = "渐显"
            RvAnim.SCALEIN -> name = "缩放"
            RvAnim.SLIDEIN_BOTTOM -> name = "底部滑入"
            RvAnim.SLIDEIN_LEFT -> name = "左侧滑入"
            RvAnim.SLIDEIN_RIGHT -> name = "右侧滑入"
            else -> {
            }
        }
        return name
    }

    fun getAnim(anim: String): Int {
        var name = RvAnim.NONE
        when (anim) {
            "无" -> name = RvAnim.NONE
            "渐显" -> name = RvAnim.ALPHAIN
            "缩放" -> name = RvAnim.SCALEIN
            "底部滑入" -> name = RvAnim.SLIDEIN_BOTTOM
            "左侧滑入" -> name = RvAnim.SLIDEIN_LEFT
            "右侧滑入" -> name = RvAnim.SLIDEIN_RIGHT
            else -> {
            }
        }
        return name
    }

    fun setAnim(adapter: BaseQuickAdapter<*, *>, anim: Any) {
        var anim1 = anim
        if (anim is String) {
            anim1 = getAnim(anim)
        }
        when (anim1) {
            RvAnim.NONE -> adapter.animationEnable = false
            RvAnim.ALPHAIN -> adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.AlphaIn)
            RvAnim.SCALEIN -> adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.ScaleIn)
            RvAnim.SLIDEIN_BOTTOM -> adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInBottom)
            RvAnim.SLIDEIN_LEFT -> adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInLeft)
            RvAnim.SLIDEIN_RIGHT -> adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInRight)
            else -> {
            }
        }
    }

    object RvAnim {
        const val NONE = 0
        const val ALPHAIN = 1
        const val SCALEIN = 2
        const val SLIDEIN_BOTTOM = 3
        const val SLIDEIN_LEFT = 4
        const val SLIDEIN_RIGHT = 5
    }
}
