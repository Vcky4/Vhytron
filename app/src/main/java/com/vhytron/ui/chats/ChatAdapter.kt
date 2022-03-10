package com.vhytron.ui.chats

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vhytron.R
import com.vhytron.databinding.ChatItemBinding

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private val chatList = mutableListOf<ChatModel>()
    private val database: DatabaseReference = Firebase.database.reference
    private val uRef = database.child("users").ref
    private val auth: FirebaseAuth = Firebase.auth

    inner class ChatViewHolder(private val binding: ChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(chats: ChatModel) {
            binding.chat.text = chats.message
            binding.time.text = chats.time

            auth.currentUser.let {
                it?.uid?.let { it1 ->
                    uRef.child(it1).child("userName")
                        .get().addOnSuccessListener { sender ->
                            if (sender.value.toString() == chats.userName) {
                                binding.card.setBackgroundResource(R.drawable.chat_right)
                                binding.layout.setPadding(100, 10, 10, 10)
                                binding.layout.setHorizontalGravity(Gravity.END)
                            } else {
                                binding.card.setBackgroundResource(R.drawable.chart_left)
                                binding.layout.setPadding(10, 10, 100, 10)
                            }
                        }
                }
            }
        }
    }

    fun setUpChats(chats: List<ChatModel>) {
        when {
            this.chatList.isEmpty() -> {
                chatList.addAll(chats)
            }
            this.chatList.size < chats.size -> {
                chatList.add(chats.last())
            }
            this.chatList.size > chats.size -> {
                chatList.clear()
                chatList.addAll(chats)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            ChatItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.bindItem(chat)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}