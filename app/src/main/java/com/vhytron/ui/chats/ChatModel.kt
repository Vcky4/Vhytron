package com.vhytron.ui.chats

import com.google.firebase.database.Exclude

data class ChatModel(val userName: String, val message: String, val time: String){

    @Exclude
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "userName" to userName,
            "message" to message,
            "time" to time
        )
    }
}
