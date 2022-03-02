package com.vhytron.ui.todos

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vhytron.databinding.TodoItemItemBinding

class TodoRyAdapter: RecyclerView.Adapter<TodoRyAdapter.TodoRYViewHolder>() {
    private val todoList = mutableListOf<TodosData>()

    inner class TodoRYViewHolder(private val binding: TodoItemItemBinding):
            RecyclerView.ViewHolder(binding.root){
                @SuppressLint("SetTextI18n")
                fun bindItem(todo: TodosData){
                    binding.checkbox.isChecked = todo.checked
                    binding.taskName.text = todo.taskName
                    binding.startDate.text = "Start: ${todo.startDate}"
                    binding.dueDate.text = "Due: ${todo.dueDate}"
                }
            }

    fun setUpTodo(todo: List<TodosData>){
        when{
            this.todoList.isEmpty() ->{
                todoList.addAll(todo)
            }
            this.todoList.size < todo.size ->{
                todoList.add(todo.last())
            }
            this.todoList.size > todo.size ->{
                todoList.clear()
                todoList.addAll(todo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoRYViewHolder {
        return TodoRYViewHolder(TodoItemItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: TodoRYViewHolder, position: Int) {
        val todo = todoList[position]
        holder.bindItem(todo)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }
}