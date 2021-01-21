package com.jzh.mvvm.ui.activity.login

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseViewModelActivity
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.mvvm.viewModel.LoginActivityViewModel
import com.jzh.mvvm.utils.MyMMKV.Companion.mmkv
import com.jzh.mvvm.utils.toast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseViewModelActivity<LoginActivityViewModel>() {

    override fun providerVMClass() = LoginActivityViewModel::class.java

    override fun getLayoutId(): Int = R.layout.activity_login

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initData() {
        setTop("登录")
        btn_login.setOnClickListener { login() }
        tv_register.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }

    override fun initView() {}

    override fun startHttp() {}

    private fun login() {
        val enterpriseName = et_login_id.text.toString().trim()
        val password = et_login_password.text.toString().trim()
        when {
            enterpriseName == "" -> toast("账号不能为空")
            password == "" -> toast("密码不能为空")
            else -> {
                viewModel.login(enterpriseName, password).observe(this, {
                    mmkv.encode(Constant.IS_LOGIN, true)
                    LiveEventBus.get(Constant.IS_LOGIN).post(true)
                    finish()
                })
            }
        }
    }
}