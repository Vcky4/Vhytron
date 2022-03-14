package com.vhytron.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModel(application: Application): AndroidViewModel(application) {

    //get user data
    val readUserData: LiveData<List<UserEntity>>
    //get chats
    val readChatData: LiveData<List<ChatEntity>>

    //declare user repository
    private val userRepository: Repositories.UserRepository =
        Repositories.UserRepository(AppDatabase.getDataBase(application).userDao())

    //declare merchant repository
    private val chatRepository: Repositories.ChatRepository =
        Repositories.ChatRepository(AppDatabase.getDataBase(application).chatDao())


    init {
        //instantiate user
        readUserData = userRepository.getAllUsers
        //instantiate merchant
        readChatData = chatRepository.getAllChats
    }

    fun addUser(user: UserEntity){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.insertUser(user)
        }
    }


}