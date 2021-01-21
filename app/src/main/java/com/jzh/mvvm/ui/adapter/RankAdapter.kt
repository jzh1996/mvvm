package com.jzh.mvvm.ui.adapter

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzh.mvvm.R
import com.jzh.mvvm.httpUtils.CoinInfoBean

class RankAdapter : BaseQuickAdapter<CoinInfoBean, BaseViewHolder>(R.layout.rank_list_item),
    LoadMoreModule {

    private val mScale = 1000
    private var mMax = 0

    override fun convert(holder: BaseViewHolder, item: CoinInfoBean) {
        mMax = data[0].coinCount
        val pb = holder.getView<ProgressBar>(R.id.pb_rank_list)
        pb.max = mMax * mScale
        cancelProgressAnim(pb)
        if (!item.anim) {
            item.anim = true
            doProgressAnim(pb, item.coinCount)
        } else pb.progress = item.coinCount * mScale
        holder.setText(R.id.tv_rank_num, "${holder.layoutPosition + 1}")
        holder.setText(R.id.tv_rank_name, item.username)
        holder.setText(R.id.tv_rank_count, "${item.coinCount}")
    }

    private fun doProgressAnim(pb: ProgressBar, to: Int) {
        val animator = ValueAnimator.ofInt(0, to)
        animator.duration = 1000
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            pb.progress = animation.animatedValue as Int * mScale
        }
        pb.tag = animator
        animator.start()
    }

    private fun cancelProgressAnim(pb: ProgressBar) {
        val obj = pb.tag
        if (obj is Animator) {
            obj.cancel()
        }
        pb.tag = null
    }
}