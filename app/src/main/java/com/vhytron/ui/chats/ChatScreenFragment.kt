package com.vhytron.ui.chats

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vhytron.databinding.FragmentChatScreenBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChatScreenFragment : Fragment() {

    private var _binding: FragmentChatScreenBinding? = null
    private lateinit var adapter: ChatAdapter
    private val database: DatabaseReference = Firebase.database.reference
    private val chatRef = database.child("chats").ref
    private lateinit var auth: FirebaseAuth
    private val chatsViewModel: ChatsViewModel by viewModel()
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

        val recentChatsListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                chatsViewModel.updateChats(args.chats.userName)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        }
        chatRef.addChildEventListener(recentChatsListener)

        linearLayoutManager.stackFromEnd = true

        binding.chatRv.layoutManager = linearLayoutManager
        binding.chatRv.adapter = adapter
        chatsViewModel.readChatData.observe(viewLifecycleOwner) { chatList ->
            adapter.differ.submitList(
                chatList.filter {
                    it.parties == args.chats.userName
                }.sortedBy { it.time })
        }

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.toolbar.title = args.chats.name
        Glide.with(requireContext()).load(args.chats.image.toUri())
            .into(binding.profilePic)

        binding.sendBt.setOnClickListener {
            chatsViewModel.thisUser.observe(viewLifecycleOwner) { peopleList ->
                peopleList.filter { it.uId == auth.currentUser?.uid }.forEach { user ->
                    if (binding.messageInput.text?.isNotEmpty() == true) {
                        chatsViewModel.createChats(args, binding.messageInput.text.toString(), user)
                        binding.messageInput.text?.clear()
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