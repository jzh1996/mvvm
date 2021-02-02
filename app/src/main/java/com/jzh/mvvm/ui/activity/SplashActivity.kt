package com.jzh.mvvm.ui.activity

import android.content.Intent
import com.jzh.mvvm.MainActivity
import com.jzh.mvvm.base.BaseActivity

/**
 * 闪屏页，可以放入广告等页面
 * Created by jzh on 2020-12-26.
 */
class SplashActivity : BaseActivity() {

    override fun getLayoutId(): Int = 0
    override fun initData() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    override fun initView() {}

    override fun startHttp() {}
}