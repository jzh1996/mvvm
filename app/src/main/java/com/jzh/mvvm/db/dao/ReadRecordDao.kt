package com.jzh.mvvm.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jzh.mvvm.db.model.ReadRecordModel

@Dao
interface ReadRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mode: ReadRecordModel): Long

    @Query("DELETE FROM ReadRecordModel WHERE link = :link")
    suspend fun delete(link: String): Int

    @Query("DELETE FROM ReadRecordModel")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM ReadRecordModel ORDER BY time DESC LIMIT (:offset), (:count)")
    suspend fun findAll(offset: Int, count: Int): List<ReadRecordModel>

    @Query(
        """DELETE FROM ReadRecordModel WHERE link NOT IN 
        (SELECT link FROM ReadRecordModel ORDER BY time DESC LIMIT 0, :maxCount)"""
    )
    suspend fun deleteIfMaxCount(maxCount: Int): Int
}