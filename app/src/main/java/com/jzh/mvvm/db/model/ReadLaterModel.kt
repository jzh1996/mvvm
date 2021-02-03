package com.jzh.mvvm.db.model

import androidx.room.Entity

@Entity(primaryKeys = ["link"])
data class ReadLaterModel(
        val link: String,
        val title: String,
        val time: Long
)