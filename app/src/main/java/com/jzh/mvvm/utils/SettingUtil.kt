package com.jzh.mvvm.utils

import android.app.Activity
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseApplication
import com.tencent.mmkv.MMKV

object SettingUtil {

    private val setting = MMKV.defaultMMKV()

    /**
     * 获取是否开启无图模式
     */
    fun getIsNoPhotoMode() = setting.decodeBool("switch_no_img", false)
    fun setIsNoPhotoMode(isShow: Boolean) = setting.encode("switch_no_img", isShow)

    /**
     * 获取是否开启显示首页置顶文章
     */
    fun getIsShowTopArticle() = setting.decodeBool("switch_show_top", true)
    fun setIsShowTopArticle(isShow: Boolean) = setting.encode("switch_show_top", isShow)

    /**
     * 获取是否显示轮播图
     */
    fun getIsShowBanner() = setting.decodeBool("switch_show_banner", true)
    fun setIsShowBanner(isShow: Boolean) = setting.encode("switch_show_banner", isShow)

    /**
     * 获取是否显示底部导航角标
     */
    fun getIsShowBadge() = setting.decodeBool("switch_show_badge", true)
    fun setIsShowBadge(isShow: Boolean) = setting.encode("switch_show_badge", isShow)

    /**
     * 获取进入APP默认展示的页面
     */
    fun getDefaultPage(): String = setting.decodeString(
        "default_page", BaseApplication.mContext.resources.getString(
            R.string.home
        )
    )

    fun getDefaultPage(activity: Activity): Int {
        return when (getDefaultPage()) {
            activity.resources.getString(R.string.home) -> 0
            activity.resources.getString(R.string.system) -> 1
            activity.resources.getString(R.string.weixin) -> 2
            activity.resources.getString(R.string.question) -> 3
            activity.resources.getString(R.string.my) -> 4
            else -> 0
        }
    }

    fun setDefaultPage(pageStr: String) = setting.encode("default_page", pageStr)

    /**
     * 获取列表动画
     */
    fun getListAnimal() = setting.decodeString("list_animal", "无")
    fun setListAnimal(animal: String) = setting.encode("list_animal", animal)
}