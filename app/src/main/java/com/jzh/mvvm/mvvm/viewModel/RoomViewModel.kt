package com.jzh.mvvm.mvvm.viewModel

import androidx.lifecycle.LiveData
import com.jzh.mvvm.db.model.ReadLaterModel
import com.jzh.mvvm.db.model.ReadRecordModel
import com.jzh.mvvm.db.repository.ReadLaterRepository
import com.jzh.mvvm.db.repository.ReadRecordRepository
import com.jzh.mvvm.utils.SingleLiveEvent

class RoomViewModel : CommonViewModel() {

    /**
     * room数据库相关接口
     */
    private val laterRepository = ReadLaterRepository()
    private val recordRepository = ReadRecordRepository()
    private var readLaterData = SingleLiveEvent<List<ReadLaterModel>>()
    private var readRecordData = SingleLiveEvent<List<ReadRecordModel>>()

    fun addLater(link: String, title: String): LiveData<Any> {
        val data = SingleLiveEvent<Any>()
        laterRepository.add(link, title) { data.value = it }
        return data
    }

    fun removeLater(link: String): LiveData<Any> {
        val data = SingleLiveEvent<Any>()
        laterRepository.remove(link) { data.value = it }
        return data
    }

    fun removeAllLater(): LiveData<Any> {
        val data = SingleLiveEvent<Any>()
        laterRepository.removeAll { data.value = it }
        return data
    }

    fun getLaterList(from: Int, count: Int): LiveData<List<ReadLaterModel>> {
        laterRepository.getList(from, count) {
            readLaterData.value = it
        }
        return readLaterData
    }

    fun addRecord(link: String, title: String): LiveData<Any> {
        val data = SingleLiveEvent<Any>()
        recordRepository.add(link, title) { data.value = it }
        return data
    }

    fun removeRecord(link: String): LiveData<Any> {
        val data = SingleLiveEvent<Any>()
        recordRepository.remove(link) { data.value = it }
        return data
    }

    fun removeAllRecord(): LiveData<Any> {
        val data = SingleLiveEvent<Any>()
        recordRepository.removeAll { data.value = it }
        return data
    }

    fun getRecordList(from: Int, count: Int): LiveData<List<ReadRecordModel>> {
        recordRepository.getList(from, count) {
            readRecordData.value = it
        }
        return readRecordData
    }

    fun removeIfMaxCount(): LiveData<Any> {
        val data = SingleLiveEvent<Any>()
        recordRepository.removeIfMaxCount {  data.value = it}
        return data
    }
}