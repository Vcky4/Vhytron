package com.vhytron.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userss")
data class User(
    @PrimaryKey val uId: String,
    val name: String,
    val title: String,
    val userName: String,
    val image: String = ""
)
