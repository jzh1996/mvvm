package com.jzh.mvvm.mvvm.viewModel

import androidx.lifecycle.LiveData
import com.jzh.mvvm.httpUtils.BaseListResponseBody
import com.jzh.mvvm.httpUtils.CoinInfoBean
import com.jzh.mvvm.httpUtils.UserInfoBody
import com.jzh.mvvm.httpUtils.UserScoreBean
import com.jzh.mvvm.mvvm.repository.ScoreRepository
import com.jzh.mvvm.utils.SingleLiveEvent

class MyScoreActivityViewModel : CommonViewModel() {

    private val repository = ScoreRepository()
    private val data = SingleLiveEvent<BaseListResponseBody<UserScoreBean>>()
    private val userInfo = SingleLiveEvent<UserInfoBody>()
    private val rankList = SingleLiveEvent<BaseListResponseBody<CoinInfoBean>>()

    fun getScoreList(page: Int): LiveData<BaseListResponseBody<UserScoreBean>> {
        launchUI {
            var result = repository.getScoreList(page)
            data.value = result.data
        }
        return data
    }

    fun getUserInfo(): LiveData<UserInfoBody> {
        launchUI {
            val result = repository.getUserInfo()
            userInfo.value = result.data
        }
        return userInfo
    }


    fun getRankList(page: Int): LiveData<BaseListResponseBody<CoinInfoBean>> {
        launchUI {
            val result = repository.getRankList(page)
            rankList.value = result.data
        }
        return rankList
    }
}