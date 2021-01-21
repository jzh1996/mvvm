package com.jzh.mvvm.mvvm.repository

import com.jzh.mvvm.httpUtils.ArticleResponseBody
import com.jzh.mvvm.httpUtils.ResponseData
import com.jzh.mvvm.httpUtils.RetrofitClient

class GroupRepository : CommonRepository() {

    suspend fun getGroupList(page: Int): ResponseData<ArticleResponseBody> = request {
        RetrofitClient.service.getGroupList(page)
    }
}