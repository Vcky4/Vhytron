package com.vhytron.ui.chats

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vhytron.R
import com.vhytron.databinding.PeopleItemBinding
import java.sql.Timestamp

class PeopleAdapter(private val context: Context): RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {

    inner class PeopleViewHolder(private val binding: PeopleItemBinding):
        RecyclerView.ViewHolder(binding.root){

            fun bindItem(people: PeopleModel) {
                binding.name.text = people.name
                val hours = Timestamp(people.time).hours
                val minutes = Timestamp(people.time).minutes
                binding.time.text = "${
                    if (hours > 12) {
                        hours.minus(12)
                    } else {
                        hours
                    }
                }${
                    if (minutes != 0) {
                        ":$minutes"
                    } else {
                        ""
                    }
                }${
                    if (hours >= 12) {
                        "pm"
                    } else {
                        "am"
                    }
                }"
                if (people.image.isEmpty()) {
                    binding.profilePic.setImageResource(R.drawable.profile)
                } else {
                    Glide.with(context).load(people.image.toUri())
                        .into(binding.profilePic)
                }
                binding.read.visibility = if (people.isRead) {
                    GONE
                } else {
                    VISIBLE
                }
                binding.title.text = people.title
            }

        val card = binding.card

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        return PeopleViewHolder(PeopleItemBinding.inflate(LayoutInflater.from(
            parent.context), parent, false
        ))
    }

    private val differCallback = object : DiffUtil.ItemCallback<PeopleModel>() {
        override fun areItemsTheSame(oldItem: PeopleModel, newItem: PeopleModel) =
            oldItem.uId == newItem.uId

        override fun areContentsTheSame(oldItem: PeopleModel, newItem: PeopleModel) =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val people = differ.currentList[position]
        holder.bindItem(people)

        holder.card.setOnClickListener {
            onItemClickListener?.let { it(people) }
        }
    }

    private var onItemClickListener: ((PeopleModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (PeopleModel) -> Unit){
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}