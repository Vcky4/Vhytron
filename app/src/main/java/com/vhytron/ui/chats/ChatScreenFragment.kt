package com.vhytron.ui.chats

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vhytron.R
import com.vhytron.databinding.FragmentChatScreenBinding


class ChatScreenFragment : Fragment() {

    private var _binding: FragmentChatScreenBinding? = null
    private val adapter = ChatAdapter()
    private val database: DatabaseReference = Firebase.database.reference
    private val storageRef = Firebase.storage.reference.child("profileImage")
    private val ref = database.child("chats").ref
    private lateinit var auth: FirebaseAuth
    private lateinit var chatsViewModel: ChatsViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        chatsViewModel =
            ViewModelProvider(this)[ChatsViewModel::class.java]
        _binding = FragmentChatScreenBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textTextWatcher()

        val bundle = arguments
        if (bundle == null) {
            Log.e("chats", "chats did not receive people information")
            return
        }
        val args = ChatsFragmentArgs.fromBundle(bundle)

        chatsViewModel.updateChats(args.chats.userName)
        chatsViewModel.chats.observe(viewLifecycleOwner){
            binding.chatRv.layoutManager = LinearLayoutManager(activity)
            binding.chatRv.adapter = adapter
            adapter.setUpChats(it)
        }

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.toolbar.title = args.chats.name
        binding.profilePic.setImageBitmap(args.chats.image)

        binding.sendBt.setOnClickListener {
            if (binding.messageInput.text?.isNotEmpty() == true){
                createChats(args, binding.messageInput.text.toString())
                binding.messageInput.text?.clear()
                chatsViewModel.updateChats(args.chats.userName)
            }
        }
    }

    private fun createChats(args: ChatsFragmentArgs, message: String) {
        val key = database.child("chats").push().key
        if (key == null) {
            Log.w(ContentValues.TAG, "couldn't get push key for chats")
            return
        }
        auth.currentUser.let {
            if (it != null) {
                database.child("users")
                    .child(it.uid).child("userName").get().addOnSuccessListener { user ->
                        val chat = ChatModel(user.value.toString(), message, "2:30pm")

                        val postValues = chat.toMap()

                        val childUpdates = hashMapOf<String, Any>(
                            "/chats/${user.value.toString()}${args.chats.userName}/$key" to postValues
                        )

                        database.updateChildren(childUpdates)
                            .addOnFailureListener { exception ->
                                // Write failed
                                Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG)
                                    .show()
                                // ...
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    }

            }
        }
    }

    private fun textTextWatcher(){
        val watcher: TextWatcher = object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val s0 = binding.messageInput.text.toString()
                binding.sendBt.isEnabled = s0.isNotEmpty()

            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        binding.messageInput.addTextChangedListener(watcher)
    }

}