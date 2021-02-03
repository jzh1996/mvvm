package com.jzh.mvvm.ui.activity.my

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseViewModelActivity
import com.jzh.mvvm.mvvm.viewModel.RoomViewModel
import com.jzh.mvvm.ui.adapter.LaterReadAdapter
import com.jzh.mvvm.ui.view.MyDialog
import com.jzh.mvvm.ui.view.SwipeItemLayout
import com.jzh.mvvm.utils.RvAnimUtils
import com.jzh.mvvm.utils.SettingUtil
import com.jzh.mvvm.webView.WebViewActivity
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.activity_later_read.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class LaterReadActivity : BaseViewModelActivity<RoomViewModel>() {

    private val mAdapter by lazy { LaterReadAdapter() }
    private var isRefresh = false
    private lateinit var refreshLayout: SmartRefreshLayout
    private val linearLayoutManager by lazy { LinearLayoutManager(this) }
    private lateinit var clearDialog: MyDialog

    override fun providerVMClass() = RoomViewModel::class.java

    override fun getLayoutId() = R.layout.activity_later_read

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initData() {
        setTop("稍后阅读", "清空")
        toolbar_subtitle.setOnClickListener {
            if (!this::clearDialog.isInitialized) {
                clearDialog = MyDialog(this)
                clearDialog.run {
                    setDialogText("清空后无法还原,确认全部删除吗")
                    setClickListener { v ->
                        when (v.id) {
                            R.id.tv_dialog_sure -> {
                                if (clearDialog.isShowing) clearDialog.dismiss()
                                viewModel.removeAllLater().observe(this@LaterReadActivity) {
                                    mAdapter.setList(mutableListOf())
                                    mAdapter.setEmptyView(R.layout.fragment_empty_layout)
                                }
                            }
                            R.id.tv_dialog_cancle -> if (clearDialog.isShowing) clearDialog.dismiss()
                        }
                    }
                    show()
                }
            } else clearDialog.show()
        }
    }

    override fun initView() {
        refreshLayout = swipeRefreshLayout_later_read
        refreshLayout.run {
            setRefreshHeader(ch_header_later_read)
            setOnRefreshListener {
                startHttp()
            }
        }
        recyclerView_later_read.run {
            layoutManager = linearLayoutManager
            adapter = mAdapter
            itemAnimator = DefaultItemAnimator()
            addOnItemTouchListener(SwipeItemLayout.OnSwipeItemTouchListener(this@LaterReadActivity))
        }
        mAdapter.run {
            recyclerView = recyclerView_later_read
            loadMoreModule.setOnLoadMoreListener {
                isRefresh = false
                refreshLayout.finishRefresh()
                val page = mAdapter.data.size / pageSize
                getLaterReadList(page)
            }
            addChildClickViewIds(R.id.rl_read, R.id.btn_delete_read)
            setOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.rl_read -> WebViewActivity.start(
                        this@LaterReadActivity,
                        data[position].link
                    )
                    R.id.btn_delete_read -> {
                        viewModel.removeLater(data[position].link).observe(this@LaterReadActivity) {
                            adapter.removeAt(position)
                        }
                    }
                }
            }
        }
        RvAnimUtils.setAnim(mAdapter, SettingUtil.getListAnimal())
    }

    override fun startHttp() {
        showLoading()
        isRefresh = true
        getLaterReadList(0)
    }

    private fun getLaterReadList(page: Int) {
        val from = if (page == 0) 0
        else page * pageSize
        viewModel.getLaterList(from, pageSize).observe(this) {
            hideLoading()
            it.let { laterList ->
                mAdapter.run {
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                        setList(laterList)
                        recyclerView.scrollToPosition(0)
                    } else addData(laterList)
                    if (data.size == 0) setEmptyView(R.layout.fragment_empty_layout)
                    else if (hasEmptyView()) removeEmptyView()
                    if (laterList.size < pageSize) loadMoreModule.loadMoreEnd(isRefresh)
                    else loadMoreModule.loadMoreComplete()
                }
            }
        }
    }
}