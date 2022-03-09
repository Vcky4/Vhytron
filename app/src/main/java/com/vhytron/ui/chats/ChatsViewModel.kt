package com.vhytron.ui.chats

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
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
    private val ref = database.child("users").ref
    private val auth: FirebaseAuth = Firebase.auth



    private val _user = MutableLiveData<String>()
    private val _people = MutableLiveData<MutableList<PeopleModel>>()
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    val people: LiveData<MutableList<PeopleModel>> = _people


    @SuppressLint("SetTextI18n")
    fun update() {
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
                                    storageRef.child("${dataValues.key.toString()}.jpg")
                                        .getBytes(oneMegaByte).addOnSuccessListener { bytes ->
                                            if (bytes != null) {
                                                val image = BitmapFactory.decodeByteArray(
                                                    bytes, 0, bytes.size)
                                                list.add(PeopleModel(
                                                    image, name, title, userName))
                                                _people.value = list
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            _error.value = e.message
                                        }

//                                    list.add(PeopleModel(
//                                        R.drawable.profile.toDrawable().toBitmap(20,20), name, title, userName))
//                                    binding.contactLoading.visibility = GONE

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
}