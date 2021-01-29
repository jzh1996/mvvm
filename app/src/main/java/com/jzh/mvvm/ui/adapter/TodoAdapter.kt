package com.jzh.mvvm.ui.adapter

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzh.mvvm.R
import com.jzh.mvvm.httpUtils.TodoBean

class TodoAdapter : BaseQuickAdapter<TodoBean, BaseViewHolder>(R.layout.item_todo_list),
    LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: TodoBean) {
        holder.setText(R.id.tv_todo_title, item.title)
            .setText(R.id.item_todo_time, item.dateStr)
        if (item.content.isNotEmpty()) {
            holder.getView<TextView>(R.id.tv_todo_desc).visibility = View.VISIBLE
            holder.setText(R.id.tv_todo_desc, item.content)
        } else {
            holder.getView<TextView>(R.id.tv_todo_desc).visibility = View.INVISIBLE
            holder.setText(R.id.tv_todo_desc, "")
        }
        val done = holder.getView<Button>(R.id.item_todo_done)
        val status = holder.getView<TextView>(R.id.item_todo_status)
        if (item.status == 0) {
            status.visibility = View.VISIBLE
            done.text = context.resources.getString(R.string.done)
        } else if (item.status == 1) {
            status.visibility = View.GONE
            done.text = context.resources.getString(R.string.daiban)
        }
        val imp = holder.getView<TextView>(R.id.item_todo_imp)
        if (item.priority == 1) {
            imp.text = context.resources.getString(R.string.important)
            imp.visibility = View.VISIBLE
        } else imp.visibility = View.GONE
    }
}