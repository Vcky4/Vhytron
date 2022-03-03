package com.vhytron.ui.todos

import com.vhytron.ui.chats.PeopleModel
import java.io.Serializable

data class TodosData(val taskName: String, val checked: Boolean,
                     val dueDate: String, val startDate: String,
                     val assigner: PeopleModel): Serializable
