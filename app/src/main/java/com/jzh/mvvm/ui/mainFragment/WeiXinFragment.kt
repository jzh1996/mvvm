package com.jzh.mvvm.ui.mainFragment

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseViewModelFragment
import com.jzh.mvvm.httpUtils.WXChapterBean
import com.jzh.mvvm.mvvm.mainViewModel.WeiXinViewModel
import com.jzh.mvvm.ui.fragment.KnowListFragment
import kotlinx.android.synthetic.main.wei_xin_fragment.*
import java.lang.Exception

class WeiXinFragment : BaseViewModelFragment<WeiXinViewModel>() {

    companion object {
        fun newInstance() = WeiXinFragment()
    }

    private val data = mutableListOf<WXChapterBean>()
    private val viewPagerAdapter by lazy { WeXinPagerAdapter() }
    private lateinit var vp2: ViewPager2
    private lateinit var tbLayout: TabLayout

    override fun providerVMClass(): Class<WeiXinViewModel> = WeiXinViewModel::class.java

    override fun getLayoutId(): Int = R.layout.wei_xin_fragment

    override fun initData() {}

    override fun initView(view: View) {
        vp2 = vp2_weixin
        tbLayout = tl_weixin
        tbLayout.run {
            addOnTabSelectedListener(onTabSelectedListener)
        }
    }

    override fun startHttp() {
        getWeiXin()
    }

    private fun getWeiXin() {
        viewModel.getWeiXin().observe(activity!!, {
            data.addAll(it)
            vp2.run {
                adapter = viewPagerAdapter
                offscreenPageLimit = data.size
            }
            TabLayoutMediator(tbLayout, vp2) { tab, position ->
                tab.text = data[position].name
            }.attach()
        })
    }

    inner class WeXinPagerAdapter : FragmentStateAdapter(this) {

        private val fragments = mutableListOf<Fragment>()

        init {
            fragments.clear()
            data.forEach {
                fragments.add(KnowListFragment.newInstance(it.id))
            }
        }

        override fun getItemCount(): Int = data.size

        override fun createFragment(position: Int): Fragment =
            KnowListFragment.newInstance(data[position].id)
    }

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {}

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let {
                vp2.currentItem = it.position
            }
        }
    }
}