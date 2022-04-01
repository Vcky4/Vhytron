package com.vhytron.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vhytron.ui.chats.ChatModel
import com.vhytron.ui.chats.RecentChats

@Dao
interface ChatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatModel)

    @Query("Select * from chats")
    fun getAllChat(): LiveData<List<ChatModel>>

    @Update
    suspend fun updateChat(chat: ChatModel)

    @Delete
    suspend fun deleteChat(chat: ChatModel)
}


@Dao
interface RecentChatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: RecentChats)

    @Query("Select * from recentChats")
    fun getAllChat(): LiveData<List<RecentChats>>

    @Update
    suspend fun updateChat(chat: RecentChats)

    @Delete
    suspend fun deleteChat(chat: RecentChats)

    @Query("Delete from recentChats")
    suspend fun deleteAll()
}