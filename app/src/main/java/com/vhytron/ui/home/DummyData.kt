package com.vhytron.ui.home

import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import com.vhytron.R
import com.vhytron.ui.chats.ChatModel
import com.vhytron.ui.chats.PeopleModel
import com.vhytron.ui.todos.TodoModel
import com.vhytron.ui.todos.TodosData

object DummyData {
    val chats = mutableListOf(
        ChatModel("thanks for your message I really appreciate", "1:00pm"),
        ChatModel("its fine any time", "1:00pm"),
        ChatModel("so what do we do next?", "1:00pm"),
        ChatModel("so what do we do next?", "1:00pm"),
        ChatModel("so what do we do next?", "1:00pm"),
        ChatModel("so what do we do next?", "1:00pm"),
        ChatModel("so what do we do next?", "1:00pm"),
        ChatModel("so what do we do next?", "1:00pm"),
        ChatModel("so what do we do next?", "1:00pm"),
        ChatModel("so what do we do next?", "1:00pm"),
    )

    val people =  mutableListOf(
        PeopleModel(R.drawable.profile.toDrawable(), "Victor", "Mobile developer", "chats"),
        PeopleModel(R.drawable.profile.toDrawable(), "Abasiefon", "Designer", "chats"),
        PeopleModel(R.drawable.profile.toDrawable(), "Ubongabasi Ndak", "Designer", "chats"),
        PeopleModel(R.drawable.profile.toDrawable(), "Uduak Ime", "Secretary", "chats"),
        PeopleModel(R.drawable.profile.toDrawable(), "Salomie", "Marketer", "chats"),
        PeopleModel(R.drawable.profile.toDrawable(), "Victor", "Mobile developer", "chats"),
        PeopleModel(R.drawable.profile.toDrawable(), "Victor", "Mobile developer", "chats"),
        PeopleModel(R.drawable.profile.toDrawable(), "Victor", "Mobile developer", "chats"),
        PeopleModel(R.drawable.profile.toDrawable(), "Victor", "Mobile developer", "chats"),
        PeopleModel(R.drawable.profile.toDrawable(), "Victor", "Mobile developer", "chats"),
        PeopleModel(R.drawable.profile.toDrawable(), "Victor", "Mobile developer", "chats"),
        PeopleModel(R.drawable.profile.toDrawable(), "Victor", "Mobile developer", "chats"),
        PeopleModel(R.drawable.profile.toDrawable(), "Victor", "Mobile developer", "chats"),
        PeopleModel(R.drawable.profile.toDrawable(), "Victor", "Mobile developer", "chats"),
        PeopleModel(R.drawable.profile.toDrawable(), "Victor", "Mobile developer", "chats")
    )

    private val todosData = mutableListOf(
        TodosData("complete this task", true, "4 march", "2 march",PeopleModel(R.drawable.profile.toDrawable(),"Victor", "Mobile developer", "chats")),
        TodosData("complete this task", false, "4 march", "2 march",PeopleModel(R.drawable.profile.toDrawable(), "Victor", "Mobile developer", "chats")),
        TodosData("complete this task", true, "4 march", "2 march",PeopleModel(R.drawable.profile.toDrawable(),"Victor", "Mobile developer", "chats")),
        TodosData("complete this task", true, "4 march", "2 march",PeopleModel(R.drawable.profile.toDrawable(),"Victor", "Mobile developer", "chats")),
        TodosData("complete this task", false, "4 march", "2 march",PeopleModel(R.drawable.profile.toDrawable(), "Victor", "Mobile developer", "chats")),
    )

    val todos = mutableListOf(
        TodoModel(todosData, "Today","Design"),
        TodoModel(todosData, "Overdue","Design"),
        TodoModel(todosData, "Next","Design"),
        TodoModel(todosData, "No due date","Design"),
    )

    val teams =  listOf(
        TeamsData("Designers", "4"),
        TeamsData("Developer", "4"),
        TeamsData("Technician", "4")
    )

    val titles = arrayOf("choose job title", "Developer", "Secretary", "Marketer", "Designer", "Technician", "Blockchain", "Idea Owner")

    val newTeam = arrayOf("Join a Team", "Developer", "Secretary", "Marketer", "Designer", "Technician", "Blockchain", "Idea Owner")



}