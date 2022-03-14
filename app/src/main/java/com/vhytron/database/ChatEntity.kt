package com.vhytron.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val userName: String,
    val message: String,
    val time: String,
    val image: Bitmap,
    val name: String,
    val title: String,

)
