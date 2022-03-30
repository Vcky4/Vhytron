package com.vhytron.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vhytron.ui.chats.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppViewModel : ViewModel(), KoinComponent {

    private val auth: FirebaseAuth = Firebase.auth
    private val database: DatabaseReference = Firebase.database.reference
    private val storageRef = Firebase.storage.reference.child("profileImage")
    private val ref = database.child("users").ref
    private val refRecent = database.child("recent").ref
    private val chatRef = database.child("chats").ref

    private val chatRepository: Repositories.ChatRepository by inject()

    private val recentChatRepository: Repositories.RecentChatRepository by inject()

    private val peopleRepository: Repositories.PeopleRepository by inject()

    private val roomDatabase: AppDatabase by inject()


    val recentChats = recentChatRepository.getAllChats

    fun clear() {
        viewModelScope.launch(Dispatchers.IO) {
            roomDatabase.clearAllTables()
        }
    }

    //get chats
    val readChatData = chatRepository.getAllChats
    //get user

    val thisUser = peopleRepository.getAllPeople

    //get all people
    val allPeople = peopleRepository.getAllPeople

    private val _error = MutableLiveData<String>()
    private val _signUpSuccessful = MutableLiveData(false)

    val message: LiveData<String> = _error
    val signUpSuccessful: LiveData<Boolean> = _signUpSuccessful

//    private fun _addUser(user: CurrentUser){
//        viewModelScope.launch(Dispatchers.IO) {
//            userRepository.insertUser(user)
//            Log.d("entity", user.toString())
//        }
//    }


    @SuppressLint("SetTextI18n")
    fun update() {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val menuListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (dataValues in dataSnapshot.children) {
                            auth.currentUser.let {
                                if (it != null) {
                                    if (dataValues.key.toString() == it.uid) {
                                        val userName = dataValues.child("userName").value.toString()
                                        val title = dataValues.child("title").value.toString()
                                        val name = dataValues.child("name").value.toString()
                                        val image = dataValues.child("image").value.toString()

//                                        _updateUser(CurrentUser(it.uid, name, title, userName, image))

//                                        val oneMegaByte: Long = 1024 * 1024
//
//                                        storageRef.child("${userName}.jpg").getBytes(oneMegaByte)
//                                            .addOnSuccessListener { bytes ->
//                                                if (bytes != null){
//                                                    val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//                                                    _updateUser(UserEntity(it.uid, name, title, userName))
//
//                                                }
//                                            }
//                                            .addOnFailureListener { e ->
//                                                _error.value = e.message
//                                            }
                                    }
                                }
                            }
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // handle error
                        _error.value = databaseError.message
                    }
                }
                ref.addListenerForSingleValueEvent(menuListener)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        }

        ref.addChildEventListener(childEventListener)

    }


    fun addUser(name: String, userName: String, title: String, userId: String) {
        //            _key.ifEmpty {
//            database.child("devotional").push().key
//        }

        val user = ContactModel("", name, title, userName)
        val postValues = user.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/users/$userId" to postValues
        )

        database.updateChildren(childUpdates)
            .addOnSuccessListener {
                // Write was successful!
                viewModelScope.launch(Dispatchers.IO) {
                    updatePeople()
//                    Log.d("entity", user.toString())
                }
                _signUpSuccessful.value = true
            }
            .addOnFailureListener {
                // Write failed
                FirebaseAuth.getInstance().currentUser?.delete()
                _signUpSuccessful.value = false
                _error.value = it.localizedMessage
                // ...
            }

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
            refRecent.addListenerForSingleValueEvent(recentChatsListener)

        }
    }


    fun updatePeople() {
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataValues in dataSnapshot.children) {
                    val userName =
                        dataValues.child("userName").value.toString()
                    val title =
                        dataValues.child("title").value.toString()
                    val name = dataValues.child("name").value.toString()
                    storageRef.child("${userName}.jpg").downloadUrl
                        .addOnSuccessListener { image ->
                            viewModelScope.launch(Dispatchers.IO) {
                                if (image != null) {
                                    peopleRepository.insertPeople(
                                        PeopleModel(
                                            image.toString(), name, title, userName,
                                            uId = dataValues.key.toString()
                                        )
                                    )
                                    Log.d("peopleKey", dataValues.key.toString())
                                } else {
                                    peopleRepository.insertPeople(
                                        PeopleModel(
                                            "", name, title, userName,
                                            uId = dataValues.key.toString()
                                        )
                                    )
                                }

                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("peopleImage", e.message.toString())
                        }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        ref.addListenerForSingleValueEvent(menuListener)
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
        chatRef.addListenerForSingleValueEvent(menuListener)

    }


    fun createChats(args: ChatsFragmentArgs, message: String) {
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

                        database.child("users")
                            .child(it.uid).get().addOnSuccessListener { user ->
                                val chat =
                                    ChatModel(
                                        key,
                                        user.child("userName").value.toString().trim(),
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
                                        image.toString(),
                                        friend.name,
                                        friend.title,
                                        friend.userName,
                                        time = System.currentTimeMillis()
                                    )//complete this

                                val postChat = chat.toMap()
                                val postRecent = recentChats.toMap()

                                //check if directory exist
                                database.get().addOnSuccessListener { data ->
                                    if (!(data.child("chats").exists())) {

                                        Log.d("message", "no chat directory")

                                        val childUpdates = hashMapOf<String, Any>(
                                            "/chats/${it.uid}/${args.chats.userName}/$key" to postChat,
                                            "/chats/${friend.uId}/${user.value.toString()}/$key" to postChat
                                        )
                                        val recentUpdate = hashMapOf<String, Any>(
                                            "/recent/${it.uid}/${friend.userName}" to postRecent,
                                            "/recent/${friend.uId}/${user.value.toString()}" to postRecent
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


                                    } else {
                                        val childUpdates = hashMapOf<String, Any>(
                                            "/chats/${it.uid}/${args.chats.userName}/$key" to postChat,
                                            "/chats/${friend.uId}/${user.value.toString()}/$key" to postChat
                                        )
                                        val recentUpdate = hashMapOf<String, Any>(
                                            "/recent/${it.uid}/${friend.userName}" to postRecent,
                                            "/recent/${friend.uId}/${user.value.toString()}" to postRecent
                                        )
                                        Log.d("friendId", args.chats.toString())
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
/*
                                        // add child to directory
                                        ref.get().addOnSuccessListener { chatData ->
                                            //do this if it's the first chat
                                            chatData.children.forEach { dd ->
                                                if (dd.key.toString()
                                                        .contains(user.value.toString()) &&
                                                    dd.key.toString().contains(args.chats.userName)
                                                ) {
                                                    Log.d("key", dd.key.toString())

                                                    val childUpdates =
                                                        hashMapOf<String, Any>(
                                                            "/chats/${dd.key.toString()}/$key" to postChat
                                                        )
                                                    val recentUpdate = hashMapOf<String, Any>(
                                                        "/recent/${it.uid}/${friend.userName}" to postRecent
                                                    )

                                                    val updateOthers = hashMapOf<String, Any>(
                                                        "/recent/${friend.uId}/${user.value.toString()}" to postRecent
                                                    )

                                                    database.updateChildren(updateOthers)
                                                    database.updateChildren(childUpdates)
                                                        .addOnSuccessListener {
                                                            updateChats(args.chats.userName)
                                                        }
                                                    database.updateChildren(recentUpdate)
                                                        .addOnSuccessListener {
                                                            //todo
                                                        }
                                                        .addOnFailureListener { exception ->
                                                            // Write failed
                                                            _error.postValue(exception.localizedMessage)
                                                        }

                                                }

                                                if (!chatData.child("${user.value.toString()} ${args.chats.userName}")
                                                        .exists() &&
                                                    !chatData.child("${args.chats.userName} ${user.value.toString()}")
                                                        .exists()
                                                ) {
                                                    Log.d(
                                                        "problem",
                                                        chatData.child("${user.value.toString()} ${args.chats.userName}")
                                                            .exists().toString()
                                                    )

                                                    val childUpdates = hashMapOf<String, Any>(
                                                        "/chats/${user.value.toString()} ${args.chats.userName}/$key" to postChat
                                                    )

                                                    val recentUpdate = hashMapOf<String, Any>(
                                                        "/recent/${it.uid}/${friend.userName}" to postRecent
                                                    )

                                                    val updateOthers = hashMapOf<String, Any>(
                                                        "/recent/${friend.uId}/${user.value.toString()}" to postRecent
                                                    )

                                                    database.updateChildren(updateOthers)

                                                    database.updateChildren(childUpdates)
                                                        .addOnSuccessListener {
                                                            updateChats(args.chats.userName)
                                                        }
                                                    database.updateChildren(recentUpdate)
                                                        .addOnSuccessListener {
                                                            //todo
                                                        }
                                                        .addOnFailureListener { exception ->
                                                            // Write failed
                                                            _error.postValue(exception.localizedMessage)
                                                        }
                                                }
                                            }
                                        }
*/

                                    }
                                }

                            }
                    }
                }
        }
    }

}