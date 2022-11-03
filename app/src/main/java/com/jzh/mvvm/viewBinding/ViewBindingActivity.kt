package com.jzh.mvvm.viewBinding

import android.annotation.SuppressLint
import com.jzh.mvvm.databinding.ActivityViewBindingBinding
import com.jzh.mvvm.httpUtils.DataState
import com.jzh.mvvm.httpUtils.TodoResponseBody
import com.jzh.mvvm.mvvm.viewModel.CommonViewModel
import com.jzh.mvvm.utils.toast

/**
 * Created by jzh on 2022-05-27 10:11:51
 * Viewbinding示例
 * 网络请求也是用的新的
 */
class ViewBindingActivity : BaseViewBindingActivity<CommonViewModel, ActivityViewBindingBinding>() {

    override fun providerVMClass(): Class<CommonViewModel> = CommonViewModel::class.java

    override fun getViewBinding() = ActivityViewBindingBinding.inflate(layoutInflater)

    override fun initData() {
        //请求接口
        getData()
        //监听接口数据
        viewModel.newTodoData.observe(this) {
            //这里为了便于理解我直接用了原始的Observer
            //这里可以自定义一个类继承Observer，对返回数据进一步封装
            //防止每个接口都要处理请求开始(STATE_LOADING)&请求出错(STATE_FAILED,STATE_ERROR)的情况
            //感兴趣的同学可以自行尝试
            when(it.dataState) {
                DataState.STATE_LOADING -> {
                    //请求开始,这里可以做一下常规操作，如网络请求弹窗
                }
                DataState.STATE_SUCCESS -> {
                    //请求成功，数据不为null
                    it.data?.let { it1 -> setData(it1) }
                }
                DataState.STATE_FAILED, DataState.STATE_ERROR -> {
                    //接口请求出错,这里自行处理接口报错
                    toast(it.errorMsg)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        setTop(binding.toolbar, "ViewBinding示例")
        binding.tvTotalNum.text = "ViewBinding示例"
    }

    private fun getData() {
        val dataMap = mutableMapOf<String, Any>()
        dataMap["status"] = 1
        //相比于以前的发起请求后直接监听结果，这里分开写更容易理解；而且可以手动取消
        val todoJob = viewModel.getTodoListNew(0, dataMap)
        //viewModel.getTodoListNew返回的是一个Job对象，如果有特殊情况需要取消网络请求可以使用，写法如下
//        todoJob.cancel()
    }

    @SuppressLint("SetTextI18n")
    private fun setData(data: TodoResponseBody) {
        binding.run {
            //viewBind会根据xml中定义的控件id自动为控件生成驼峰命名
            tvTotalNum.text = "接口返回数据---->${data}"
        }
    }
}