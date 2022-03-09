package com.vhytron.ui.home

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class HomeViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    private val database: DatabaseReference = Firebase.database.reference
    private val storageRef = Firebase.storage.reference.child("profileImage")
    private val ref = database.child("users").ref

    private val _image = MutableLiveData<Bitmap>()
    private val _userName = MutableLiveData<String>()
    private val _name = MutableLiveData<String>()
    private val _title = MutableLiveData<String>()
    private val _error = MutableLiveData<String>()


    val name: LiveData<String> = _name
    val userName: LiveData<String> = _userName
    val title: LiveData<String> = _title
    val error: LiveData<String> = _error
    val image: LiveData<Bitmap> = _image



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
                                        val oneMegaByte: Long = 1024 * 1024

                                        storageRef.child("${auth.currentUser?.uid}.jpg").getBytes(oneMegaByte)
                                            .addOnSuccessListener { bytes ->
                                                if (bytes != null){
                                                    val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                                    _image.value = image
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                _error.value = e.message
                                            }
                                        _name.value = name
                                        _userName.value = userName
                                        _title.value = title
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
}