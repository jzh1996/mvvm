package com.jzh.mvvm.ui.adapter

import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzh.mvvm.R
import com.jzh.mvvm.httpUtils.Article
import com.jzh.mvvm.utils.ImageLoader

class KnowledgeListAdapter :
    BaseQuickAdapter<Article, BaseViewHolder>(R.layout.item_knowledge_list),
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

        if (!TextUtils.isEmpty(item.envelopePic)) {
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