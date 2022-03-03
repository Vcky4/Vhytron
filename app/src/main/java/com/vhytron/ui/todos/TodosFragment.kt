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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vhytron.R
import com.vhytron.databinding.AddTodoBinding
import com.vhytron.databinding.FragmentTodosBinding
import com.vhytron.databinding.ProfileAlertBinding
import com.vhytron.ui.home.DummyData

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


        adapter.setUpTodo(DummyData.todos)

        val builder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        val addTodoBinding = AddTodoBinding.inflate(layoutInflater)
        builder.setView(addTodoBinding.root)
        val todoAlert = builder.create()
        todoAlert?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        adapter.setOnItemClickListener {}

        adapter.setOnAddButtonClickListener {
            todoAlert.show()
        }
        adapter.setOnProfileClickListener {
            val bundle = Bundle().apply {
                putSerializable("chats", it.assigner)
            }
            findNavController().navigate(R.id.action_nav_home_to_chat_screen, bundle)        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}