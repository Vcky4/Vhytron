package com.vhytron.database

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
import com.vhytron.ui.chats.ContactModel
import com.vhytron.ui.chats.PeopleModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppViewModel : ViewModel(), KoinComponent {

    private val auth: FirebaseAuth = Firebase.auth
    private val database: DatabaseReference = Firebase.database.reference
    private val storageRef = Firebase.storage.reference.child("profileImage")
    private val ref = database.child("users").ref


    private val chatRepository: Repositories.ChatRepository by inject()

    private val recentChatRepository: Repositories.RecentChatRepository by inject()

    private val peopleRepository: Repositories.PeopleRepository by inject()

    private val roomDatabase: AppDatabase by inject()


    val recentChats = recentChatRepository.getAllChats

    fun clear() {
        viewModelScope.launch(Dispatchers.IO) {
            roomDatabase.clearAllTables()
            delay(900)
        }
    }


    val thisUser = peopleRepository.getAllPeople

    //get all people
    val allPeople = peopleRepository.getAllPeople

    private val _error = MutableLiveData<String>()
    private val _signUpSuccessful = MutableLiveData(false)

    val message: LiveData<String> = _error
    val signUpSuccessful: LiveData<Boolean> = _signUpSuccessful




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



}