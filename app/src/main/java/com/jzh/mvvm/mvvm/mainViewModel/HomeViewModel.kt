package com.jzh.mvvm.mvvm.mainViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.jzh.mvvm.httpUtils.*
import com.jzh.mvvm.mvvm.mainRepository.HomeRepository
import com.jzh.mvvm.mvvm.viewModel.CommonViewModel
import com.jzh.mvvm.utils.SingleLiveEvent

class HomeViewModel : CommonViewModel() {

    private val repository = HomeRepository()
    private val bannerDatas = SingleLiveEvent<List<Banner>>()
    private val articlesDatas = SingleLiveEvent<List<Article>>()
    private var topArticlesDatas = SingleLiveEvent<List<Article>>()
    private val searchData = SingleLiveEvent<ArticleResponseBody>()
    private val hotSearchData = SingleLiveEvent<MutableList<HotSearchBean>>()

    private var topArticlesList = mutableListOf<Article>()
    private var articlesList = mutableListOf<Article>()

    fun getBanner(): LiveData<List<Banner>> {
        launchUI {
            val result = repository.getBanner()
            bannerDatas.value = result.data
        }
        return bannerDatas
    }

    fun getArticles(page: Int): LiveData<List<Article>> {
        launchUI {
            val result = repository.getArticles(page)
            articlesDatas.value = result.data.datas
        }
        return articlesDatas
    }

    @Deprecated("", replaceWith = ReplaceWith("getArticlesAndTopArticles()"))
    private fun getTopArticles(): LiveData<List<Article>> {
        launchUI {
            val result = repository.getTopArticles()
            result.data.forEach {
                it.top = "1"
            }
            topArticlesDatas.value = result.data
        }
        return topArticlesDatas
    }

    fun getArticlesAndTopArticles(page: Int): LiveData<List<Article>> {
        //置顶数据一般少于20条，所以这个只在第一页加载置顶数据
        return if (page == 0) {
            launchUI {
                topArticlesList = repository.getTopArticles().data
                topArticlesList.forEach {
                    it.top = "1"
                }
            }
            launchUI {
                articlesList = repository.getArticles(0).data.datas
                topArticlesList.addAll(articlesList)
                topArticlesDatas.value = topArticlesList
            }
            topArticlesDatas
        } else {
            getArticles(page)
        }
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