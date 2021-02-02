package com.jzh.mvvm.ui.view

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.jzh.mvvm.R
import kotlinx.android.synthetic.main.bottom_dialog_view.*

class BottomDialog : Dialog {

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
        setContentView(R.layout.bottom_dialog_view)
        val dialogWindow: Window? = this.window
        dialogWindow?.setGravity(Gravity.BOTTOM)
        dialogWindow?.setWindowAnimations(R.style.bottom_anim_style)
        dialogWindow?.decorView?.setPadding(0, 0, 0, 0)
        dialogWindow?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tv_take_photo.setOnClickListener(mClickListener)
        tv_from_album.setOnClickListener(mClickListener)
        tv_cancle.setOnClickListener(mClickListener)
    }
}