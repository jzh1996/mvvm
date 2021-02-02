package com.jzh.mvvm.ui.mainFragment

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseViewModelFragment
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.mvvm.mainViewModel.QuestionViewModel
import com.jzh.mvvm.ui.activity.login.LoginActivity
import com.jzh.mvvm.ui.adapter.HomeAdapter
import com.jzh.mvvm.utils.MyMMKV
import com.jzh.mvvm.utils.RvAnimUtils
import com.jzh.mvvm.utils.SettingUtil
import com.jzh.mvvm.utils.toast
import com.jzh.mvvm.webView.WebViewActivity
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.question_fragment.*
import java.lang.Exception

class QuestionFragment : BaseViewModelFragment<QuestionViewModel>() {

    companion object {
        fun newInstance() = QuestionFragment()
    }

    private var isRefresh = true
    private lateinit var refreshLayout: SmartRefreshLayout
    private val linearLayoutManager by lazy { LinearLayoutManager(activity) }
    private val questionAdapter: HomeAdapter by lazy { HomeAdapter() }

    override fun getLayoutId(): Int = R.layout.question_fragment
    override fun initData() {}

    override fun initView(view: View) {
        refreshLayout = swipeRefreshLayout1_question
        refreshLayout.run {
            setRefreshHeader(ch_header_question)
            setOnRefreshListener {
                startHttp()
            }
        }
        recyclerView1_question.run {
            layoutManager = linearLayoutManager
            adapter = questionAdapter
            itemAnimator = DefaultItemAnimator()
        }
        questionAdapter.run {
            recyclerView = recyclerView1_question
            setOnItemClickListener { adapter, view, position ->
                if (data.size != 0) {
                    val data = data[position]
                    WebViewActivity.start(activity, data.id, data.title, data.link)
                }
            }
            loadMoreModule.setOnLoadMoreListener {
                isRefresh = false
                refreshLayout.finishRefresh()
                val page = questionAdapter.data.size / pageSize
                getQuestionList(page)
            }
            addChildClickViewIds(R.id.iv_like)
            setOnItemChildClickListener { adapter, view, position ->
                if (data.size == 0) return@setOnItemChildClickListener
                val res = data[position]
                when (view.id) {
                    R.id.iv_like -> {
                        if (!MyMMKV.mmkv.decodeBool(Constant.IS_LOGIN, false)) {
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
        RvAnimUtils.setAnim(questionAdapter, SettingUtil.getListAnimal())
        LiveEventBus.get("rv_anim").observe(this, {
            RvAnimUtils.setAnim(questionAdapter, it)
        })
    }

    override fun startHttp() {
        showLoading()
        isRefresh = true
        getQuestionList(0)
    }

    private fun getQuestionList(page: Int) {
        viewModel.getQuestionList(page).observe(activity!!, {
            it.datas.let { article ->
                hideLoading()
                questionAdapter.run {
                    if (isRefresh) {
                        refreshLayout.finishRefresh()
                        setList(article)
                        recyclerView.scrollToPosition(0)
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
        questionAdapter.loadMoreModule.loadMoreFail()
    }

    override fun providerVMClass(): Class<QuestionViewModel> = QuestionViewModel::class.java
}