package com.vhytron.ui.chats

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vhytron.R
import com.vhytron.databinding.FragmentContactBinding


class ContactFragment : Fragment() {

    private lateinit var binding: FragmentContactBinding
    private val adapter = PeopleAdapter()
    private val database: DatabaseReference = Firebase.database.reference
    private val storageRef = Firebase.storage.reference.child("profileImage")
    private val ref = database.child("users").ref
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment }
        binding = FragmentContactBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.contactRy.layoutManager = LinearLayoutManager(activity)
        binding.contactRy.adapter = adapter

    }

    @SuppressLint("SetTextI18n")
    private fun update(){
        val childEventListener = object : ChildEventListener {
            val list = mutableListOf<ContactModel>()
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
                                                  list.add(ContactModel(image, name, title, userName))
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                                            }
                                        adapter.setUpPeople(list)
                                    }
                                }
                            }
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // handle error
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