package com.vhytron.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val UserId: String,
    val image: Bitmap,
    val name: String,
    val title: String,
    val userName: String
)
