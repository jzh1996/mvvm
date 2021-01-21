package com.jzh.mvvm.mvvm.mainViewModel

import androidx.lifecycle.LiveData
import com.jzh.mvvm.mvvm.viewModel.CommonViewModel
import com.jzh.mvvm.httpUtils.WXChapterBean
import com.jzh.mvvm.mvvm.mainRepository.WeiXinRepository
import com.jzh.mvvm.utils.SingleLiveEvent

class WeiXinViewModel : CommonViewModel() {

    private val repository = WeiXinRepository()
    private val weiXinData = SingleLiveEvent<List<WXChapterBean>>()

    fun getWeiXin(): LiveData<List<WXChapterBean>> {
        launchUI {
            val result = repository.getWeiXin()
            weiXinData.value = result.data
        }
        return weiXinData
    }
}