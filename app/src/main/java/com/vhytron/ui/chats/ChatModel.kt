package com.vhytron.ui.chats

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude

@Entity(tableName = "chats")
data class ChatModel(
    @PrimaryKey(autoGenerate = true) var id: Int,
    val userName: String,
    val message: String,
    val time: String){

    @Exclude
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "userName" to userName,
            "message" to message,
            "time" to time
        )
    }
}

@Entity(tableName = "recentChats")
data class RecentChats(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @Embedded(prefix = "people_")
    val people: PeopleModel = PeopleModel()
)
