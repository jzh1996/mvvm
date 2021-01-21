package com.jzh.mvvm.ui.fragment

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseViewModelFragment
import com.jzh.mvvm.httpUtils.KnowledgeTreeBody
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.mvvm.viewModel.KnowledgeViewModel
import com.jzh.mvvm.ui.activity.common.KnowledgeActivity
import com.jzh.mvvm.ui.adapter.KnowledgeAdapter
import kotlinx.android.synthetic.main.knowledge_fragment.*
import java.io.Serializable
import java.lang.Exception

class KnowledgeFragment : BaseViewModelFragment<KnowledgeViewModel>() {

    companion object {
        fun newInstance() = KnowledgeFragment()
    }

    private val linearLayoutManager by lazy { LinearLayoutManager(activity) }
    private val knowledgeAdapter: KnowledgeAdapter by lazy { KnowledgeAdapter() }

    override fun getLayoutId(): Int = R.layout.knowledge_fragment

    override fun initData() {}

    override fun initView(view: View) {
        recyclerView_work.run {
            layoutManager = linearLayoutManager
            adapter = knowledgeAdapter
        }
        knowledgeAdapter.run {
            recyclerView = recyclerView_work
            setOnItemClickListener(object : KnowledgeAdapter.OnItemClickListener {
                override fun onClick(bean: KnowledgeTreeBody, pos: Int) {
                    Intent(activity, KnowledgeActivity::class.java).run {
                        putExtra(Constant.CONTENT_TITLE_KEY, bean.name)
                        putExtra(Constant.CONTENT_DATA_KEY, bean as Serializable)
                        putExtra("postion", pos)
                        startActivity(this)
                    }
                }
            })
        }
    }

    override fun startHttp() {
        initKnowledgeTree()
    }

    private fun initKnowledgeTree() {
        viewModel = ViewModelProvider(this).get(KnowledgeViewModel::class.java)
        viewModel.getKnowledgeTree()
            .observe(activity!!, { list ->
                knowledgeAdapter.run {
                    setList(list)
                }
            })
    }

    override fun requestError(it: Exception?) {
        super.requestError(it)
    }

    override fun providerVMClass(): Class<KnowledgeViewModel> = KnowledgeViewModel::class.java
}