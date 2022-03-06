package com.vhytron.ui.chats

import android.graphics.drawable.Drawable
import com.google.firebase.database.Exclude
import java.io.Serializable

//this model contains every other model in it
data class PeopleModel(
    val image: Drawable , val name: String, val title: String,
    val userName: String):
    Serializable{

    @Exclude
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "image" to image,
            "name" to name,
            "title" to title,
            "userName" to userName
        )
    }
}
