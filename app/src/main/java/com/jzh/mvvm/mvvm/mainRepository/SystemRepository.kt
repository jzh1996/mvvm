package com.jzh.mvvm.mvvm.mainRepository

import com.jzh.mvvm.httpUtils.ArticleResponseBody
import com.jzh.mvvm.httpUtils.KnowledgeTreeBody
import com.jzh.mvvm.httpUtils.NavigationBean
import com.jzh.mvvm.httpUtils.ResponseData
import com.jzh.mvvm.httpUtils.RetrofitClient
import com.jzh.mvvm.mvvm.repository.CommonRepository

class SystemRepository : CommonRepository() {

    suspend fun getKnowledgeTree(): ResponseData<List<KnowledgeTreeBody>> = request {
        RetrofitClient.service.getKnowledgeTree()
    }

    suspend fun getNavigationTree(): ResponseData<List<NavigationBean>> = request {
        RetrofitClient.service.getNavigationList()
    }

    suspend fun getKnowledgeList(page: Int, cid: Int): ResponseData<ArticleResponseBody> = request {
        RetrofitClient.service.getKnowledgeList(page, cid)
    }
}