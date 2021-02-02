package com.jzh.mvvm.ui.view

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.jzh.mvvm.R
import com.jzh.mvvm.utils.SettingUtil
import kotlinx.android.synthetic.main.rv_anim_dialog_view.*

class RvAnimDialog : Dialog {

    private var mContext: Activity
    private var mClickListener: View.OnClickListener? = null//弹窗上的按钮的点击事件

    constructor(context: Activity) : super(context) {
        this.mContext = context
    }

    constructor(
        context: Activity,
        clickListener: View.OnClickListener,
        theme: Int = R.style.myDialogTheme
    ) : super(context, theme) {
        this.mContext = context
        this.mClickListener = clickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_anim_dialog_view)
        val dialogWindow: Window? = this.window
        dialogWindow?.setGravity(Gravity.BOTTOM)
        dialogWindow?.setWindowAnimations(R.style.bottom_anim_style)
        dialogWindow?.decorView?.setPadding(0, 0, 0, 0)
        dialogWindow?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tv_rv_no.setOnClickListener(mClickListener)
        tv_rv_jx.setOnClickListener(mClickListener)
        tv_rv_db.setOnClickListener(mClickListener)
        tv_rv_sf.setOnClickListener(mClickListener)
        tv_rv_zc.setOnClickListener(mClickListener)
        tv_rv_yc.setOnClickListener(mClickListener)
    }
}