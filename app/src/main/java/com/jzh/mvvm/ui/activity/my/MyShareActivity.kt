package com.jzh.mvvm.ui.activity.my

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseViewModelActivity
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.mvvm.viewModel.ShareActivityViewModel
import com.jzh.mvvm.ui.activity.login.LoginActivity
import com.jzh.mvvm.ui.activity.share.ShareActivity
import com.jzh.mvvm.ui.adapter.ShareAdapter
import com.jzh.mvvm.ui.view.SwipeItemLayout
import com.jzh.mvvm.utils.MyMMKV
import com.jzh.mvvm.utils.RvAnimUtils
import com.jzh.mvvm.utils.SettingUtil
import com.jzh.mvvm.webView.WebViewActivity
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.activity_my_share.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class MyShareActivity : BaseViewModelActivity<ShareActivityViewModel>() {

    companion object {
        const val SHARE_SUCCESS = 1
    }

    private val mAdapter by lazy { ShareAdapter() }
    private var isRefresh = false
    private lateinit var refreshLayout: SmartRefreshLayout
    private val linearLayoutManager by lazy { LinearLayoutManager(this) }

    override fun providerVMClass() = ShareActivityViewModel::class.java

    override fun getLayoutId() = R.layout.activity_my_share

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initData() {
        setTop("我的分享", R.drawable.add)
        toolbar_subtitle_image?.setOnClickListener {
            if (MyMMKV.mmkv.decodeBool(Constant.IS_LOGIN, false)) {
                startActivityForResult(Intent(this, ShareActivity::class.java), SHARE_SUCCESS)
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    override fun initView() {
        refreshLayout = swipeRefreshLayout_share
        refreshLayout.setRefreshHeader(ch_header_share)
        refreshLayout.setOnRefreshListener {
            mAdapter.loadMoreModule.isEnableLoadMore = false
            startHttp()
        }
        recyclerView_share.run {
            layoutManager = linearLayoutManager
            adapter = mAdapter
            itemAnimator = DefaultItemAnimator()
            addOnItemTouchListener(SwipeItemLayout.OnSwipeItemTouchListener(this@MyShareActivity))
        }
        mAdapter.run {
            recyclerView = recyclerView_share
            setOnItemClickListener { adapter, view, position ->
                if (data.size != 0) {
                    val data = data[position]
                    WebViewActivity.start(this@MyShareActivity, data.id, data.title, data.link)
                }
            }
            loadMoreModule.setOnLoadMoreListener {
                isRefresh = false
                refreshLayout.finishRefresh()
                val page = mAdapter.data.size / 20
                getShareArticle(page)
            }
            addChildClickViewIds(R.id.iv_like, R.id.btn_delete, R.id.rl_content)
            setOnItemChildClickListener { adapter, view, position ->
                if (data.size == 0) return@setOnItemChildClickListener
                val res = data[position]
                when (view.id) {
                    R.id.iv_like -> {
                        if (!MyMMKV.mmkv.decodeBool(Constant.IS_LOGIN, false)) {
                            startActivity(Intent(this@MyShareActivity, LoginActivity::class.java))
                            return@setOnItemChildClickListener
                        }
                        val collect = res.collect
                        res.collect = !collect
                        setData(position, res)
                        if (collect) viewModel.cancelCollectArticle(res.id)
                            .observe(this@MyShareActivity, {})
                        else viewModel.addCollectArticle(res.id).observe(this@MyShareActivity, {})
                    }
                    R.id.btn_delete -> {
                        viewModel.deleteShareArticle(res.id).observe(this@MyShareActivity, {
                            mAdapter.removeAt(position)
                        })
                    }
                    R.id.rl_content -> {
                        WebViewActivity.start(this@MyShareActivity, res.id, res.title, res.link)
                    }
                }
            }
        }
        RvAnimUtils.setAnim(mAdapter, SettingUtil.getListAnimal())
    }

    override fun startHttp() {
        showLoading()
        isRefresh = true
        getShareArticle(0)
    }

    private fun getShareArticle(page: Int) {
        viewModel.getShareList(page + 1).observe(this, {
            hideLoading()
            it.shareArticles.datas.let { article ->
                mAdapter.run {
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                        setList(article)
                    } else addData(article)
                    if (data.size == 0) setEmptyView(R.layout.fragment_empty_layout)
                    else if (hasEmptyView()) removeEmptyView()
                    if (it.shareArticles.over) loadMoreModule.loadMoreEnd(isRefresh)
                    else loadMoreModule.loadMoreComplete()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SHARE_SUCCESS -> {
                if (resultCode == RESULT_OK) startHttp()
            }
        }
    }

    override fun requestError(it: Exception?) {
        super.requestError(it)
        mAdapter.loadMoreModule.loadMoreFail()
    }
}