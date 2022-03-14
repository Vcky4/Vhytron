package com.vhytron.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ChatsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChat(chat: ChatEntity)

    @Query("Select * from chats")
    fun getAllChat(): LiveData<List<ChatEntity>>

    @Update
    suspend fun updateChat(chat: ChatEntity)

    @Delete
    suspend fun deleteChat(chat: ChatEntity)
}