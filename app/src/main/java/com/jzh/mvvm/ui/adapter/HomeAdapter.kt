package com.jzh.mvvm.ui.adapter

import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzh.mvvm.R
import com.jzh.mvvm.httpUtils.Article
import com.jzh.mvvm.utils.ImageLoader

class HomeAdapter : BaseQuickAdapter<Article, BaseViewHolder>(R.layout.item_home_list),
    LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: Article) {
        val authorStr = if (item.author.isNotEmpty()) item.author else item.shareUser
        holder.setText(R.id.tv_article_title, Html.fromHtml(item.title))
            .setText(R.id.tv_article_author, authorStr)
            .setText(R.id.tv_article_date, item.niceDate)
            .setImageResource(
                R.id.iv_like,
                if (item.collect) R.drawable.like else R.drawable.not_like
            )
        val chapterName = when {
            item.superChapterName.isNotEmpty() and item.chapterName.isNotEmpty() ->
                "${item.superChapterName} - ${item.chapterName}"
            item.superChapterName.isNotEmpty() -> item.superChapterName
            item.chapterName.isNotEmpty() -> item.chapterName
            else -> ""
        }
        holder.setText(R.id.tv_article_chapterName, chapterName)
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
        val tv_fresh = holder.getView<TextView>(R.id.tv_article_fresh)
        if (item.fresh) {
            tv_fresh.visibility = View.VISIBLE
        } else {
            tv_fresh.visibility = View.GONE
        }
        val tv_top = holder.getView<TextView>(R.id.tv_article_top)
        if (item.top == "1") {
            tv_top.visibility = View.VISIBLE
        } else {
            tv_top.visibility = View.GONE
        }
        val tv_article_tag = holder.getView<TextView>(R.id.tv_article_tag)
        if (item.tags.size > 0) {
            tv_article_tag.visibility = View.VISIBLE
            tv_article_tag.text = item.tags[0].name
        } else {
            tv_article_tag.visibility = View.GONE
        }
    }
}