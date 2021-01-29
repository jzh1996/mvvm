package com.jzh.mvvm.ui.activity.my

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseViewModelActivity
import com.jzh.mvvm.mvvm.viewModel.TodoActivityViewModel
import com.jzh.mvvm.utils.formatCurrentDate
import com.jzh.mvvm.utils.stringToCalendar
import com.jzh.mvvm.utils.toast
import kotlinx.android.synthetic.main.activity_add_todo.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.*

class AddTodoActivity : BaseViewModelActivity<TodoActivityViewModel>() {

    /**
     * 优先级  重要（1），一般（0）
     */
    private var mPriority = 0
    private var mCurrentDate = formatCurrentDate()

    override fun getLayoutId() = R.layout.activity_add_todo

    override fun providerVMClass() = TodoActivityViewModel::class.java

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initData() {
        if (intent?.getStringExtra("type") == "edit") {
            setTop("编辑", "保存")
            toolbar_subtitle.setOnClickListener {
                if (getData() == null) return@setOnClickListener
                updateTodo(intent?.getIntExtra("id", 0), getData()!!)
            }
            if (intent?.getStringExtra("title") != null) {
                et_title.setText(intent?.getStringExtra("title"))
            }
            if (intent?.getStringExtra("content") != null) {
                et_content.setText(intent?.getStringExtra("content"))
            }
            if (intent?.getIntExtra("priority", -1) != 1) {
                rb0.isChecked = true
                rb1.isChecked = false
                mPriority = 0
            } else {
                rb0.isChecked = false
                rb1.isChecked = true
                mPriority = 1
            }
            if (intent?.getStringExtra("dateStr") != null) {
                tv_date.text = intent?.getStringExtra("dateStr")
            }
        } else {
            setTop("新增", "保存")
            toolbar_subtitle.setOnClickListener {
                if (getData() == null) return@setOnClickListener
                addTodo(getData()!!)
            }
            tv_date.text = mCurrentDate
        }
    }

    private fun getData(): MutableMap<String, Any>? {
        val map = mutableMapOf<String, Any>()
        when {
            et_title.text.toString() == "" -> {
                toast("标题不能为空")
                return null
            }
            et_content.text.toString() == "" -> {
                toast("描述内容不能为空")
                return null
            }
            else -> {
                map["title"] = et_title.text.toString()
                map["content"] = et_content.text.toString()
                map["date"] = tv_date.text.toString()
                map["priority"] = mPriority
            }
        }
        return map
    }

    private fun addTodo(map: MutableMap<String, Any>) {
        showLoading()
        viewModel.addTodo(map).observe(this, {
            hideLoading()
            setResult(RESULT_OK)
            toast("添加成功")
            finish()
        })
    }

    private fun updateTodo(id: Int?, map: MutableMap<String, Any>) {
        if (id == null) return
        showLoading()
        viewModel.updateTodo(id, map).observe(this, {
            hideLoading()
            setResult(RESULT_OK)
            toast("修改成功")
            finish()
        })
    }

    override fun initView() {
        rg_priority.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rb0) {
                mPriority = 0
                rb0.isChecked = true
                rb1.isChecked = false
            } else if (checkedId == R.id.rb1) {
                mPriority = 1
                rb0.isChecked = false
                rb1.isChecked = true
            }
        }
        ll_date.setOnClickListener {
            var now = Calendar.getInstance()
            if (intent.getStringExtra("type") == "new") {
                now = intent?.getStringExtra("dateStr")?.stringToCalendar() ?: now
            }
            val dpd = DatePickerDialog(
                this, { view, year, month, dayOfMonth ->
                    mCurrentDate = "$year-${month + 1}-$dayOfMonth"
                    tv_date.text = mCurrentDate
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }
    }

    override fun startHttp() {}
}