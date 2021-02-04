package com.jzh.mvvm.base

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jzh.mvvm.R
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.utils.DensityUtil
import com.jzh.mvvm.utils.MyMMKV.Companion.mmkv
import com.jzh.mvvm.utils.StatusBarUtils.getStatusBarHeight
import com.jzh.mvvm.utils.StatusBarUtils.setWindowStatusTransparent
import com.jzh.mvvm.utils.getRotateAnimation
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.toolbar
import kotlinx.android.synthetic.main.toolbar_layout.toolbar_left_image_back
import kotlinx.android.synthetic.main.toolbar_layout.toolbar_subtitle
import kotlinx.android.synthetic.main.toolbar_layout.toolbar_subtitle_image
import kotlinx.android.synthetic.main.toolbar_layout_search.*

abstract class BaseActivity : AppCompatActivity() {

    val TAG = javaClass.simpleName

    /**
     * 列表接口每页请求的条数
     */
    val pageSize = 20

    /**
     * 布局文件id
     */
    abstract fun getLayoutId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 初始化 View
     */
    abstract fun initView()

    /**
     * 开始请求
     */
    abstract fun startHttp()

    /**
     * 无网状态—>有网状态 的自动重连操作，子类可重写该方法
     */
    open fun doReConnected() {
        LiveEventBus.get("isConnected", Boolean::class.java).observe(this, {
            if (it) startHttp()
        })
    }

    open fun showLoading() {
        toolbar_title?.visibility = View.GONE
        toolbar_loading?.visibility = View.VISIBLE
        toolbar_loading?.startAnimation(getRotateAnimation(0f, 360f))
    }

    open fun hideLoading() {
        toolbar_title?.visibility = View.VISIBLE
        toolbar_loading?.visibility = View.GONE
        toolbar_loading?.clearAnimation()
    }

    open fun isLoading() = toolbar_loading?.visibility == View.VISIBLE

    /**
     * 这里只适配了搜索按钮是图片的情况，是文字的话需要判断下
     */
    open fun showSearchLoading() {
        toolbar_subtitle_image?.visibility = View.GONE
        toolbar_loading_search?.visibility = View.VISIBLE
        toolbar_loading_search?.startAnimation(getRotateAnimation(0f, 360f))
    }

    open fun hideSearchLoading() {
        toolbar_subtitle_image?.visibility = View.VISIBLE
        toolbar_loading_search?.visibility = View.GONE
        toolbar_loading_search?.clearAnimation()
    }

    open fun isSearchLoading() = toolbar_loading_search?.visibility == View.VISIBLE

    override fun onCreate(savedInstanceState: Bundle?) {
        //防止输入法顶起底部布局
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
        //0是默认值
        if (mmkv.decodeInt("max_size") == 0) {
            mmkv.encode("max_size", resources.displayMetrics.heightPixels)
        }
        //设置状态栏透明
        setWindowStatusTransparent(this)
        if (getLayoutId() > 0) setContentView(getLayoutId())
        initData()
        initView()
        doReConnected()
    }

    //设置顶部toolbar相应样式
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    open fun setTop(
        title: String, subTitle: Any? = null, isBack: (() -> Unit)? = {
            toolbar_left_image_back?.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.write_back)
            )
            toolbar_left_image_back?.setOnClickListener { onBackPressed() }
        }
    ) {
        toolbar_title?.text = title
        toolbar_title?.isSelected = true
        //小窗模式不计算状态栏的高度
        if (!DensityUtil.isSmallWindow(this)) {
            val layoutParams = toolbar?.layoutParams
            layoutParams?.height =
                getStatusBarHeight(this) + DensityUtil.dip2px(this, Constant.TOOLBAR_HEIGHT)
            toolbar?.layoutParams = layoutParams
        }
        //默认显示返回按钮
        isBack?.invoke()
        //根据subtitle的数据类型来显示图片或文字
        when (subTitle) {
            is String -> {
                toolbar_subtitle_image?.visibility = View.GONE
                toolbar_subtitle?.visibility = View.VISIBLE
                toolbar_subtitle?.text = subTitle
            }
            is Int -> {
                toolbar_subtitle?.visibility = View.GONE
                toolbar_subtitle_image?.visibility = View.VISIBLE
                toolbar_subtitle_image?.setImageResource(subTitle)
            }
            else -> {
                toolbar_subtitle?.visibility = View.GONE
                toolbar_subtitle_image?.visibility = View.GONE
            }
        }
    }

    //设置顶部toolbar相应样式
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    open fun setSearchTop(
        subTitle: Any? = null, isBack: (() -> Unit)? = {
            toolbar_left_image_back.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.write_back)
            )
            toolbar_left_image_back.setOnClickListener { onBackPressed() }
        }
    ) {
        toolbar_search_title?.isSelected = true
        //小窗模式不计算状态栏的高度
        if (!DensityUtil.isSmallWindow(this)) {
            val layoutParams = toolbar?.layoutParams
            layoutParams?.height =
                getStatusBarHeight(this) + DensityUtil.dip2px(this, Constant.TOOLBAR_HEIGHT)
            toolbar?.layoutParams = layoutParams
        }
        //默认显示返回按钮
        isBack?.invoke()
        //根据subtitle的数据类型来显示图片或文字
        when (subTitle) {
            is String -> {
                toolbar_subtitle_image?.visibility = View.GONE
                toolbar_subtitle?.visibility = View.VISIBLE
                toolbar_subtitle?.text = subTitle
            }
            is Int -> {
                toolbar_subtitle?.visibility = View.GONE
                toolbar_subtitle_image?.visibility = View.VISIBLE
                toolbar_subtitle_image?.setImageResource(subTitle)
            }
            else -> {
                toolbar_subtitle?.visibility = View.GONE
                toolbar_subtitle_image?.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoading()
        hideSearchLoading()
    }
}