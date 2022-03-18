package com.vhytron.ui.chats

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vhytron.R
import com.vhytron.databinding.PeopleItemBinding

class PeopleAdapter(private val context: Context): RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {
    private val peopleList = mutableListOf<PeopleModel>()

    inner class PeopleViewHolder(private val binding: PeopleItemBinding):
        RecyclerView.ViewHolder(binding.root){

            fun bindItem(people: PeopleModel){
                binding.name.text = people.name
                if (people.image.isEmpty()){
                    binding.profilePic.setImageResource(R.drawable.profile)
                }else{
                    Glide.with(context).load(people.image.toUri())
                        .into(binding.profilePic)
                }
                binding.title.text = people.title
            }

        val card = binding.card

    }

    fun setUpPeople(people: List<PeopleModel>){
        when {
            this.peopleList.isEmpty() ->{
                this.peopleList.addAll(people)
            }
            peopleList.size < people.size ->{
                this.peopleList.add(people.last())
            }
            peopleList.size > people.size ->{
                this.peopleList.clear()
                this.peopleList.addAll(people)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        return PeopleViewHolder(PeopleItemBinding.inflate(LayoutInflater.from(
            parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val people = peopleList[position]
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
        return peopleList.size
    }
}