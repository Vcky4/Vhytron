package com.vhytron.ui.chats

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vhytron.R
import com.vhytron.database.Repositories
import com.vhytron.databinding.ChatItemBinding
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(), KoinComponent {

    val userRepo: Repositories.UserRepository by inject()

    inner class ChatViewHolder(private val binding: ChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(chats: ChatModel) {

            if (userRepo.getAllUsers.userName == chats.userName) {
                binding.card.setBackgroundResource(R.drawable.chat_right)
                binding.layout.setPadding(100, 10, 10, 10)
                binding.layout.setHorizontalGravity(Gravity.END)
            } else {
                binding.card.setBackgroundResource(R.drawable.chart_left)
                binding.layout.setPadding(10, 10, 100, 10)
            }
            binding.chat.text = chats.message
            binding.time.text = chats.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            ChatItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private val differCallback = object : DiffUtil.ItemCallback<ChatModel>() {
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel) = oldItem == newItem
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = differ.currentList[position]
        holder.bindItem(chat)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}