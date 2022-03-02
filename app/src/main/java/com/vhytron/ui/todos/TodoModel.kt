package com.vhytron.ui.todos

import java.io.Serializable

data class TodoModel(val list: MutableList<TodosData>, val day: String): Serializable
