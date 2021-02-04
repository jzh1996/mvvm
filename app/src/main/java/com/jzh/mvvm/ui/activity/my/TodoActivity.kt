package com.jzh.mvvm.ui.activity.my

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseViewModelActivity
import com.jzh.mvvm.mvvm.viewModel.TodoActivityViewModel
import com.jzh.mvvm.ui.adapter.TodoAdapter
import com.jzh.mvvm.ui.view.SwipeItemLayout
import com.jzh.mvvm.utils.RvAnimUtils
import com.jzh.mvvm.utils.SettingUtil
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.activity_todo.*
import kotlinx.android.synthetic.main.item_todo_list.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.view_popupwindow.view.*
import kotlin.collections.set

class TodoActivity : BaseViewModelActivity<TodoActivityViewModel>() {

    companion object {
        const val DEAL_SUCCESS = 1
    }

    private val mAdapter by lazy { TodoAdapter() }
    private var isRefresh = false
    private lateinit var refreshLayout: SmartRefreshLayout
    private val linearLayoutManager by lazy { LinearLayoutManager(this) }
    private lateinit var dataMap: MutableMap<String, Any>
    private lateinit var popWindow: PopupWindow

    override fun providerVMClass() = TodoActivityViewModel::class.java

    override fun getLayoutId() = R.layout.activity_todo

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initData() {
        setTop("TODO", R.drawable.add)
        toolbar_subtitle_image.setOnClickListener {
            val intent = Intent(this, AddTodoActivity::class.java)
            intent.putExtra("type", "new")
            startActivityForResult(intent, DEAL_SUCCESS)
        }
    }

    override fun initView() {
        dataMap = mutableMapOf()
        refreshLayout = swipeRefreshLayout_todo
        refreshLayout.run {
            setRefreshHeader(ch_header_todo)
            setOnRefreshListener {
                startHttp()
            }
        }
        recyclerView_todo.run {
            layoutManager = linearLayoutManager
            adapter = mAdapter
            itemAnimator = DefaultItemAnimator()
            addOnItemTouchListener(SwipeItemLayout.OnSwipeItemTouchListener(this@TodoActivity))
        }
        mAdapter.run {
            recyclerView = recyclerView_todo
            //item使用的自定义的SwipeItemLayout，会导致item点击事件无效，所以在ChildClick添加item点击事件
            setOnItemClickListener { adapter, view, position -> }
            loadMoreModule.setOnLoadMoreListener {
                isRefresh = false
                refreshLayout.finishRefresh()
                val page = mAdapter.data.size / pageSize
                getTodoList(page, dataMap)
            }
            addChildClickViewIds(R.id.item_todo_done, R.id.item_todo_delete, R.id.rl_item_todo)
            setOnItemChildClickListener { adapter, view, position ->
                var status = data[position]
                when (view.id) {
                    R.id.rl_item_todo -> {
                        val intent = Intent(this@TodoActivity, AddTodoActivity::class.java)
                        intent.putExtra("id", data[position].id)
                        intent.putExtra("type", "edit")
                        intent.putExtra("dateStr", data[position].dateStr)
                        intent.putExtra("title", data[position].title)
                        intent.putExtra("priority", data[position].priority)
                        intent.putExtra("content", data[position].content)
                        startActivityForResult(intent, DEAL_SUCCESS)
                    }
                    R.id.item_todo_done -> {
                        if (data[position].status == 0) {
                            updateTodoById(data[position].id, 1)
                            if (toolbar_title.text == "TODO") {
                                status.status = 1
                                setData(position, status)
                            } else adapter.removeAt(position)
                        } else {
                            updateTodoById(data[position].id, 0)
                            if (toolbar_title.text == "TODO") {
                                status.status = 0
                                setData(position, status)
                            } else adapter.removeAt(position)
                        }
                    }
                    R.id.item_todo_delete -> {
                        deleteTodoById(data[position].id)
                        adapter.removeAt(position)
                    }
                }
            }
        }
        RvAnimUtils.setAnim(mAdapter, SettingUtil.getListAnimal())
        floating_action_btn.run {
            setOnClickListener {
                if (!this@TodoActivity::popWindow.isInitialized) initPopWindow(it)
                else if (popWindow.isShowing) popWindow.dismiss()
                else popWindow.showAsDropDown(it, -1, -585)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initPopWindow(view: View) {
        val popView = LayoutInflater.from(this).inflate(R.layout.view_popupwindow, null, false)
        popWindow = PopupWindow(
            popView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popWindow.run {
            animationStyle = R.anim.fade_in
            isTouchable = true
            setTouchInterceptor { view, motionEvent ->
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return@setTouchInterceptor false
            }
            setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.white)))
            showAsDropDown(view, -1, -585)
            popView.tv_pop_all.setOnClickListener {
                dataMap.clear()
                getTodoList(0, dataMap)
                popWindow.dismiss()
                toolbar_title.text = "TODO"
            }
            popView.tv_pop_done.setOnClickListener {
                dataMap.clear()
                dataMap["status"] = 1
                getTodoList(0, dataMap)
                popWindow.dismiss()
                toolbar_title.text = "已完成"
            }
            popView.tv_pop_todo.setOnClickListener {
                dataMap.clear()
                dataMap["status"] = 0
                getTodoList(0, dataMap)
                popWindow.dismiss()
                toolbar_title.text = "待办"
            }
        }
    }


    override fun startHttp() {
        isRefresh = true
        getTodoList(0, dataMap)
    }

    private fun getTodoList(page: Int, map: MutableMap<String, Any>) {
        showLoading()
        viewModel.getTodoList(page + 1, map).observe(this, {
            hideLoading()
            it.datas.let { todoList ->
                setResult(RESULT_OK)
                mAdapter.run {
                    var hasTodo = false
                    for (i in 0 until todoList.size) {
                        if (todoList[i].status == 0) {
                            hasTodo = true
                            break
                        }
                    }
                    if (hasTodo) LiveEventBus.get("myBadge").post(true)
                    else LiveEventBus.get("myBadge").post(false)
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                        setList(todoList)
                        recyclerView.scrollToPosition(0)
                    } else addData(todoList)
                    if (data.size == 0) setEmptyView(R.layout.fragment_empty_layout)
                    else if (hasEmptyView()) removeEmptyView()
                    if (it.over) loadMoreModule.loadMoreEnd(isRefresh)
                    else loadMoreModule.loadMoreComplete()
                }
            }
        })
    }

    private fun updateTodoById(id: Int, status: Int) {
        viewModel.updateTodoById(id, status).observe(this, {
            setResult(RESULT_OK)
        })
    }

    private fun deleteTodoById(id: Int) {
        viewModel.deleteTodoById(id).observe(this, {
            setResult(RESULT_OK)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DEAL_SUCCESS && resultCode == RESULT_OK) {
            startHttp()
        }
    }

    override fun requestError(it: Exception?) {
        super.requestError(it)
        mAdapter.loadMoreModule.loadMoreFail()
    }
}