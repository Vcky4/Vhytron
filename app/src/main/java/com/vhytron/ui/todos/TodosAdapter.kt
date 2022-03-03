package com.vhytron.ui.todos

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vhytron.R
import com.vhytron.databinding.TodoItemsBinding

class TodosAdapter(val activity: FragmentActivity?): RecyclerView.Adapter<TodosAdapter.TodoViewHolder>() {
    private val todoList = mutableListOf<TodoModel>()

    inner class TodoViewHolder(private val binding: TodoItemsBinding):
        RecyclerView.ViewHolder(binding.root){
        private val adapter = TodoRyAdapter()
        fun bindItems(todos: TodoModel, position: Int){
            binding.day.text = todos.day
            if (position == 0){
                binding.todoRy.visibility = VISIBLE
                binding.drop.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
            binding.todoRy.adapter = adapter
            binding.todoRy.layoutManager = LinearLayoutManager(activity)
            adapter.setUpTodo(todos.list)
        }
        val card = binding.topLayout
        fun expand(){
            if (binding.todoRy.visibility == GONE){
                binding.todoRy.visibility = VISIBLE
                binding.drop.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }else{
                binding.todoRy.visibility = GONE
                binding.drop.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_right_24)

            }
        }
    }

    fun setUpTodo(todo: List<TodoModel>){
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(TodoItemsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todoList[position]
        holder.bindItems(todo, position)
        holder.card.setOnClickListener {
            holder.expand()
            onItemClickListener?.let { it(todo) }
        }
    }

    private var onItemClickListener: ((TodoModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (TodoModel) -> Unit){
        onItemClickListener = listener
    }
    override fun getItemCount(): Int {
        return todoList.size
    }
}