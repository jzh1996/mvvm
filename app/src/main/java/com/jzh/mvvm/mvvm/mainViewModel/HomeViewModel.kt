package com.jzh.mvvm.mvvm.mainViewModel

import androidx.lifecycle.LiveData
import com.jzh.mvvm.httpUtils.Article
import com.jzh.mvvm.httpUtils.ArticleResponseBody
import com.jzh.mvvm.httpUtils.Banner
import com.jzh.mvvm.httpUtils.HotSearchBean
import com.jzh.mvvm.mvvm.mainRepository.HomeRepository
import com.jzh.mvvm.mvvm.viewModel.CommonViewModel
import com.jzh.mvvm.utils.SingleLiveEvent

class HomeViewModel : CommonViewModel() {

    private val repository = HomeRepository()
    private val bannerDatas = SingleLiveEvent<List<Banner>>()
    private val articlesDatas = SingleLiveEvent<ArticleResponseBody>()
    private val topArticlesDatas = SingleLiveEvent<List<Article>>()
    private val searchData = SingleLiveEvent<ArticleResponseBody>()
    private val hotSearchData = SingleLiveEvent<MutableList<HotSearchBean>>()

    fun getBanner(): LiveData<List<Banner>> {
        launchUI {
            val result = repository.getBanner()
            bannerDatas.value = result.data
        }
        return bannerDatas
    }

    fun getArticles(page: Int): LiveData<ArticleResponseBody> {
        launchUI {
            val result = repository.getArticles(page)
            articlesDatas.value = result.data
        }
        return articlesDatas
    }

    fun getTopArticles(): LiveData<List<Article>> {
        launchUI {
            val result = repository.getTopArticles()
            topArticlesDatas.value = result.data
        }
        return topArticlesDatas
    }

    fun queryBySearchKey(page: Int, key: String): LiveData<ArticleResponseBody> {
        launchUI {
            val result = repository.queryBySearchKey(page, key)
            searchData.value = result.data
        }
        return searchData
    }

    fun getHotSearchData(): LiveData<MutableList<HotSearchBean>> {
        launchUI {
            val result = repository.getHotSearchData()
            hotSearchData.value = result.data
        }
        return hotSearchData
    }
}