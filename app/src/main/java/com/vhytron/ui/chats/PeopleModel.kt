package com.vhytron.ui.chats

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import java.io.Serializable

//this model contains every other model in it
@Entity(tableName = "people")
data class PeopleModel(
    val image: String = "",
    val name: String = "",
    val title: String = "",
    @PrimaryKey val userName: String = ""):
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
