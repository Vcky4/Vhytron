package com.vhytron.ui.chats

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vhytron.R
import com.vhytron.databinding.FragmentContactBinding


class ContactsFragment : Fragment() {

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
        update()

        adapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("chats", it)
            }
            findNavController().navigate(R.id.action_contact_screen_to_chat_screen, bundle)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun update() {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val menuListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        binding.contactLoading.visibility = VISIBLE
                        val list = mutableListOf<PeopleModel>()
                        for (dataValues in dataSnapshot.children) {
                            auth.currentUser.let {
                                if (it != null) {
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
                                                binding.contactRy.layoutManager = LinearLayoutManager(activity)
                                                binding.contactRy.adapter = adapter
                                                adapter.setUpPeople(list)
                                                binding.contactLoading.visibility = GONE
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, e.message, Toast.LENGTH_LONG)
                                                .show()
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