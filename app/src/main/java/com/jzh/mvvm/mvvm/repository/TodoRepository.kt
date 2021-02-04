package com.jzh.mvvm.mvvm.repository

import com.jzh.mvvm.httpUtils.ResponseData
import com.jzh.mvvm.httpUtils.RetrofitClient

class TodoRepository : CommonRepository() {

    suspend fun updateTodoById(id: Int, status: Int): ResponseData<Any> =
        request {
            RetrofitClient.service.updateTodoById(id, status)
        }

    suspend fun deleteTodoById(page: Int): ResponseData<Any> =
        request {
            RetrofitClient.service.deleteTodoById(page)
        }

    suspend fun addTodo(map: MutableMap<String, Any>): ResponseData<Any> =
        request {
            RetrofitClient.service.addTodo(map)
        }

    suspend fun updateTodo(
        id: Int,
        map: MutableMap<String, Any>
    ): ResponseData<Any> =
        request {
            RetrofitClient.service.updateTodo(id, map)
        }
}