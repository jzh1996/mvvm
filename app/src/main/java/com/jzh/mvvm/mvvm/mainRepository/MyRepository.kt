package com.jzh.mvvm.mvvm.mainRepository

import com.jzh.mvvm.httpUtils.*
import com.jzh.mvvm.mvvm.repository.CommonRepository

class MyRepository : CommonRepository() {

    suspend fun getUserInfo(): ResponseData<UserInfoBody> = request {
        RetrofitClient.service.getUserInfo()
    }
}