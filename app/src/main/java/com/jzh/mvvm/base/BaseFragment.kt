package com.jzh.mvvm.base

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jzh.mvvm.R
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.utils.DensityUtil
import com.jzh.mvvm.utils.StatusBarUtils
import com.jzh.mvvm.utils.getRotateAnimation
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.toolbar
import kotlinx.android.synthetic.main.toolbar_layout.toolbar_subtitle_image
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import kotlinx.android.synthetic.main.toolbar_layout_search.*

abstract class BaseFragment : Fragment() {

    val TAG = javaClass.simpleName
    open lateinit var mainView: View //fragment显示view
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
    abstract fun initView(view: View)

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
        activity?.toolbar_title?.visibility = View.GONE
        activity?.toolbar_loading?.visibility = View.VISIBLE
        activity?.toolbar_loading?.startAnimation(getRotateAnimation(0f, 360f))
    }

    open fun hideLoading() {
        activity?.toolbar_title?.visibility = View.VISIBLE
        activity?.toolbar_loading?.visibility = View.GONE
        activity?.toolbar_loading?.clearAnimation()
    }

    open fun isLoading() = activity?.toolbar_loading?.visibility == View.VISIBLE

    /**
     * 这里只适配了搜索按钮是图片的情况，是文字的话需要判断下
     */
    open fun showSearchLoading() {
        activity?.toolbar_subtitle_image?.visibility = View.GONE
        activity?.toolbar_loading_search?.visibility = View.VISIBLE
        activity?.toolbar_loading_search?.startAnimation(getRotateAnimation(0f, 360f))
    }

    open fun hideSearchLoading() {
        activity?.toolbar_subtitle_image?.visibility = View.VISIBLE
        activity?.toolbar_loading_search?.visibility = View.GONE
        activity?.toolbar_loading_search?.clearAnimation()
    }

    open fun isSearchLoading() = activity?.toolbar_loading_search?.visibility == View.VISIBLE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(getLayoutId(), null)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initData()
        doReConnected()
    }

    //设置顶部toolbar相应样式
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    open fun setTop(
        title: String, subTitle: Any?, isBack: (() -> Unit)? = {
            mainView.toolbar_left_image_back.setImageDrawable(
                ContextCompat.getDrawable(
                    activity ?: BaseApplication.mContext,
                    R.drawable.write_back
                )
            )
            mainView.toolbar_left_image_back.setOnClickListener { activity?.onBackPressed() }
        }
    ) {
        mainView.toolbar_title?.text = title
        mainView.toolbar_title?.isSelected = true
        mainView.toolbar_title?.setOnClickListener { startHttp() }
        if (!DensityUtil.isSmallWindow(activity!!)) {
            val layoutParams = toolbar?.layoutParams
            layoutParams?.height =
                StatusBarUtils.getStatusBarHeight(activity!!) + DensityUtil.dip2px(
                    activity!!,
                    Constant.TOOLBAR_HEIGHT
                )
            toolbar?.layoutParams = layoutParams
        }
        //默认显示返回按钮
        isBack?.invoke()
        //根据subtitle的数据类型来显示图片或文字
        when (subTitle) {
            is String -> {
                mainView.toolbar_subtitle_image?.visibility = View.GONE
                mainView.toolbar_subtitle?.visibility = View.VISIBLE
                mainView.toolbar_subtitle?.text = subTitle
            }
            is Int -> {
                mainView.toolbar_subtitle?.visibility = View.GONE
                mainView.toolbar_subtitle_image?.visibility = View.VISIBLE
                mainView.toolbar_subtitle_image?.setImageResource(subTitle)
            }
            else -> {
                mainView.toolbar_subtitle?.visibility = View.GONE
                mainView.toolbar_subtitle_image?.visibility = View.GONE
            }
        }
    }
}