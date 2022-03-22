package com.vhytron.ui.chats

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vhytron.database.AppDatabase
import com.vhytron.database.AppViewModel
import com.vhytron.databinding.FragmentChatScreenBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChatScreenFragment : Fragment() {

    private var _binding: FragmentChatScreenBinding? = null
    private lateinit var adapter: ChatAdapter
    private val database: DatabaseReference = Firebase.database.reference
    private val ref = database.child("chats").ref
    private lateinit var auth: FirebaseAuth
    private val chatsViewModel: AppViewModel by viewModel()
    private val linearLayoutManager = LinearLayoutManager(activity)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatScreenBinding.inflate(inflater, container, false)
        auth = Firebase.auth


        adapter = ChatAdapter(viewLifecycleOwner)

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

        linearLayoutManager.stackFromEnd = true

        chatsViewModel.updateChats(args.chats.userName)

        binding.chatRv.layoutManager = linearLayoutManager
        binding.chatRv.adapter = adapter
        chatsViewModel.readChatData.observe(viewLifecycleOwner) {
            adapter.differ.submitList(it)
        }

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.toolbar.title = args.chats.name
        Glide.with(requireContext()).load(args.chats.image.toUri())
            .into(binding.profilePic)

        binding.sendBt.setOnClickListener {
            if (binding.messageInput.text?.isNotEmpty() == true) {
                createChats(args, binding.messageInput.text.toString())
                binding.messageInput.text?.clear()
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
                        database.get().addOnSuccessListener { data ->
                            if (!(data.child("chats").exists())) {
                                Log.d("message", "got here right1")
                                val chat =
                                    ChatModel(key,user.value.toString().trim(), message, System.currentTimeMillis())
                                Log.d("message", "got here right")
                                val postValues = chat.toMap()

                                val childUpdates = hashMapOf<String, Any>(
                                    "/chats/${user.value.toString()} ${args.chats.userName}/$key" to postValues
                                )

                                database.updateChildren(childUpdates)
                                    .addOnSuccessListener {
                                        chatsViewModel.updateChats(args.chats.userName)
                                        chatsViewModel.readChatData.observe(viewLifecycleOwner) { chatList ->
//                                                linearLayoutManager.reverseLayout = true
                                            linearLayoutManager.stackFromEnd = true
                                            adapter.differ.submitList(chatList)
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        // Write failed
                                        Toast.makeText(
                                            context,
                                            exception.localizedMessage,
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                    }

                            } else {
                                ref.get().addOnSuccessListener { chatData ->
                                    //do this if it's the first chat
                                    chatData.children.forEach { dd ->
                                        if (dd.key.toString().contains(user.value.toString()) &&
                                            dd.key.toString().contains(args.chats.userName)
                                        ) {
                                            Log.d("key", dd.key.toString())

                                            val chat =
                                                ChatModel(key,user.value.toString(), message, System.currentTimeMillis())

                                            val postValues = chat.toMap()

                                            val childUpdates = hashMapOf<String, Any>(
                                                "/chats/${dd.key.toString()}/$key" to postValues
                                            )

                                            database.updateChildren(childUpdates)
                                                .addOnSuccessListener {
                                                    chatsViewModel.updateChats(args.chats.userName)
                                                    chatsViewModel.readChatData.observe(viewLifecycleOwner) { chatList ->
//                                                        linearLayoutManager.reverseLayout = true
                                                        linearLayoutManager.stackFromEnd = true
                                                        adapter.differ.submitList(chatList)
                                                    }
                                                }
                                                .addOnFailureListener { exception ->
                                                    // Write failed
                                                    Toast.makeText(
                                                        context,
                                                        exception.localizedMessage,
                                                        Toast.LENGTH_LONG
                                                    )
                                                        .show()
                                                }
                                        }
                                        if (!chatData.child("${user.value.toString()} ${args.chats.userName}").exists() &&
                                            !chatData.child("${args.chats.userName} ${user.value.toString()}").exists()){
                                                    Log.d("problem", chatData.child("${user.value.toString()} ${args.chats.userName}").exists().toString() )
                                            val chat =
                                                ChatModel(
                                                    key,user.value.toString(),
                                                    message,
                                                    System.currentTimeMillis()
                                                )
                                            Log.d("message", "got here right")
                                            val postValues = chat.toMap()

                                            val childUpdates = hashMapOf<String, Any>(
                                                "/chats/${user.value.toString()} ${args.chats.userName}/$key" to postValues
                                            )

                                            database.updateChildren(childUpdates)
                                                .addOnSuccessListener {
                                                    chatsViewModel.updateChats(args.chats.userName)
                                                    chatsViewModel.readChatData.observe(viewLifecycleOwner) { chatList ->
//                                                        linearLayoutManager.reverseLayout = true
                                                        linearLayoutManager.stackFromEnd = true
                                                        adapter.differ.submitList(chatList)
                                                    }
                                                }
                                                .addOnFailureListener { exception ->
                                                    // Write failed
                                                    Toast.makeText(
                                                        context,
                                                        exception.localizedMessage,
                                                        Toast.LENGTH_LONG
                                                    )
                                                        .show()
                                                }
                                        }
                                    }
                                }

                            }
                        }

                    }
            }
        }
    }

    private fun textTextWatcher() {
        val watcher: TextWatcher = object : TextWatcher {
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