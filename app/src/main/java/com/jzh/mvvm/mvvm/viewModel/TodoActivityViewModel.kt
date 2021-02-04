package com.jzh.mvvm.mvvm.viewModel

import androidx.lifecycle.LiveData
import com.jzh.mvvm.mvvm.repository.TodoRepository
import com.jzh.mvvm.utils.SingleLiveEvent

class TodoActivityViewModel : CommonViewModel() {

    private val repository = TodoRepository()
    private var updateTodoById = SingleLiveEvent<Any>()
    private var deleteTodoById = SingleLiveEvent<Any>()
    private var addTodo = SingleLiveEvent<Any>()
    private var updateTodo = SingleLiveEvent<Any>()

    fun updateTodoById(id: Int, status: Int): LiveData<Any> {
        launchUI {
            val res = repository.updateTodoById(id, status)
            updateTodoById.value = res.data
        }
        return updateTodoById
    }

    fun deleteTodoById(page: Int): LiveData<Any> {
        launchUI {
            val res = repository.deleteTodoById(page)
            deleteTodoById.value = res.data
        }
        return deleteTodoById
    }

    fun addTodo(map: MutableMap<String, Any>): LiveData<Any> {
        launchUI {
            val res = repository.addTodo(map)
            addTodo.value = res.data
        }
        return addTodo
    }

    fun updateTodo(id: Int, map: MutableMap<String, Any>): LiveData<Any> {
        launchUI {
            val res = repository.updateTodo(id, map)
            updateTodo.value = res.data
        }
        return updateTodo
    }
}