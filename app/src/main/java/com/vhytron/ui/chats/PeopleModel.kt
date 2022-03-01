package com.vhytron.ui.chats

import java.io.Serializable

data class PeopleModel(val image: Int, val name: String, val title: String): Serializable{


//    fun toMap(): Map<String, Any?>{
//        return mapOf(
//            "image" to image,
//            "name" to name,
//            "title" to title
//        )
//    }
}
