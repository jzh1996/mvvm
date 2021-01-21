package com.jzh.mvvm.ui.activity.login

import android.os.Build
import androidx.annotation.RequiresApi
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseViewModelActivity
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.mvvm.viewModel.LoginActivityViewModel
import com.jzh.mvvm.utils.MyMMKV
import com.jzh.mvvm.utils.toast
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseViewModelActivity<LoginActivityViewModel>() {

    override fun providerVMClass() = LoginActivityViewModel::class.java

    override fun getLayoutId() = R.layout.activity_register

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initData() {
        setTop("注册")
        btn_register.setOnClickListener { register() }
    }

    override fun initView() {}

    override fun startHttp() {}

    private fun register() {
        val enterpriseName = et_login_id.text.toString().trim()
        val password1 = et_login_password1.text.toString().trim()
        val password2 = et_login_password2.text.toString().trim()
        when {
            enterpriseName == "" -> toast("账号不能为空")
            password1 == "" -> toast("密码不能为空")
            password1 != password2 -> toast("两次密码不一致")
            else -> {
                viewModel.register(enterpriseName, password1, password2).observe(this, {
                    finish()
                })
            }
        }
    }
}