package com.jzh.mvvm.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jzh.mvvm.httpUtils.Knowledge
import com.jzh.mvvm.ui.fragment.KnowListFragment

class KnowledgePagerAdapter(val activity: FragmentActivity, val list: List<Knowledge>) :
    FragmentStateAdapter(activity) {

    private val fragments = mutableListOf<Fragment>()

    init {
        fragments.clear()
        list.forEach {
            fragments.add(KnowListFragment.newInstance(it.id))
        }
    }

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        return KnowListFragment.newInstance(list[position].id)
    }
}