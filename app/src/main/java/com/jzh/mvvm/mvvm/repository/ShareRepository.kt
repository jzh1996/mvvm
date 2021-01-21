package com.jzh.mvvm.mvvm.repository

import com.jzh.mvvm.httpUtils.ResponseData
import com.jzh.mvvm.httpUtils.RetrofitClient
import com.jzh.mvvm.httpUtils.ShareResponseBody

class ShareRepository : CommonRepository() {

    suspend fun shareArticle(map: MutableMap<String, Any>): ResponseData<Any> = request {
        RetrofitClient.service.shareArticle(map)
    }

    suspend fun getShareList(page: Int): ResponseData<ShareResponseBody> = request {
        RetrofitClient.service.getShareList(page)
    }

    suspend fun deleteShareArticle(id: Int): ResponseData<Any> = request {
        RetrofitClient.service.deleteShareArticle(id)
    }
}