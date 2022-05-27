package com.jzh.mvvm.viewBinding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import java.lang.Exception

/**
 * Created by jzh
 * 通用的基础Fragment
 * (参数同BaseActivity)
 */
abstract class BaseViewBindingFragment<VM : BaseViewModel, VB : ViewBinding> : Fragment() {

    protected lateinit var viewModel: VM
    protected lateinit var binding: VB
    val activity: BaseViewBindingActivity<*, *> by lazy { requireActivity() as BaseViewBindingActivity<*, *> }

    /**
     * viewModel实例
     */
    abstract fun providerVMClass(): Class<VM>?

    /**
     * 布局文件id
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = getViewBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initVM()
        initData()
        initView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initVM() {
        providerVMClass()?.let {
            viewModel = ViewModelProvider(this)[it]
            lifecycle.addObserver(viewModel)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        startObserve()
    }

    private fun startObserve() {
        //处理一些通用异常，比如网络超时等
        viewModel.run {
            getError().observe(activity) {
                requestError(it)
            }
            getFinally().observe(activity) {
                requestFinally(it)
            }
        }
    }

    open fun requestFinally(it: Int?) {

    }

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
                ContextCompat.getDrawable(activity, R.drawable.write_back)
            )
            toolBar.toolbarLeftImageBack.setOnClickListener {
                if (isInvalidClick(it)) return@setOnClickListener
                activity.onBackPressed()
            }
        },
    ) {
        toolBar.toolbarTitle.text = title
        toolBar.toolbarTitle.isSelected = true
        //根据不同设备的状态栏高度设置不同的顶部toolbar高度
        val layoutParams = toolBar.toolbar.layoutParams
        layoutParams?.height =
            StatusBarUtils.getStatusBarHeight(activity) + DensityUtil.dip2px(activity,
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
        if (this::viewModel.isInitialized)
            lifecycle.removeObserver(viewModel)
    }
}