package com.vhytron.ui.chats

import android.graphics.Bitmap
import com.google.firebase.database.Exclude
import java.io.Serializable

class ContactModel(
                   val bitmap: Bitmap, val name: String, val title: String,
                   val userName: String):
    Serializable {

    @Exclude
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "image" to bitmap,
            "name" to name,
            "title" to title,
            "userName" to userName
        )
    }
}