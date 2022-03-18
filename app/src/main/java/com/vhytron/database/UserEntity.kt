package com.vhytron.database

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val title: String,
    val userName: String,
    var image: String? = null
)
