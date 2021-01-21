package com.jzh.mvvm.mvvm.viewModel

import androidx.lifecycle.LiveData
import com.jzh.mvvm.httpUtils.ArticleResponseBody
import com.jzh.mvvm.mvvm.repository.GroupRepository
import com.jzh.mvvm.utils.SingleLiveEvent

class GroupActivityViewModel : CommonViewModel() {

    private val repository = GroupRepository()
    private val data = SingleLiveEvent<ArticleResponseBody>()

    fun getGroupList(page: Int): LiveData<ArticleResponseBody> {
        launchUI {
            val result = repository.getGroupList(page)
            data.value = result.data
        }
        return data
    }
}