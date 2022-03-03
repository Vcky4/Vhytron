package com.vhytron.ui.home

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
        PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats),
        PeopleModel(R.drawable.profile, "Abasiefon", "Designer", chats),
        PeopleModel(R.drawable.profile, "Ubongabasi Ndak", "Designer", chats),
        PeopleModel(R.drawable.profile, "Uduak Ime", "Secretary", chats),
        PeopleModel(R.drawable.profile, "Salomie", "Marketer", chats),
        PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats),
        PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats),
        PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats),
        PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats),
        PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats),
        PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats),
        PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats),
        PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats),
        PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats),
        PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats)
    )

    private val todosData = mutableListOf(
        TodosData("complete this task", true, "4 march", "2 march", PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats)),
        TodosData("complete this task", false, "4 march", "2 march", PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats)),
        TodosData("complete this task", true, "4 march", "2 march", PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats)),
        TodosData("complete this task", true, "4 march", "2 march", PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats)),
        TodosData("complete this task", false, "4 march", "2 march", PeopleModel(R.drawable.profile, "Victor", "Mobile developer", chats)),
    )

    val todos = mutableListOf(
        TodoModel(todosData, "Today","Design"),
        TodoModel(todosData, "Overdue","Design"),
        TodoModel(todosData, "Next","Design"),
        TodoModel(todosData, "No due date","Design"),
    )


}