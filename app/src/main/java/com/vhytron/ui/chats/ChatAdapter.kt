package com.vhytron.ui.chats

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vhytron.R
import com.vhytron.databinding.ChatItemBinding

class ChatAdapter: RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private val chatList = mutableListOf<ChatModel>()

    inner class ChatViewHolder(private  val binding: ChatItemBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bindItem(chats: ChatModel){
            binding.chat.text = chats.message
            binding.time.text = chats.time
        }

        fun setPosition(position: Int){
            if (position % 2 == 0){
                binding.card.setBackgroundResource(R.drawable.chart_left)
                binding.layout.setPadding(10, 10, 100, 10)
                binding.card.setHorizontalGravity(Gravity.START)
            }else{
                binding.card.setBackgroundResource(R.drawable.chat_right)
                binding.layout.setPadding(100, 10, 10, 10)
                binding.card.setHorizontalGravity(Gravity.END)
            }
        }
        }

    fun setUpChats(chats: List<ChatModel>){
        when{
            this.chatList.isEmpty() ->{
                chatList.addAll(chats)
            }
            this.chatList.size < chats.size ->{
                chatList.add(chats.last())
            }
            this.chatList.size > chats.size ->{
                chatList.clear()
                chatList.addAll(chats)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(ChatItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.bindItem(chat)
        holder.position = position
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}