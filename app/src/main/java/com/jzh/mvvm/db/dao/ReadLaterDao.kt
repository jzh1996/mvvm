package com.jzh.mvvm.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jzh.mvvm.db.model.ReadLaterModel

@Dao
interface ReadLaterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(model: ReadLaterModel): Long

    @Query("SELECT * FROM ReadLaterModel ORDER BY time DESC LIMIT (:offset),(:count)")
    suspend fun findAll(offset: Int, count: Int): List<ReadLaterModel>

    @Query("DELETE FROM ReadLaterModel WHERE link = :link")
    suspend fun delete(link: String): Int


    @Query("DELETE FROM ReadLaterModel")
    suspend fun deleteAll(): Int
}