package com.jzh.mvvm.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseViewModelFragment
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.mvvm.viewModel.KnowListViewModel
import com.jzh.mvvm.ui.activity.login.LoginActivity
import com.jzh.mvvm.ui.adapter.KnowledgeListAdapter
import com.jzh.mvvm.utils.MyMMKV.Companion.mmkv
import com.jzh.mvvm.utils.RvAnimUtils
import com.jzh.mvvm.utils.SettingUtil
import com.jzh.mvvm.webView.WebViewActivity
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.know_list_fragment.*
import java.lang.Exception

class KnowListFragment : BaseViewModelFragment<KnowListViewModel>() {

    companion object {
        fun newInstance(id: Int): KnowListFragment {
            val fragment = KnowListFragment()
            val args = Bundle()
            args.putInt(Constant.CONTENT_CID_KEY, id)
            fragment.arguments = args
            return fragment
        }
    }

    private var cid = 0
    private val knowledgeListAdapter by lazy { KnowledgeListAdapter() }
    private var isRefresh = true
    private lateinit var refreshLayout: SmartRefreshLayout
    private val linearLayoutManager by lazy { LinearLayoutManager(activity) }

    override fun getLayoutId(): Int = R.layout.know_list_fragment

    override fun initData() {
        cid = arguments?.getInt(Constant.CONTENT_CID_KEY) ?: 0
    }

    override fun initView(view: View) {
        refreshLayout = swipeRefreshLayout_know_list
        refreshLayout.setRefreshHeader(ch_header_know_list)
        refreshLayout.setOnRefreshListener {
            knowledgeListAdapter.loadMoreModule.isEnableLoadMore = false
            startHttp()
        }
        recyclerView_know_list.run {
            layoutManager = linearLayoutManager
            adapter = knowledgeListAdapter
            itemAnimator = DefaultItemAnimator()
        }
        knowledgeListAdapter.run {
            recyclerView = recyclerView_know_list
            setOnItemClickListener { adapter, view, position ->
                if (data.size != 0) {
                    val data = data[position]
                    WebViewActivity.start(activity, data.id, data.title, data.link)
                }
            }
            loadMoreModule.setOnLoadMoreListener {
                isRefresh = false
                refreshLayout.finishRefresh()
                val page = knowledgeListAdapter.data.size / pageSize
                initKnowledgeList(page)
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
                        else viewModel.addCollectArticle(res.id).observe(activity!!,{})
                    }
                }
            }
        }
        RvAnimUtils.setAnim(knowledgeListAdapter, SettingUtil.getListAnimal())
        LiveEventBus.get("rv_anim").observe(this, {
            RvAnimUtils.setAnim(knowledgeListAdapter, it)
        })
    }

    private fun initKnowledgeList(page: Int) {
        viewModel.getKnowledgeList(page, cid).observe(activity!!, {
            it.datas.let { Article ->
                hideLoading()
                knowledgeListAdapter.run {
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                        setList(Article)
                        recyclerView.scrollToPosition(0)
                    } else addData(Article)
                    if (data.size == 0) setEmptyView(R.layout.fragment_empty_layout)
                    else if (hasEmptyView()) removeEmptyView()
                    if (it.over) loadMoreModule.loadMoreEnd(isRefresh)
                    else loadMoreModule.loadMoreComplete()
                }
            }
        })
    }

    override fun startHttp() {
        showLoading()
        isRefresh = true
        initKnowledgeList(0)
    }

    override fun requestError(it: Exception?) {
        super.requestError(it)
        knowledgeListAdapter.loadMoreModule.loadMoreFail()
    }

    override fun providerVMClass(): Class<KnowListViewModel> = KnowListViewModel::class.java
}