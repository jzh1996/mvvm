package com.jzh.mvvm.ui.activity.group

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseViewModelActivity
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.mvvm.viewModel.GroupActivityViewModel
import com.jzh.mvvm.ui.activity.login.LoginActivity
import com.jzh.mvvm.ui.activity.share.ShareActivity
import com.jzh.mvvm.ui.adapter.HomeAdapter
import com.jzh.mvvm.utils.MyMMKV.Companion.mmkv
import com.jzh.mvvm.utils.RvAnimUtils
import com.jzh.mvvm.utils.SettingUtil
import com.jzh.mvvm.utils.toast
import com.jzh.mvvm.webView.WebViewActivity
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.lang.Exception

class GroupActivity : BaseViewModelActivity<GroupActivityViewModel>() {

    private val mAdapter by lazy { HomeAdapter() }
    private var isRefresh = false
    private lateinit var refreshLayout: SmartRefreshLayout
    private val linearLayoutManager by lazy { LinearLayoutManager(this) }

    override fun getLayoutId(): Int = R.layout.activity_group

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initData() {
        setTop("广场", R.drawable.add)
        toolbar_title?.setOnClickListener { recyclerView_group.scrollToPosition(0) }
        toolbar_subtitle_image?.setOnClickListener {
            if (mmkv.decodeBool(Constant.IS_LOGIN, false)) {
                startActivity(Intent(this, ShareActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    override fun initView() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        refreshLayout = swipeRefreshLayout_group
        refreshLayout.setRefreshHeader(ch_header_group)
        refreshLayout.setOnRefreshListener {
            mAdapter.loadMoreModule.isEnableLoadMore = false
            startHttp()
        }
        recyclerView_group.run {
            layoutManager = linearLayoutManager
            adapter = mAdapter
            itemAnimator = DefaultItemAnimator()
        }
        mAdapter.run {
            recyclerView = recyclerView_group
            setOnItemClickListener { adapter, view, position ->
                if (data.size != 0) {
                    val data = data[position]
                    WebViewActivity.start(this@GroupActivity, data.id, data.title, data.link)
                }
            }
            loadMoreModule.setOnLoadMoreListener {
                isRefresh = false
                refreshLayout.finishRefresh()
                val page = mAdapter.data.size / 20
                getGroupList(page)
            }
            addChildClickViewIds(R.id.iv_like)
            setOnItemChildClickListener { adapter, view, position ->
                if (data.size == 0) return@setOnItemChildClickListener
                val res = data[position]
                when (view.id) {
                    R.id.iv_like -> {
                        if (!mmkv.decodeBool(Constant.IS_LOGIN, false)) {
                            startActivity(Intent(this@GroupActivity, LoginActivity::class.java))
                            return@setOnItemChildClickListener
                        }
                        val collect = res.collect
                        res.collect = !collect
                        setData(position, res)
                        if (collect) viewModel.cancelCollectArticle(res.id).observe(this@GroupActivity, {})
                        else viewModel.addCollectArticle(res.id).observe(this@GroupActivity,{})
                    }
                }
            }
        }
        RvAnimUtils.setAnim(mAdapter, SettingUtil.getListAnimal())
    }

    override fun startHttp() {
        showLoading()
        isRefresh = true
        getGroupList(0)
    }

    private fun getGroupList(page: Int) {
        viewModel.getGroupList(page).observe(this, {
            it.datas.let { article ->
                hideLoading()
                mAdapter.run {
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                        setList(article)
                    } else addData(article)
                    if (data.size == 0) setEmptyView(R.layout.fragment_empty_layout)
                    else if (hasEmptyView()) removeEmptyView()
                    if (it.over) loadMoreModule.loadMoreEnd(isRefresh)
                    else loadMoreModule.loadMoreComplete()
                }
            }
        })
    }

    override fun requestError(it: Exception?) {
        super.requestError(it)
        mAdapter.loadMoreModule.loadMoreFail()
    }

    override fun providerVMClass() = GroupActivityViewModel::class.java
}