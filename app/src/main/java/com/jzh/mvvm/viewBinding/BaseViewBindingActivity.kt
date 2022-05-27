package com.jzh.mvvm.viewBinding

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseRepository
import com.jzh.mvvm.base.BaseViewModel
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.databinding.ToolbarLayoutBinding
import com.jzh.mvvm.utils.DensityUtil
import com.jzh.mvvm.utils.StatusBarUtils
import com.jzh.mvvm.utils.isInvalidClick
import com.jzh.mvvm.utils.toast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import retrofit2.HttpException

/**
 * Created by jzh
 * 通用的基础Activity
 * @param VM 如果页面不需要调用接口就传CommViewModel,需要接口就传对应模块的ViewModel(如HomeViewModel)
 * @param VB 页面的XML布局对应的ViewBind
 */
abstract class BaseViewBindingActivity<VM : BaseViewModel, VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var viewModel: VM
    protected lateinit var binding: VB
    //TODO  自定义一个加载弹窗
    private lateinit var loadingDialog: Dialog

    /**
     * viewModel实例
     */
    abstract fun providerVMClass(): Class<VM>

    /**
     * 布局文件
     */
    abstract fun getViewBinding(): VB

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 初始化View
     */
    abstract fun initView()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        initVM()
        //禁用自动切换横竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //防止输入法顶起底部布局
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
        //设置状态栏透明
        StatusBarUtils.setWindowStatusTransparent(this)
        binding = getViewBinding()
        if (this::binding.isInitialized)
            setContentView(binding.root)
        initData()
        initView()
        startObserve()
    }

    private fun initVM() {
        providerVMClass().let {
            viewModel = ViewModelProvider(this)[it]
            lifecycle.addObserver(viewModel)
        }
    }

    fun showLoading(canCancel: Boolean = true) {
        //TODO 定义一个加载弹窗显示出来
    }

    fun hideLoading() {
        //TODO 隐藏弹窗
    }

    open fun isLoading() = if (this::loadingDialog.isInitialized) loadingDialog.isShowing else false

    private fun startObserve() {
        //处理一些通用异常，比如网络超时等
        viewModel.run {
            getError().observe(this@BaseViewBindingActivity) {
                hideLoading()
                requestError(it)
            }
            getFinally().observe(this@BaseViewBindingActivity) {
                requestFinally(it)
            }
        }
    }

    //接口请求完毕，子类可以重写此方法做一些操作
    open fun requestFinally(it: Int?) {}

    //接口请求出错，子类可以重写此方法做一些操作
    open fun requestError(it: Exception?) {
        //处理一些已知异常
        it?.run {
            when (it) {
                is CancellationException -> Log.d("--->接口请求取消", it.message.toString())
                is TimeoutCancellationException -> toast("请求超时")
                is BaseRepository.TokenInvalidException -> toast("登陆超时")
                is HttpException -> {
                    if (it.code() == 504) toast("无法连接服务器,请检查网络设置")
                    else toast(it.message.toString())
                }
                else -> toast(it.message.toString())
            }
        }
    }

    //设置顶部toolbar相应样式
    open fun setTop(
        toolBar: ToolbarLayoutBinding,
        title: String,
        subTitle: Any? = null,
        isBack: (() -> Unit)? = {
            toolBar.toolbarLeftImageBack.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.write_back)
            )
            toolBar.toolbarLeftImageBack.setOnClickListener {
                if (isInvalidClick(it)) return@setOnClickListener
                onBackPressed()
            }
        },
    ) {
        toolBar.toolbarTitle.text = title
        toolBar.toolbarTitle.isSelected = true
        //根据不同设备的状态栏高度设置不同的顶部toolbar高度
        val layoutParams = toolBar.toolbar.layoutParams
        layoutParams?.height =
            StatusBarUtils.getStatusBarHeight(this) + DensityUtil.dip2px(this,
                Constant.TOOLBAR_HEIGHT)
        toolBar.toolbar.layoutParams = layoutParams
        //默认显示返回按钮
        isBack?.invoke()
        //根据subtitle的数据类型来显示图片或文字
        when (subTitle) {
            is String -> {
                toolBar.toolbarSubtitleImage.visibility = View.GONE
                toolBar.toolbarSubtitle.visibility = View.VISIBLE
                toolBar.toolbarSubtitle.text = subTitle
            }
            is Int -> {
                toolBar.toolbarSubtitle.visibility = View.GONE
                toolBar.toolbarSubtitleImage.visibility = View.VISIBLE
                toolBar.toolbarSubtitleImage.setImageResource(subTitle)
            }
            else -> {
                toolBar.toolbarSubtitle.visibility = View.GONE
                toolBar.toolbarSubtitleImage.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoading()
        lifecycle.removeObserver(viewModel)
    }
}