package com.jzh.mvvm.mvvm.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.jzh.mvvm.base.BaseViewModel
import com.jzh.mvvm.httpUtils.LoginData
import com.jzh.mvvm.httpUtils.TodoResponseBody
import com.jzh.mvvm.mvvm.repository.CommonRepository
import com.jzh.mvvm.utils.SingleLiveEvent

/**
 * 通用的ViewModel,如收藏,登录等接口很多页面都用
 * Created by jzh on 2021-1-15.
 */
open class CommonViewModel : BaseViewModel() {

    private val repository = CommonRepository()
    private val addCollect = SingleLiveEvent<Any>()
    private val addCollectOutside = SingleLiveEvent<Any>()
    private val cancelCollect = SingleLiveEvent<Any>()
    private val removeCollect = SingleLiveEvent<Any>()
    private var loginData = SingleLiveEvent<LoginData>()
    private var logoutData = SingleLiveEvent<Any>()
    private var registerData = SingleLiveEvent<LoginData>()
    private var data = SingleLiveEvent<TodoResponseBody>()

    /**
     * 如果需要对返回的数据进行操作后再返回给调用者
     * 可以使用Transformations.map(data) {}或 Transformations.switchMap(data){}对data进行处理
     * 详见 https://developer.android.google.cn/topic/libraries/architecture/livedata
     */
    fun getTodoList(page: Int, map: MutableMap<String, Any>): LiveData<TodoResponseBody> {
        launchUI {
            val res = repository.getTodoList(page, map)
            data.value = res.data
        }
        return data
    }

    fun addCollectArticle(id: Int): LiveData<Any> {
        launchUI {
            val res = repository.addCollectArticle(id)
            addCollect.value = res
        }
        return addCollect
    }

    fun addCollectOutsideArticle(
        title: String,
        author: String,
        link: String
    ): LiveData<Any> {
        launchUI {
            val res = repository.addCollectOutsideArticle(title, author, link)
            addCollectOutside.value = res
        }
        return addCollectOutside
    }

    fun cancelCollectArticle(id: Int): LiveData<Any> {
        launchUI {
            val res = repository.cancelCollectArticle(id)
            cancelCollect.value = res
        }
        return cancelCollect
    }

    fun removeCollectArticle(page: Int, originId: Int): LiveData<Any> {
        launchUI {
            val res = repository.removeCollectArticle(page, originId)
            removeCollect.value = res
        }
        return removeCollect
    }

    fun login(name: String, psw: String): LiveData<LoginData> {
        launchUI {
            val result = repository.login(name, psw)
            loginData.value = result.data
        }
        return loginData
    }

    fun logout(): LiveData<Any> {
        launchUI {
            val result = repository.logout()
            logoutData.value = result.data
        }
        return logoutData
    }

    fun register(name: String, psw: String, rePws: String): LiveData<LoginData> {
        launchUI {
            val result = repository.register(name, psw, rePws)
            registerData.value = result.data
        }
        return registerData
    }
}