package com.vhytron.ui.chats

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vhytron.database.Repositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatsViewModel : ViewModel(), KoinComponent {

    private val auth: FirebaseAuth = Firebase.auth
    private val database: DatabaseReference = Firebase.database.reference
    private val storageRef = Firebase.storage.reference.child("profileImage")
    private val ref = database.child("users").ref
    private val refRecent = database.child("recent").ref
    private val chatRef = database.child("chats").ref
    private val chatRepository: Repositories.ChatRepository by inject()
    private val recentChatRepository: Repositories.RecentChatRepository by inject()
    private val peopleRepository: Repositories.PeopleRepository by inject()
    private val _error = MutableLiveData<String>()

    val recentChats = recentChatRepository.getAllChats

    //get chats
    val readChatData = chatRepository.getAllChats

    val thisUser = peopleRepository.getAllPeople

    val message: LiveData<String> = _error

    init {
        getRecentChats()
    }

    fun getRecentChats() {
        auth.currentUser.let { user ->
            val recentChatsListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (dataValues in snapshot.children.filter { it.key == user?.uid }) {
                        dataValues.children.forEach { each ->
                            ref.get().addOnSuccessListener {
                                it.children.filter { it.child("userName").value == each.child("userName").value.toString() }
                                    .forEach { users ->
                                        storageRef.child(
                                            "${each.child("userName").value.toString()}.jpg"
                                        ).downloadUrl
                                            .addOnSuccessListener { image ->
                                                viewModelScope.launch(Dispatchers.IO) {
                                                    if (image != null) {
                                                        recentChatRepository.insertChat(
                                                            RecentChats(
                                                                users.key.toString(),
                                                                PeopleModel(
                                                                    image.toString(),
                                                                    each.child("name").value.toString(),
                                                                    each.child("title").value.toString(),
                                                                    each.child("userName").value.toString(),
                                                                    users.key.toString(),
                                                                    each.child("time").value.toString()
                                                                        .toLong()
                                                                )
                                                            )
                                                        )


                                                        Log.d(
                                                            "recent",
                                                            recentChatRepository.getAllChats.value.toString()
                                                        )

                                                        Log.d("key", users.key.toString())
                                                    } else {
                                                        recentChatRepository.insertChat(
                                                            RecentChats(
                                                                users.key.toString(),
                                                                PeopleModel(
                                                                    "",
                                                                    each.child("name").value.toString(),
                                                                    each.child("title").value.toString(),
                                                                    each.child("userName").value.toString(),
                                                                    users.key.toString(),
                                                                    each.child("time").value.toString()
                                                                        .toLong()
                                                                )
                                                            )
                                                        )
                                                        Log.d("key", users.key.toString())
                                                    }
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("peopleImage", e.message.toString())
                                            }
                                    }
                            }

                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _error.value = error.message
                    return
                }

            }
//            refRecent.addListenerForSingleValueEvent(recentChatsListener)
            refRecent.addValueEventListener(recentChatsListener)
        }
    }

    fun updateChats(friend: String) {
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataValues in dataSnapshot.children) {
                    auth.currentUser.let { user ->
                        if (user != null) {
                            dataValues.children.filter { dataValues.key == user.uid }
                                .forEach { snapshot ->
                                    snapshot.children.filter { snapshot.key == friend }
                                        .forEach { chats ->
                                            Log.d("chatnow", chats.value.toString())
                                            Log.d("datavalues", dataValues.key.toString())
                                            val message =
                                                chats.child("message").value.toString()
                                            val sender = chats.child("userName")
                                                .value.toString()
                                            val time = chats.child("time")
                                                .value.toString().toLong()
                                            viewModelScope.launch(Dispatchers.IO) {
                                                Log.d("chatId", message.toString())
                                                chatRepository.insertChat(
                                                    ChatModel(
                                                        chats.key.toString(),
                                                        sender,
                                                        message,
                                                        time,
                                                        snapshot.key.toString()
                                                    )
                                                )
                                            }
                                        }

                                }


                        }
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        chatRef.addValueEventListener(menuListener)

    }


    fun createChats(args: ChatsFragmentArgs, message: String, me: PeopleModel) {
        val key = database.child("chats").push().key
        val storageRef = Firebase.storage.reference.child("profileImage")
        val friend = args.chats
        if (key == null) {
            Log.w(ContentValues.TAG, "couldn't get push key for chats")
            return
        }


        auth.currentUser.let {
            storageRef.child("${friend.userName}.jpg").downloadUrl
                .addOnSuccessListener { image ->
                    if (it != null) {


                        val chat =
                            ChatModel(
                                key,
                                me.userName.trim(),
                                message,
                                System.currentTimeMillis()
                            )
                        val recentChats =
                            PeopleModel(
                                image.toString(),
                                friend.name,
                                friend.title,
                                friend.userName,
                                time = System.currentTimeMillis()
                            )

                        val otherUser =
                            PeopleModel(
                                me.image,
                                me.name.trim(),
                                me.title,
                                me.userName,
                                time = System.currentTimeMillis()
                            )

                        val postChat = chat.toMap()
                        val postRecent = recentChats.toMap()
                        val postOther = otherUser.toMap()

                        Log.d("message", "no chat directory")

                        val childUpdates = hashMapOf<String, Any>(
                            "/chats/${it.uid}/${args.chats.userName}/$key" to postChat,
                            "/chats/${friend.uId}/${me.userName}/$key" to postChat
                        )
                        val recentUpdate = hashMapOf<String, Any>(
                            "/recent/${it.uid}/${friend.userName}" to postRecent,
                            "/recent/${friend.uId}/${me.userName}" to postOther
                        )
                        Log.d("friendId", friend.uId)

                        database.updateChildren(childUpdates)
                            .addOnSuccessListener {
                                updateChats(args.chats.userName)

                            }
                        database.updateChildren(recentUpdate)
                            .addOnSuccessListener {
                                getRecentChats()
                            }
                            .addOnFailureListener { exception ->
                                // Write failed
                                _error.postValue(exception.localizedMessage)
                            }

                    }
                }
        }
    }


}