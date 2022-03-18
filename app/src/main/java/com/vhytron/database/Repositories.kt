package com.vhytron.database

import androidx.lifecycle.LiveData
import com.vhytron.ui.chats.ChatModel
import com.vhytron.ui.chats.PeopleModel
import com.vhytron.ui.chats.RecentChats

class Repositories {

    class UserRepository(private val userDao: UserDao){

        //Fetch All the Users
        val getAllUsers: LiveData<List<UserEntity>> = userDao.getAllUser()
        fun getUser(userId: String): LiveData<UserEntity>{
            return userDao.getUser(userId)
        }

        // Insert new user
        suspend fun insertUser(users: UserEntity) {
            userDao.insertUser(users)
        }

        // update user
        suspend fun updateUser(users: UserEntity) {
            userDao.updateUser(users)
        }

        // Delete user
        suspend fun deleteUser(users: UserEntity) {
            userDao.deleteUser(users)
        }
    }

    class ChatRepository(private val chatsDao: ChatsDao){

        //Fetch All the Users
        val getAllChats: LiveData<List<ChatModel>> = chatsDao.getAllChat()

        // Insert new user
        suspend fun insertChat(chat: ChatModel) {
            chatsDao.insertChat(chat)
        }

        // update user
        suspend fun updateChat(chat: ChatModel) {
            chatsDao.updateChat(chat)
        }

        // Delete user
        suspend fun deleteChat(chat: ChatModel) {
            chatsDao.deleteChat(chat)
        }
    }

    class RecentChatRepository(private val recentChatsDao: RecentChatsDao){

        //Fetch All the Users
        val getAllChats: LiveData<List<RecentChats>> = recentChatsDao.getAllChat()

        // Insert new user
        suspend fun insertChat(chat: RecentChats) {
            recentChatsDao.insertChat(chat)
        }

        // update user
        suspend fun updateChat(chat: RecentChats) {
            recentChatsDao.updateChat(chat)
        }

        // Delete user
        suspend fun deleteChat(chat: RecentChats) {
            recentChatsDao.deleteChat(chat)
        }
    }


    class PeopleRepository(private val peopleDao: PeopleDao){

        //Fetch All the people
        val getAllPeople: LiveData<List<PeopleModel>> = peopleDao.getAllPeople()

        // Insert new user
        suspend fun insertPeople(people: PeopleModel) {
            peopleDao.insertPeople(people)
        }

        // update user
        suspend fun updatePeople(people: PeopleModel) {
            peopleDao.updatePeople(people)
        }

        // Delete user
        suspend fun deleteChat(people: PeopleModel) {
            peopleDao.deletePeople(people)
        }
    }
}