package com.jzh.mvvm.mvvm.mainViewModel

import androidx.lifecycle.LiveData
import com.jzh.mvvm.mvvm.viewModel.CommonViewModel
import com.jzh.mvvm.httpUtils.UserInfoBody
import com.jzh.mvvm.mvvm.mainRepository.MyRepository
import com.jzh.mvvm.utils.SingleLiveEvent

class MyViewModel : CommonViewModel() {

    private val repository = MyRepository()
    private val userInfo = SingleLiveEvent<UserInfoBody>()

    fun getUserInfo(): LiveData<UserInfoBody> {
        launchUI {
            val result = repository.getUserInfo()
            userInfo.value = result.data
        }
        return userInfo
    }

}