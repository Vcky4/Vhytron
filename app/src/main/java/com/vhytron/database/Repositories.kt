package com.vhytron.database

import androidx.lifecycle.LiveData
import com.vhytron.ui.chats.ChatModel
import com.vhytron.ui.chats.PeopleModel
import com.vhytron.ui.chats.RecentChats
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Repositories {

    class UserRepository: KoinComponent{
        private val db : AppDatabase by inject()
        private val userDao: UserDao = db.userDao()

        val getUser: LiveData<List<User>> = userDao.getAllUser()

        suspend fun insertUser(user: User){
            userDao.insertUser((user))
        }
    }


    class ChatRepository: KoinComponent{

        private val db: AppDatabase by inject()
        private val chatsDao: ChatsDao = db.chatDao()

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


    class RecentChatRepository: KoinComponent{

        private val db: AppDatabase by inject()
        private val recentChatsDao: RecentChatsDao = db.recentChatsDao()

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


    class PeopleRepository: KoinComponent{

        private val db: AppDatabase by inject()
        private val peopleDao: PeopleDao = db.peopleDao()

        //Fetch All the people
        val getAllPeople: LiveData<List<PeopleModel>> = peopleDao.getAllPeople()

        fun thisUser() = peopleDao.thisUser()

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