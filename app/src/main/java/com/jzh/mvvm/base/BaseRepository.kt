package com.jzh.mvvm.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.jzh.mvvm.httpUtils.DataState
import com.jzh.mvvm.httpUtils.NewResponseData
import com.jzh.mvvm.httpUtils.ResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

open class BaseRepository {

    suspend fun <T : Any> request(call: suspend () -> ResponseData<T>): ResponseData<T> {
        return withContext(Dispatchers.IO) {
            call.invoke()
        }.apply {
            Log.d("接口返回数据----->", this.toString())
            //这儿可以对返回结果errorCode做一些特殊处理，比如token失效等，可以通过抛出异常的方式实现
            //例：当token失效时，后台返回errorCode 为 100，下面代码实现,再到baseActivity通过观察error来处理
            when (errorCode) {
                //一般0和200是请求成功，直接返回数据
                0, 200 -> this
                100, 401 -> throw TokenInvalidException(errorMsg)
                403 -> throw NoPermissionsException(errorMsg)
                404 -> throw NotFoundException(errorMsg)
                500 -> throw InterfaceErrException(errorMsg)
                504 -> throw TimeOutErrException(errorMsg)
                else -> throw Exception(errorMsg)
            }
        }
    }

    class TokenInvalidException(msg: String = "token失效，请重新登录") : Exception(msg)
    class NoPermissionsException(msg: String = "您没有操作权限，请联系管理员开通") : Exception(msg)
    class NotFoundException(msg: String = "请求的地址不存在") : Exception(msg)
    class InterfaceErrException(msg: String = "接口请求出错") : Exception(msg)
    class TimeOutErrException(msg: String = "连接超时") : Exception(msg)


    /**
     * 结合Flow请求数据。
     * 根据Flow的不同请求状态，如onStart、onEmpty、onCompletion等设置baseResp.dataState状态值，
     * 最后通过stateLiveData分发给UI层。
     *
     * @param block api的请求方法
     * @param stateLiveData 每个请求传入相应的LiveData，主要负责网络状态的监听
     */
    suspend fun <T : Any> requestWithFlow(
        block: suspend () -> NewResponseData<T>,
        stateLiveData: MutableLiveData<NewResponseData<T>>,
    ) {
        var baseResp =  NewResponseData<T>()
        flow {
            //设置超时时间
            val respResult = withTimeout(60 * 1000) {
                block.invoke()
            }
            baseResp = respResult
            baseResp.dataState = DataState.STATE_SUCCESS
            if (baseResp.errorCode != 0 && baseResp.errorCode != 200) {
                baseResp.dataState = DataState.STATE_FAILED
            }
            emit(respResult)
        }
            .flowOn(Dispatchers.IO)
            .onStart {
                baseResp.dataState = DataState.STATE_LOADING
                stateLiveData.postValue(baseResp)
            }
            .catch { exception ->
                run {
                    exception.printStackTrace()
                    baseResp.dataState = DataState.STATE_ERROR
                    baseResp.error = exception
                    stateLiveData.postValue(baseResp)
                }
            }
            .collect {
                stateLiveData.postValue(baseResp)
            }
    }
}