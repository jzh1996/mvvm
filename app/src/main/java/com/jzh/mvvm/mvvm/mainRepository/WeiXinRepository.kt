package com.jzh.mvvm.mvvm.mainRepository

import com.jzh.mvvm.httpUtils.ResponseData
import com.jzh.mvvm.httpUtils.WXChapterBean
import com.jzh.mvvm.httpUtils.RetrofitClient
import com.jzh.mvvm.mvvm.repository.CommonRepository

class WeiXinRepository : CommonRepository() {

    suspend fun getWeiXin(): ResponseData<List<WXChapterBean>> = request {
        RetrofitClient.service.getWeiXin()
    }
}