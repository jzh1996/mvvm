package com.jzh.mvvm.ui.adapter

import android.text.Html
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzh.mvvm.R
import com.jzh.mvvm.httpUtils.CollectionArticle
import com.jzh.mvvm.utils.ImageLoader

class CollectAdapter :
    BaseQuickAdapter<CollectionArticle, BaseViewHolder>(R.layout.item_collect_list),
    LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: CollectionArticle) {
        val name = if (item.author.isNotEmpty()) item.author else "匿名"
        val chapterName = if (item.chapterName.isNotEmpty()) item.chapterName else "站外收藏"
        holder.setText(R.id.tv_article_title, Html.fromHtml(item.title))
            .setText(R.id.tv_article_author, name)
            .setText(R.id.tv_article_date, item.niceDate)
            .setText(R.id.tv_article_chapterName, chapterName)
            .setImageResource(R.id.iv_like, R.drawable.like)
        if (item.envelopePic.isNotEmpty()) {
            holder.getView<ImageView>(R.id.iv_article_thumbnail)
                .visibility = View.VISIBLE
            context.let {
                ImageLoader.load(it, item.envelopePic, holder.getView(R.id.iv_article_thumbnail))
            }
        } else {
            holder.getView<ImageView>(R.id.iv_article_thumbnail)
                .visibility = View.GONE
        }
    }
}