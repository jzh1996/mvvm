package com.jzh.mvvm.mvvm.viewModel

import androidx.lifecycle.LiveData
import com.jzh.mvvm.httpUtils.KnowledgeTreeBody
import com.jzh.mvvm.mvvm.mainRepository.SystemRepository
import com.jzh.mvvm.utils.SingleLiveEvent

class KnowledgeViewModel : CommonViewModel() {

    private val repository = SystemRepository()
    private val knowledgeTree = SingleLiveEvent<List<KnowledgeTreeBody>>()

    fun getKnowledgeTree(): LiveData<List<KnowledgeTreeBody>> {
        launchUI {
            val result = repository.getKnowledgeTree()
            knowledgeTree.value = result.data
        }
        return knowledgeTree
    }
}