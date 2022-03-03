package com.vhytron.ui.chats

import com.vhytron.ui.todos.TodoModel
import java.io.Serializable

//this model contains every other model in it
data class PeopleModel(val image: Int, val name: String, val title: String,
                       val chats: MutableList<ChatModel>):
    Serializable{


//    fun toMap(): Map<String, Any?>{
//        return mapOf(
//            "image" to image,
//            "name" to name,
//            "title" to title
//        )
//    }
}
