package com.jzh.mvvm.ui.activity.common

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseViewModelActivity
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.mvvm.mainViewModel.HomeViewModel
import com.jzh.mvvm.ui.activity.login.LoginActivity
import com.jzh.mvvm.ui.adapter.HomeAdapter
import com.jzh.mvvm.utils.*
import com.jzh.mvvm.webView.WebViewActivity
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.toolbar_layout_search.*
import kotlinx.android.synthetic.main.toolbar_layout_search.toolbar_subtitle_image
import java.lang.Exception

class SearchActivity : BaseViewModelActivity<HomeViewModel>() {

    private lateinit var search: androidx.appcompat.widget.SearchView
    private var isRefresh = true
    private lateinit var refreshLayout: SmartRefreshLayout
    private val linearLayoutManager by lazy { LinearLayoutManager(this) }
    private val homeAdapter: HomeAdapter by lazy { HomeAdapter() }
    private var searchStr = ""

    override fun providerVMClass() = HomeViewModel::class.java

    override fun getLayoutId(): Int = R.layout.activity_search

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initData() {
        setSearchTop(R.drawable.search)
        toolbar_subtitle_image.setOnClickListener {
            hideSoftInput(ll_search, this)
            isRefresh = true
            startSearch(0, searchStr)
        }
        search = toolbar_search_title
        search.onActionViewExpanded()//默认展开
        val mSearchIcon = search.findViewById<ImageView>(R.id.search_mag_icon)
        search.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(string: String): Boolean {
                searchStr = string
                if (searchStr == "") {
                    homeAdapter.run { setList(null) }
                }
                return false
            }

            override fun onQueryTextSubmit(string: String): Boolean {
                searchStr = string
                isRefresh = true
                startSearch(0, string)
                return false
            }
        })
        val mSearchTextView = search.findViewById<TextView>(R.id.search_src_text)
        //屏蔽搜索图标
        mSearchIcon.visibility = View.GONE
        mSearchTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        mSearchTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
        mSearchTextView.setHintTextColor(ContextCompat.getColor(this, R.color.list_divider))
        //去掉SearchView的下划线
        val viewById: View = search.findViewById(R.id.search_plate)
        viewById.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun initView() {
        refreshLayout = swipeRefreshLayout1_search
        refreshLayout.setRefreshHeader(ch_header_seach)
        refreshLayout.setOnRefreshListener {
            homeAdapter.loadMoreModule.isEnableLoadMore = false
            isRefresh = true
            startSearch(0, searchStr)
        }
        recyclerView1_seach.run {
            layoutManager = linearLayoutManager
            adapter = homeAdapter
            itemAnimator = DefaultItemAnimator()
        }
        recycleViewCloseSoftInput(recyclerView1_seach, ll_search, this, true)
        homeAdapter.run {
            recyclerView = recyclerView1_seach
            setOnItemClickListener { adapter, view, position ->
                if (data.size != 0) {
                    val data = data[position]
                    WebViewActivity.start(this@SearchActivity, data.id, data.title, data.link)
                }
            }
            loadMoreModule.setOnLoadMoreListener {
                isRefresh = false
                refreshLayout.finishRefresh()
                val page = homeAdapter.data.size / pageSize
                startSearch(page, searchStr)
            }
            addChildClickViewIds(R.id.iv_like)
            setOnItemChildClickListener { adapter, view, position ->
                if (data.size == 0) return@setOnItemChildClickListener
                val res = data[position]
                when (view.id) {
                    R.id.iv_like -> {
                        if (!MyMMKV.mmkv.decodeBool(Constant.IS_LOGIN, false)) {
                            startActivity(Intent(this@SearchActivity, LoginActivity::class.java))
                            return@setOnItemChildClickListener
                        }
                        val collect = res.collect
                        res.collect = !collect
                        setData(position, res)
                        if (collect) viewModel.cancelCollectArticle(res.id)
                            .observe(this@SearchActivity, {})
                        else viewModel.addCollectArticle(res.id).observe(this@SearchActivity, {})
                    }
                }
            }
        }
        RvAnimUtils.setAnim(homeAdapter, SettingUtil.getListAnimal())
    }

    override fun startHttp() {
//        getHotSearch()
    }

    private fun startSearch(page: Int, key: String) {
        if (key == "") {
            refreshLayout.finishRefresh()
            return
        }
        showSearchLoading()
        viewModel.queryBySearchKey(page, key).observe(this, {
            it.datas.let { Article ->
                hideSearchLoading()
                homeAdapter.run {
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                        setList(Article)
                        recyclerView.scrollToPosition(0)
                    } else addData(Article)
                    if (data.size == 0) setEmptyView(R.layout.fragment_empty_layout)
                    else if (hasEmptyView()) removeEmptyView()
                    if (it.over || data.size < pageSize) loadMoreModule.loadMoreEnd(isRefresh)
                    else loadMoreModule.loadMoreComplete()
                }
            }
        })
    }

    private fun getHotSearch() {
        viewModel.getHotSearchData().observe(this, {

        })
    }

    override fun requestError(it: Exception?) {
        super.requestError(it)
        homeAdapter.loadMoreModule.loadMoreFail()
    }
}