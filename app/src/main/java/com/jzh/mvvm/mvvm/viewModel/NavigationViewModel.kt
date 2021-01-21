package com.jzh.mvvm.mvvm.viewModel

import androidx.lifecycle.LiveData
import com.jzh.mvvm.httpUtils.NavigationBean
import com.jzh.mvvm.mvvm.mainRepository.SystemRepository
import com.jzh.mvvm.utils.SingleLiveEvent

class NavigationViewModel : CommonViewModel() {

    private val repository = SystemRepository()
    private val navigationTree = SingleLiveEvent<List<NavigationBean>>()

    fun getNavigationTree(): LiveData<List<NavigationBean>>{
        launchUI {
            val res = repository.getNavigationTree()
            navigationTree.value = res.data
        }
        return navigationTree
    }
}