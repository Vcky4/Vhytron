package com.vhytron.ui.todos

import java.io.Serializable

data class TodosData(val taskName: String, val checked: Boolean, val dueDate: String, val startDate: String): Serializable
