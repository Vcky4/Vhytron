package com.vhytron.ui.todos

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vhytron.R
import com.vhytron.databinding.AddTodoBinding
import com.vhytron.databinding.FragmentTodosBinding
import com.vhytron.databinding.ProfileAlertBinding

class TodosFragment : Fragment() {

    private var _binding: FragmentTodosBinding? = null
    private val adapter = TodosAdapter(activity)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val todosViewModel =
            ViewModelProvider(this)[TodosViewModel::class.java]

        _binding = FragmentTodosBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.todoRy.layoutManager = LinearLayoutManager(activity)
        binding.todoRy.adapter = adapter

        val list = mutableListOf(
            TodosData("complete this task", true, "4 march", "2 march","Victor"),
            TodosData("complete this task", false, "4 march", "2 march","Victor"),
            TodosData("complete this task", true, "4 march", "2 march","Victor"),
            TodosData("complete this task", true, "4 march", "2 march","Victor"),
            TodosData("complete this task", false, "4 march", "2 march","Victor"),
        )
        adapter.setUpTodo(
            listOf(
                TodoModel(list, "Today","Design"),
                TodoModel(list, "Overdue","Design"),
                TodoModel(list, "Next","Design"),
                TodoModel(list, "No due date","Design"),
            )
        )

        val builder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        val addTodoBinding = AddTodoBinding.inflate(layoutInflater)
        builder.setView(addTodoBinding.root)
        val todoAlert = builder.create()
        todoAlert?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        adapter.setOnItemClickListener {}

        adapter.setOnAddButtonClickListener {
            todoAlert.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}