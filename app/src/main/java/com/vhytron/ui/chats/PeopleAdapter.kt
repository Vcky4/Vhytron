package com.vhytron.ui.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vhytron.databinding.PeopleItemBinding

class PeopleAdapter: RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {
    private val peopleList = mutableListOf<PeopleModel>()

    inner class PeopleViewHolder(private val binding: PeopleItemBinding):
        RecyclerView.ViewHolder(binding.root){

            fun bindItem(people: PeopleModel){
                binding.name.text = people.name
                binding.profilePic.setImageBitmap(people.image)
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