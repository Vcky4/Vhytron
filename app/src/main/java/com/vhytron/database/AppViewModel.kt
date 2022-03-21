package com.vhytron.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vhytron.ui.chats.ChatModel
import com.vhytron.ui.chats.ContactModel
import com.vhytron.ui.chats.PeopleModel
import com.vhytron.ui.chats.RecentChats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

class AppViewModel: ViewModel(), KoinComponent {

    private val auth: FirebaseAuth = Firebase.auth
    private val database: DatabaseReference = Firebase.database.reference
    private val storageRef = Firebase.storage.reference.child("profileImage")
    private val ref = database.child("users").ref
    private val chatRef = database.child("chats").ref

    private val chatRepository: Repositories.ChatRepository by inject()

    private val recentChatRepository: Repositories.RecentChatRepository by inject()

    private val peopleRepository: Repositories.PeopleRepository by inject()



    val recentChats = recentChatRepository.getAllChats

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
    fun update(){
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val menuListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (dataValues in dataSnapshot.children) {
                            auth.currentUser.let {
                                if (it != null) {
                                    if (dataValues.key.toString() == it.uid){
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
                ref.addListenerForSingleValueEvent(menuListener)            }

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

        val user = ContactModel("", name,title,userName)
        val postValues = user.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/users/$userId" to postValues
        )

        database.updateChildren(childUpdates)
            .addOnSuccessListener {
                // Write was successful!
                viewModelScope.launch(Dispatchers.IO) {
//                    userRepository.insertUser(CurrentUser(userId, name, title, userName,""))
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
            ref.get().addOnSuccessListener { currentUser ->
                val me = currentUser.child(user?.uid.toString()).child("userName").value.toString()
                chatRef.get().addOnSuccessListener { c ->
                    c.children.forEach { chatsL ->
                        val chat = chatsL.key.toString()
                        val friend = chat.split(" ").filter { it != me }.joinToString("")
                        currentUser.children.forEach { thisUser ->
                            Log.d("chats/friend", thisUser.child("userName").value.toString())
                            if (friend == thisUser.child("userName").value.toString()) {

                                storageRef.child("${friend}.jpg").downloadUrl
                                    .addOnSuccessListener { image ->
                                    if (image !=null){
                                        viewModelScope.launch(Dispatchers.IO) {
                                            recentChatRepository.insertChat(
                                                RecentChats(
                                                    PeopleModel(
                                                        image.toString(),
                                                        thisUser.child("name").value.toString(),
                                                        thisUser.child("title").value.toString(),
                                                        friend
                                                    ))
                                            )
                                        }

                                        Log.d("recent", recentChatRepository.getAllChats.value.toString())
                                    }
                                }
                                    .addOnFailureListener { e ->
                                        _error.value = e.message
                                    }
                            }
                        }


                    }
                }

            }
        }
    }


    fun updatePeople() {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
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
                                            if (image !=null){

                                                viewModelScope.launch(Dispatchers.IO) {
                                                    peopleRepository.insertPeople(PeopleModel(
                                                        image.toString(), name, title, userName,
                                                        dataValues.key.toString()
                                                    ))
                                                }
                                            }
                                            Log.d("thisguy", thisUser.toString())
                                        }
                                        .addOnFailureListener { e ->
                                            _error.value = e.message
                                        }

                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // handle error
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


    fun updateChats(friend: String) {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val menuListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (dataValues in dataSnapshot.children) {
                            auth.currentUser.let {
                                if (it != null) {
                                    ref.child(it.uid).child("userName")
                                        .get().addOnSuccessListener { data ->
                                            if (dataValues.key.toString()
                                                    .contains(data.value.toString()) &&
                                                dataValues.key.toString().contains(friend)
                                            ) {
                                                Log.d(ContentValues.TAG, dataValues.key.toString())

                                                dataValues.children.forEach { snapshot ->
                                                    val message =
                                                        snapshot.child("message").value.toString()
                                                    val sender = snapshot.child("userName")
                                                        .value.toString()
                                                    val time = snapshot.child("time")
                                                        .value.toString()
                                                    viewModelScope.launch(Dispatchers.IO) {
                                                        Log.d("chatId", snapshot.key.toString())
                                                        chatRepository.insertChat(ChatModel(
                                                            snapshot.key.toString(), sender, message, time)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            _error.value = e.message
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

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        }

        chatRef.addChildEventListener(childEventListener)

    }
}