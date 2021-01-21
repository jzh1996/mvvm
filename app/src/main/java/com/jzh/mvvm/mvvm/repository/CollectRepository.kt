package com.jzh.mvvm.mvvm.repository

import com.jzh.mvvm.httpUtils.BaseListResponseBody
import com.jzh.mvvm.httpUtils.CollectionArticle
import com.jzh.mvvm.httpUtils.ResponseData
import com.jzh.mvvm.httpUtils.RetrofitClient

class CollectRepository : CommonRepository() {

    suspend fun getCollectList(page: Int): ResponseData<BaseListResponseBody<CollectionArticle>> =
        request {
            RetrofitClient.service.getCollectList(page)
        }
}