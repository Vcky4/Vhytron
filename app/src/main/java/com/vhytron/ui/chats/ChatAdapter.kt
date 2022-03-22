package com.vhytron.ui.chats

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.core.Repo
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vhytron.R
import com.vhytron.database.AppViewModel
import com.vhytron.database.Repositories
import com.vhytron.databinding.ChatItemBinding
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.sql.Timestamp

class ChatAdapter(private val view: LifecycleOwner) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(), KoinComponent {

    private val auth: FirebaseAuth = Firebase.auth
    private val people: Repositories.PeopleRepository by inject()

    inner class ChatViewHolder(private val binding: ChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(chats: ChatModel) {

            people.getAllPeople.observe(view){ a ->
                a.filter { it.uId == auth.currentUser?.uid }.forEach { person ->
                    Log.d("chat", person.userName )
                    if (person.userName == chats.userName) {
                        binding.card.visibility = GONE
                        binding.cardRight.visibility = VISIBLE
                        binding.chatRight.text = chats.message
                        binding.timeRight.text = Timestamp(chats.time).toString()
                    } else {
                        binding.card.visibility = VISIBLE
                        binding.cardRight.visibility = GONE
                        binding.chat.text = chats.message
                        binding.time.text = Timestamp(chats.time).toString()
                    }

                }
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