package com.jzh.mvvm.ui.adapter

import android.annotation.SuppressLint
import android.text.Html
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzh.mvvm.R
import com.jzh.mvvm.db.model.ReadLaterModel
import com.jzh.mvvm.db.model.ReadRecordModel
import java.text.SimpleDateFormat

class ReadRecordAdapter :
    BaseQuickAdapter<ReadRecordModel, BaseViewHolder>(R.layout.item_read_list),
    LoadMoreModule {

    private lateinit var mSimpleDateFormat: SimpleDateFormat

    @SuppressLint("SimpleDateFormat")
    override fun convert(holder: BaseViewHolder, item: ReadRecordModel) {
        mSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        holder.setText(R.id.tv_read_title, Html.fromHtml(item.title))
            .setText(R.id.tv_read_time, mSimpleDateFormat.format(item.time))
    }
}