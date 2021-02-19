package com.jzh.mvvm.ui.mainFragment

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jzh.mvvm.R
import com.jzh.mvvm.httpUtils.Article
import com.jzh.mvvm.httpUtils.Banner
import com.jzh.mvvm.base.BaseApplication
import com.jzh.mvvm.base.BaseViewModelFragment
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.mvvm.mainViewModel.HomeViewModel
import com.jzh.mvvm.ui.activity.login.LoginActivity
import com.jzh.mvvm.ui.adapter.HomeAdapter
import com.jzh.mvvm.ui.adapter.ImageAdapter
import com.jzh.mvvm.utils.*
import com.jzh.mvvm.utils.MyMMKV.Companion.mmkv
import com.jzh.mvvm.webView.WebViewActivity
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.youth.banner.indicator.CircleIndicator
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.layout_network_err_tip.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import java.lang.Exception


class HomeFragment : BaseViewModelFragment<HomeViewModel>() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var mTipView: View
    private var isRefresh = true
    private lateinit var refreshLayout: SmartRefreshLayout
    private val linearLayoutManager by lazy { LinearLayoutManager(activity) }
    private lateinit var imgAdapter: ImageAdapter
    private var bannerView: com.youth.banner.Banner<Banner, ImageAdapter>? = null
    private val homeAdapter by lazy { HomeAdapter() }

    override fun providerVMClass(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getLayoutId(): Int = R.layout.home_fragment
    override fun initData() {
        LiveEventBus.get("refresh_homeBadge").observe(this) {
            initArticles(0)
        }
    }

    private fun initBanner() {
        if (!SettingUtil.getIsShowBanner()) {
            bannerView?.visibility = View.GONE
            return
        }
        bannerView?.visibility = View.VISIBLE
        viewModel.getBanner().observe(activity!!, {
            imgAdapter = ImageAdapter(context ?: BaseApplication.mContext, it)
            bannerView?.run {
                setAdapter(imgAdapter, true)
                setOnBannerListener { data, position ->
                    data as Banner
                    WebViewActivity.start(activity, data.id, data.title, data.url)
                }
            }
        })
    }

    private fun initArticles(page: Int) {
        if (SettingUtil.getIsShowTopArticle() && isRefresh) {
            viewModel.getArticlesAndTopArticles(page).observe(this) {
                it.let { Article ->
                    homeAdapter.run {
                        hideLoading()
                        if (isRefresh) {
                            refreshLayout.finishRefresh()
                            setList(Article)
                            recyclerView.scrollToPosition(0)
                        } else addData(Article)
                        initBadge("home", Article)
                        if (data.size == 0) setEmptyView(R.layout.fragment_empty_layout)
                        else if (hasEmptyView()) removeEmptyView()
                        if (it.size < pageSize) loadMoreModule.loadMoreEnd(isRefresh)
                        else loadMoreModule.loadMoreComplete()
                    }
                }
            }
        } else {
            viewModel.getArticles(page).observe(activity!!, {
                it.let { Article ->
                    homeAdapter.run {
                        if (isRefresh) {
                            refreshLayout.finishRefresh()
                            setList(Article)
                            recyclerView.scrollToPosition(0)
                        } else addData(Article)
                        initBadge("home", Article)
                        if (data.size == 0) setEmptyView(R.layout.fragment_empty_layout)
                        else if (hasEmptyView()) removeEmptyView()
                        if (it.size < pageSize) loadMoreModule.loadMoreEnd(isRefresh)
                        else loadMoreModule.loadMoreComplete()
                    }
                }
            })
        }
    }

    private var homeBadge = 0
    private var homeBadgeOld = 0

    //初始化角标数据
    private fun initBadge(type: String, datas: Any) {
        when (type) {
            "home" -> {
                if (isRefresh)
                    homeBadge = 0
                for (i in 0 until (datas as MutableList<Article>).size) {
                    if (datas[i].fresh && datas[i].top != "1") {
                        homeBadge += 1
                    }
                }
                LiveEventBus.get("homeBadge").post(homeBadge)
                homeBadgeOld = homeBadge
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView(view: View) {
        //初始化网络出错的View
        mTipView = ll_net_error_tip
        mTipView.setOnClickListener {
            //跳转到系统设置页面
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
        //首次启动要判断一下，不然会始终显示网络错误的View
        if (NetWorkUtil.isNetworkConnected(BaseApplication.mContext)) mTipView.visibility =
            View.GONE
        //初始化网络状态的LiveData
        initNetError()

        refreshLayout = swipeRefreshLayout1
        refreshLayout.setRefreshHeader(ch_header)
        refreshLayout.setOnRefreshListener {
            homeAdapter.loadMoreModule.isEnableLoadMore = false
            startHttp()
        }
        recyclerView1.run {
            layoutManager = linearLayoutManager
            adapter = homeAdapter
            itemAnimator = DefaultItemAnimator()
        }
        bannerView = com.youth.banner.Banner<Banner, ImageAdapter>(context)
        bannerView?.run {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.dip2px(context, 230)
            )
            addBannerLifecycleObserver(this@HomeFragment)
            indicator = CircleIndicator(activity)
        }
        homeAdapter.run {
            recyclerView = recyclerView1
            setOnItemClickListener { adapter, view, position ->
                if (data.size != 0) {
                    val data = data[position]
                    WebViewActivity.start(activity, data.id, data.title, data.link)
                }
            }
            addHeaderView(bannerView as View)
            loadMoreModule.setOnLoadMoreListener {
                isRefresh = false
                refreshLayout.finishRefresh()
                val page = homeAdapter.data.size / pageSize
                initArticles(page)
            }
            addChildClickViewIds(R.id.iv_like)
            setOnItemChildClickListener { adapter, view, position ->
                if (data.size == 0) return@setOnItemChildClickListener
                val res = data[position]
                when (view.id) {
                    R.id.iv_like -> {
                        if (!mmkv.decodeBool(Constant.IS_LOGIN, false)) {
                            startActivity(Intent(activity, LoginActivity::class.java))
                            return@setOnItemChildClickListener
                        }
                        val collect = res.collect
                        res.collect = !collect
                        setData(position, res)
                        if (collect) viewModel.cancelCollectArticle(res.id).observe(activity!!, {})
                        else viewModel.addCollectArticle(res.id).observe(activity!!, {})
                    }
                }
            }
        }
        RvAnimUtils.setAnim(homeAdapter, SettingUtil.getListAnimal())
        LiveEventBus.get("rv_anim").observe(this, {
            RvAnimUtils.setAnim(homeAdapter, it)
        })
    }

    private fun initNetError() {
        LiveEventBus.get("isConnected", Boolean::class.java).observe(this, {
            if (it) {
                startHttp()
                mTipView.visibility = View.GONE
            } else {
                mTipView.visibility = View.VISIBLE
            }
        })
    }

    override fun startHttp() {
        showLoading()
        isRefresh = true
        initArticles(0)
        initBanner()
    }

    override fun requestError(it: Exception?) {
        super.requestError(it)
        homeAdapter.loadMoreModule.loadMoreFail()
    }
}