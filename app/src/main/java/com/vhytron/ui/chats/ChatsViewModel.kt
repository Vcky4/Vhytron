package com.vhytron.ui.chats

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vhytron.R
import com.vhytron.ui.home.DummyData

class ChatsViewModel : ViewModel() {

    private val database: DatabaseReference = Firebase.database.reference
    private val storageRef = Firebase.storage.reference.child("profileImage")
    private val uRef = database.child("users").ref
    private val cRef = database.child("chats").ref
    private val auth: FirebaseAuth = Firebase.auth

    private val _user = MutableLiveData<String>()
    private val _people = MutableLiveData<MutableList<PeopleModel>>()
    private val _chats = MutableLiveData<MutableList<ChatModel>>()
    private val _recentChats = MutableLiveData<MutableList<PeopleModel>>()
    private val _error = MutableLiveData<String>()


    val error: LiveData<String> = _error
    val people: LiveData<MutableList<PeopleModel>> = _people
    val recentChats: LiveData<MutableList<PeopleModel>> = _recentChats
    val chats: LiveData<MutableList<ChatModel>> = _chats


    @SuppressLint("SetTextI18n")
    fun updatePeople() {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val menuListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val list = mutableListOf<PeopleModel>()
                        for (dataValues in dataSnapshot.children) {
                            auth.currentUser.let {
                                if (it != null && dataValues.key != it.uid) {
                                    val userName =
                                        dataValues.child("userName").value.toString()
                                    val title =
                                        dataValues.child("title").value.toString()
                                    val name = dataValues.child("name").value.toString()


                                    val oneMegaByte: Long = 1024 * 1024
                                    storageRef.child("${userName}.jpg")
                                        .getBytes(oneMegaByte).addOnSuccessListener { bytes ->
                                            if (bytes != null) {
                                                val image = BitmapFactory.decodeByteArray(
                                                    bytes, 0, bytes.size
                                                )
                                                list.add(
                                                    PeopleModel(
                                                        "image", name, title, userName
                                                    )
                                                )
                                                _people.value = list
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
                uRef.addListenerForSingleValueEvent(menuListener)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        }
        uRef.addChildEventListener(childEventListener)
    }

    @SuppressLint("SetTextI18n")
//    fun updateChats(friend: String) {
//        val childEventListener = object : ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                val menuListener = object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        val list = mutableListOf<ChatModel>()
//                        for (dataValues in dataSnapshot.children) {
//                            auth.currentUser.let {
//                                if (it != null) {
//                                    uRef.child(it.uid).child("userName")
//                                        .get().addOnSuccessListener { data ->
//                                            if (dataValues.key.toString()
//                                                    .contains(data.value.toString()) &&
//                                                dataValues.key.toString().contains(friend)
//                                            ) {
//                                                Log.d(TAG, dataValues.key.toString())
//
//                                                dataValues.children.forEach { snapshot ->
//                                                    val message =
//                                                        snapshot.child("message").value.toString()
//                                                    val sender = snapshot.child("userName")
//                                                        .value.toString()
//                                                    val time = snapshot.child("time")
//                                                        .value.toString()
//                                                    list.add(ChatModel(0,sender, message, time))
//                                                }
//                                            }
//                                            _chats.value = list
//                                        }
//                                        .addOnFailureListener { e ->
//                                            _error.value = e.message
//                                        }
//                                }
//                            }
//                        }
//
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {
//                        // handle error
//                    }
//                }
//                cRef.addListenerForSingleValueEvent(menuListener)
//            }
//
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//            }
//
//            override fun onChildRemoved(snapshot: DataSnapshot) {}
//
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
//
//            override fun onCancelled(error: DatabaseError) {}
//
//        }
//
//        cRef.addChildEventListener(childEventListener)
//
//    }




    fun getRecentChats() {
        auth.currentUser.let { user ->
            uRef.get().addOnSuccessListener { currentUser ->
                val me = currentUser.child(user?.uid.toString()).child("userName").value.toString()
                cRef.get().addOnSuccessListener { c ->
                    val recentChats = mutableListOf<PeopleModel>()
                    c.children.forEach { chatsL ->
                        val chat = chatsL.key.toString()
                        val friend = chat.split(" ").filter { it != me }.joinToString("")


                        currentUser.children.forEach { thisUser ->
                            Log.d("chats/friend", thisUser.child("userName").value.toString())
                            if (friend == thisUser.child("userName").value.toString()) {

                                val oneMegaByte: Long = 1024 * 1024
                                storageRef.child("${friend}.jpg")
                                    .getBytes(oneMegaByte)
                                    .addOnSuccessListener { bytes ->
                                        if (bytes != null) {
                                            val image =
                                                BitmapFactory.decodeByteArray(
                                                    bytes, 0, bytes.size
                                                )
                                            recentChats.add(
                                                PeopleModel(
                                                    "image",
                                                    thisUser.child("name").value.toString(),
                                                    thisUser.child("title").value.toString(),
                                                    friend
                                                )
                                            )
                                            _recentChats.value = recentChats
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

}