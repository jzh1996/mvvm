package com.jzh.mvvm.mvvm.viewModel

import androidx.lifecycle.LiveData
import com.jzh.mvvm.httpUtils.BaseListResponseBody
import com.jzh.mvvm.httpUtils.CollectionArticle
import com.jzh.mvvm.mvvm.repository.CollectRepository
import com.jzh.mvvm.utils.SingleLiveEvent

class MyCollectActivityViewModel : CommonViewModel() {

    private val repository = CollectRepository()
    private val collectList = SingleLiveEvent<BaseListResponseBody<CollectionArticle>>()

    fun getCollectList(page: Int): LiveData<BaseListResponseBody<CollectionArticle>> {
        launchUI {
            val res = repository.getCollectList(page)
            collectList.value = res.data
        }
        return collectList
    }
}