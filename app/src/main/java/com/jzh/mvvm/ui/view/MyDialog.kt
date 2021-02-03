package com.jzh.mvvm.ui.view

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.jzh.mvvm.R
import kotlinx.android.synthetic.main.layout_alert_dialog.*


class MyDialog(mContext: Activity) : Dialog(mContext, R.style.myDialogTheme) {

    init {
        setContentView(R.layout.layout_alert_dialog)
        val dialogWindow: Window? = this.window
        val m: WindowManager = mContext.windowManager
        val d = m.defaultDisplay
        val p: WindowManager.LayoutParams? = dialogWindow?.attributes
        p?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        p?.width = (d.width * 0.8).toInt()
        dialogWindow?.attributes = p
        setCancelable(true)
        setCanceledOnTouchOutside(false)
    }

    fun setDialogText(text: String) {
        tv_dialog?.setText(text)
    }

    //isShowSure不为空则显示确定按钮，并且按钮名称为isShowSure的值，isShowCancel同理
    fun setIsShowSureOrCancel(isShowSure: String, isShowCancel: String) {
        if (isShowSure != "") {
            tv_dialog_sure?.visibility = View.VISIBLE
            tv_dialog_sure?.text = isShowSure
        } else tv_dialog_sure?.visibility = View.GONE
        if (isShowCancel != "") {
            tv_dialog_cancle?.visibility = View.VISIBLE
            tv_dialog_cancle?.text = isShowCancel
        } else tv_dialog_cancle?.visibility = View.GONE
    }

    fun setDialogCanCancel(isShowCancel: Boolean) {
        this.setCancelable(isShowCancel)
    }

    fun setIsCanceledOnTouchOutside(isShowCancel: Boolean) {
        this.setCanceledOnTouchOutside(isShowCancel)
    }

    /**
     * title为空则不显示标题
     */
    fun setTitle(title: String = "") {
        if (title == "") ll_dialog_title?.visibility = View.GONE
        else {
            ll_dialog_title?.visibility = View.VISIBLE
            tv_dialog_title?.text = title
        }
    }

    fun setClickListener(clickListener: View.OnClickListener) {
        tv_dialog_sure?.setOnClickListener(clickListener)
        tv_dialog_cancle?.setOnClickListener(clickListener)
        tv_dialog_delete?.setOnClickListener(clickListener)
    }
}