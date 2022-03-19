package com.vhytron.database

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vhytron.R
import com.vhytron.ui.chats.ChatModel
import com.vhytron.ui.chats.ContactModel
import com.vhytron.ui.chats.PeopleModel
import com.vhytron.ui.chats.RecentChats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppViewModel: ViewModel(), KoinComponent {

    private val auth: FirebaseAuth = Firebase.auth
    private val database: DatabaseReference = Firebase.database.reference
    private val storageRef = Firebase.storage.reference.child("profileImage")
    private val ref = database.child("users").ref
    private val chatRef = database.child("chats").ref

    private val _chats = MutableLiveData<MutableList<ChatModel>>()

    val recentChats: LiveData<List<RecentChats>>
    val chats: LiveData<MutableList<ChatModel>> = _chats

    //get user data
    val readUserData: UserEntity
    //get chats
    val readChatData: LiveData<List<ChatModel>>
    //get user
    val thisUser: LiveData<UserEntity>
    //get all people
    val allPeople: LiveData<List<PeopleModel>>

    private val _error = MutableLiveData<String>()
    private val _signUpSuccessful = MutableLiveData<Boolean>(false)

    val message: LiveData<String> = _error
    val signUpSuccessful: LiveData<Boolean> = _signUpSuccessful


    //declare user repository
    private val userRepository: Repositories.UserRepository by inject()

    //declare merchant repository
    private val chatRepository: Repositories.ChatRepository by inject()


    private val recentChatRepository: Repositories.RecentChatRepository by inject()


    private val peopleRepository: Repositories.PeopleRepository by inject()


    init {
        //instantiate user
        readUserData = userRepository.getAllUsers
        //instantiate merchant
        readChatData = chatRepository.getAllChats

        thisUser = userRepository.getUser()

        allPeople = peopleRepository.getAllPeople

        recentChats = recentChatRepository.getAllChats

    }

    private fun _addUser(user: UserEntity){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.insertUser(user)
        }
    }

    private fun _updateUser(user: UserEntity){
        viewModelScope.launch {
            userRepository.updateUser(user)
        }
    }


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

                                        _updateUser(UserEntity(it.uid, name, title, userName, image))

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

    fun newLogin(){
        ref.get().addOnSuccessListener {
            it.children.forEach { user ->
                if(user.key == auth.currentUser?.uid){
                    Log.d("user", user.child("userName").value.toString())

                    _addUser(UserEntity(user.key.toString(), user.child("name").toString(),
                        user.child("title").toString(),
                        user.child("userName").value.toString(), user.child("image").value.toString()))

                    Log.d("user", readUserData.toString())
                }
            }
        }

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
                _addUser(UserEntity(userId, name, title, userName,""))
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
                            auth.currentUser.let {
                                if (it != null && dataValues.key != it.uid) {
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
                                                        image.toString(), name, title, userName
                                                    ))
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