package com.jzh.mvvm.ui.activity.common

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseActivity
import com.jzh.mvvm.httpUtils.Knowledge
import com.jzh.mvvm.httpUtils.KnowledgeTreeBody
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.ui.adapter.KnowledgePagerAdapter
import kotlinx.android.synthetic.main.activity_knowledge.*

class KnowledgeActivity : BaseActivity() {

    private var data = mutableListOf<Knowledge>()
    private val viewPagerAdapter: KnowledgePagerAdapter by lazy {
        KnowledgePagerAdapter(this@KnowledgeActivity, data)
    }
    private lateinit var title: String
    private lateinit var vp2: ViewPager2
    private var pos: Int = 0

    override fun getLayoutId(): Int = R.layout.activity_knowledge

    override fun initData() {
        intent?.extras?.let {
            title = it.getString(Constant.CONTENT_TITLE_KEY) ?: ""
            pos = it.getInt("postion")
            it.getSerializable(Constant.CONTENT_DATA_KEY)?.let { res ->
                val data1 = res as KnowledgeTreeBody
                data1.children.let { data.addAll(it) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        setTop(title)
        vp2 = vp2_know
        vp2.run {
            adapter = viewPagerAdapter
//            offscreenPageLimit = data.size
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {})
            currentItem = pos
        }
        tl_know.run {
            addOnTabSelectedListener(onTabSelectedListener)
        }
        TabLayoutMediator(tl_know, vp2) { tab, position ->
            tab.text = viewPagerAdapter.list[position].name
        }.attach()
    }

    override fun startHttp() {}

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {}

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let { vp2.currentItem = it.position }
        }
    }
}