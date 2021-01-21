package com.jzh.mvvm.mvvm.mainViewModel

import androidx.lifecycle.LiveData
import com.jzh.mvvm.httpUtils.ArticleListBean
import com.jzh.mvvm.mvvm.viewModel.CommonViewModel
import com.jzh.mvvm.mvvm.mainRepository.QuestionRepository
import com.jzh.mvvm.utils.SingleLiveEvent

class QuestionViewModel : CommonViewModel() {

    private val repository = QuestionRepository()
    private val questionData = SingleLiveEvent<ArticleListBean>()

    fun getQuestionList(page: Int): LiveData<ArticleListBean> {
        launchUI {
            val result = repository.getQuestionList(page)
            questionData.value = result.data
        }
        return questionData
    }

}