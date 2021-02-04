package com.jzh.mvvm.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.jzh.mvvm.mvvm.viewModel.CommonViewModel
import com.jzh.mvvm.utils.toast
import kotlinx.coroutines.TimeoutCancellationException
import retrofit2.HttpException
import java.lang.Exception
import kotlin.coroutines.cancellation.CancellationException

abstract class BaseViewModelFragment<VM : CommonViewModel> : BaseFragment() {

    private val fragmentName = javaClass.simpleName
    protected lateinit var viewModel: VM

    abstract fun providerVMClass(): Class<VM>?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initVM()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initVM() {
        providerVMClass()?.let {
            viewModel = ViewModelProvider(this).get(it)
            lifecycle.addObserver(viewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::viewModel.isInitialized)
            lifecycle.removeObserver(viewModel)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        startObserve()
        startHttp()
    }

    private fun startObserve() {
        //处理一些通用异常，比如网络超时等
        viewModel.run {
            getError().observe(activity!!, {
                hideLoading()
                hideSearchLoading()
                requestError(it)
            })
            getFinally().observe(activity!!, {
                requestFinally(it)
            })
        }
    }

    open fun requestFinally(it: Int?) {

    }

    open fun requestError(it: Exception?) {
        //处理一些已知异常
        it?.run {
            when (it) {
                is CancellationException -> Log.d("${TAG}--->接口请求取消", it.message.toString())
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
}