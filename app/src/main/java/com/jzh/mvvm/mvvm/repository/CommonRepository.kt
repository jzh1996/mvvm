package com.jzh.mvvm.mvvm.repository

import com.jzh.mvvm.base.BaseRepository
import com.jzh.mvvm.httpUtils.LoginData
import com.jzh.mvvm.httpUtils.ResponseData
import com.jzh.mvvm.httpUtils.RetrofitClient
import com.jzh.mvvm.httpUtils.TodoResponseBody

/**
 * 通用的Repository
 * Created by jzh on 2021-1-13.
 */
open class CommonRepository : BaseRepository() {

    suspend fun login(name: String, psw: String): ResponseData<LoginData> = request {
        RetrofitClient.service.login(name, psw)
    }

    suspend fun logout(): ResponseData<Any> = request {
        RetrofitClient.service.logout()
    }

    suspend fun register(name: String, psw: String, rePws: String): ResponseData<LoginData> =
        request {
            RetrofitClient.service.register(name, psw, rePws)
        }

    suspend fun addCollectArticle(id: Int): ResponseData<Any> = request {
        RetrofitClient.service.addCollectArticle(id)
    }

    suspend fun addCollectOutsideArticle(
        title: String,
        author: String,
        link: String
    ): ResponseData<Any> = request {
        RetrofitClient.service.addCollectOutsideArticle(title, author, link)
    }

    suspend fun cancelCollectArticle(id: Int): ResponseData<Any> = request {
        RetrofitClient.service.cancelCollectArticle(id)
    }

    suspend fun removeCollectArticle(id: Int, originId: Int): ResponseData<Any> = request {
        RetrofitClient.service.removeCollectArticle(id, originId)
    }

    suspend fun getTodoList(
        page: Int,
        map: MutableMap<String, Any>
    ): ResponseData<TodoResponseBody> =
        request {
            RetrofitClient.service.getTodoList(page, map)
        }
}