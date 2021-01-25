package com.jzh.mvvm.ui.adapter

import android.view.LayoutInflater
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.flexbox.FlexboxLayout
import com.jzh.mvvm.R
import com.jzh.mvvm.httpUtils.KnowledgeTreeBody
import java.util.*

class KnowledgeAdapter :
    BaseQuickAdapter<KnowledgeTreeBody, BaseViewHolder>(R.layout.item_knowledge_tree_list),
    LoadMoreModule {

    private var mInflater: LayoutInflater? = null
    private val mFlexItemTextViewCaches: Queue<TextView> = LinkedList()
    private var mOnItemClickListener: OnItemClickListener? = null

    internal fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    override fun convert(holder: BaseViewHolder, item: KnowledgeTreeBody) {
        holder.setText(R.id.tv_knowledge_item_title, item.name)
        val fbl: FlexboxLayout = holder.getView(R.id.fbl_knowledge)
        item.children.let {
            for (i in 0 until it.size) {
                val childTextView = createOrGetCacheFlexItemTextView(fbl)
                childTextView.text = it[i].name
                childTextView.setOnClickListener {
                    mOnItemClickListener?.onClick(item, i)
                }
                fbl.addView(childTextView)
            }
        }
    }

    private fun createOrGetCacheFlexItemTextView(fbl: FlexboxLayout): TextView {
        val tv = mFlexItemTextViewCaches.poll()
        return tv ?: createFlexItemTextView(fbl)
    }

    private fun createFlexItemTextView(fbl: FlexboxLayout): TextView {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(fbl.context)
        }
        return mInflater!!.inflate(R.layout.item_knowledge_text, fbl, false) as TextView
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)
        val fbl = holder.getView<FlexboxLayout>(R.id.fbl_knowledge)
        for (i in 0 until fbl.childCount) {
            //offer:添加一个元素并返回true,如果队列已满,则返回false
            mFlexItemTextViewCaches.offer(fbl.getChildAt(i) as TextView)
        }
        fbl.removeAllViews()
    }

    interface OnItemClickListener {
        fun onClick(bean: KnowledgeTreeBody, pos: Int)
    }
}