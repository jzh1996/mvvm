package com.jzh.mvvm.viewBinding

import android.annotation.SuppressLint
import com.jzh.mvvm.databinding.ActivityViewBindingBinding
import com.jzh.mvvm.httpUtils.TodoResponseBody
import com.jzh.mvvm.mvvm.viewModel.CommonViewModel

/**
 * Created by jzh on 2022-05-27 10:11:51
 * Viewbinding示例
 *
 */
class ViewBindingActivity : BaseViewBindingActivity<CommonViewModel, ActivityViewBindingBinding>() {

    override fun providerVMClass(): Class<CommonViewModel> = CommonViewModel::class.java

    override fun getViewBinding() = ActivityViewBindingBinding.inflate(layoutInflater)

    override fun initData() {
        //请求接口
        getData()
        //监听接口数据
        viewModel.todoData.observe(this) {
            setData(it)
        }
    }

    override fun initView() {
        setTop(binding.toolbar, "ViewBinding示例")
        binding.tvTotalNum.text = "ViewBinding示例"
    }

    private fun getData() {
        val dataMap = mutableMapOf<String, Any>()
        dataMap["status"] = 1
        viewModel.getTodoList(0, dataMap)
    }

    @SuppressLint("SetTextI18n")
    private fun setData(data: TodoResponseBody) {
        binding.run {
            //viewBind会根据xml中定义的控件id自动为控件生成驼峰命名
            tvTotalNum.text = "接口返回数据---->${data}"
        }
    }
}