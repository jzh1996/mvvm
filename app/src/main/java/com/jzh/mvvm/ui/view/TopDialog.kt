package com.jzh.mvvm.ui.view

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import com.jzh.mvvm.R
import com.jzh.mvvm.utils.SettingUtil
import kotlinx.android.synthetic.main.activity_system.*
import kotlinx.android.synthetic.main.view_app_default_page.*

class TopDialog : Dialog {

    private var mContext: Activity
    private var toolbarHeight = 0

    constructor(context: Activity, toolbarHeight: Int, theme: Int = R.style.myDialogTheme) : super(
        context,
        theme
    ) {
        this.mContext = context
        this.toolbarHeight = toolbarHeight
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_app_default_page)
        val dialogWindow: Window? = this.window
        dialogWindow?.setGravity(Gravity.TOP)
//        dialogWindow?.setWindowAnimations(R.style.top_anim_style)
        dialogWindow?.decorView?.setPadding(0, toolbarHeight, 0, 0)
        dialogWindow?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        rg_app_default.run {
            clearCheck()
            setChecked(SettingUtil.getDefaultPage())
            setOnCheckedChangeListener { _, id ->
                SettingUtil.setDefaultPage(getCheck(id))
                mContext.tv_app_default_page.text = getCheck(id)
                this@TopDialog.dismiss()
            }
        }
    }

    private fun setChecked(type: String) {
        when (type) {
            mContext.resources.getString(R.string.home) -> rb_home.isChecked = true
            mContext.resources.getString(R.string.system) -> rb_system.isChecked = true
            mContext.resources.getString(R.string.weixin) -> rb_weixin.isChecked = true
            mContext.resources.getString(R.string.question) -> rb_question.isChecked = true
            mContext.resources.getString(R.string.my) -> rb_my.isChecked = true
            else -> rb_home.isChecked = true
        }
    }

    private fun getCheck(id: Int): String {
        return when (id) {
            R.id.rb_home -> mContext.resources.getString(R.string.home)
            R.id.rb_system -> mContext.resources.getString(R.string.system)
            R.id.rb_weixin -> mContext.resources.getString(R.string.weixin)
            R.id.rb_question -> mContext.resources.getString(R.string.question)
            R.id.rb_my -> mContext.resources.getString(R.string.my)
            else -> mContext.resources.getString(R.string.home)
        }
    }
}