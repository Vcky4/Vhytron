package com.vhytron.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val message: String,
    @Embedded(prefix = "sender_")
    var sender: UserEntity,
    @Embedded(prefix = "receiver")
    var receiver: UserEntity

)
