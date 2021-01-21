package com.jzh.mvvm.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * 隐藏输入键盘
 *
 * @param view
 * @param context
 */
fun hideSoftInput(view: View, context: Context) {
    val methodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    methodManager.hideSoftInputFromWindow(
        view.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}

/**
 * recycleView滑动时关闭输入法
 *
 * @param recycleView
 * @param context
 */

fun recycleViewCloseSoftInput(
    recycleView: RecyclerView,
    view: ViewGroup,
    context: Context,
    isFocusable: Boolean = false
) {
    recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (abs(dx) > 0 || abs(dy) > 0) {
                //隐藏输入键盘
                hideSoftInput(view, context)
                //隐藏输入键盘后，可以输入的控件还是会获得焦点，这里根据实际情况选择是否释放焦点
                if (isFocusable) {
                    //让控件失去焦点，不然要点两次物理返回键才能关闭页面
                    view.isFocusable = false
                    view.focusedChild?.clearFocus()
                    view.clearFocus()
                }
            }
        }
    })
}
