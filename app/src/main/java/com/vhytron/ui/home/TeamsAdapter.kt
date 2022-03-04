package com.vhytron.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vhytron.databinding.TeamsItemBinding

class TeamsAdapter: RecyclerView.Adapter<TeamsAdapter.TeamsViewHolder>() {

    private val teamList = mutableListOf<TeamsData>()

    inner class TeamsViewHolder(private val binding: TeamsItemBinding):
            RecyclerView.ViewHolder(binding.root){
                fun bindItem(team: TeamsData){
                    binding.teamName.text = team.teamName
                    binding.noOfMembers.text = team.members
                }
            }
    fun setUpTeams(team: List<TeamsData>){
        when {
            teamList.isEmpty() -> {
                teamList.addAll(team)
            }
            teamList.size < team.size -> {
                teamList.add(team.last())
            }
            teamList.size > team.size -> {
                teamList.clear()
                teamList.addAll(team)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamsViewHolder {
        return TeamsViewHolder(TeamsItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: TeamsViewHolder, position: Int) {
        val team = teamList[position]
        holder.bindItem(team)
    }

    override fun getItemCount(): Int {
        return teamList.size
    }
}