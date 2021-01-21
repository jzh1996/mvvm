package com.jzh.mvvm.mvvm.mainRepository

import com.jzh.mvvm.httpUtils.ArticleListBean
import com.jzh.mvvm.httpUtils.ResponseData
import com.jzh.mvvm.httpUtils.RetrofitClient
import com.jzh.mvvm.mvvm.repository.CommonRepository

class QuestionRepository : CommonRepository() {

    suspend fun getQuestionList(page: Int): ResponseData<ArticleListBean> = request {
        RetrofitClient.service.getQuestionList(page)
    }
}