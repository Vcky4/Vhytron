package com.vhytron.ui.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vhytron.R
import com.vhytron.databinding.FragmentContactBinding


class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactBinding
    private lateinit var chatsViewModel: ChatsViewModel
    private val adapter = PeopleAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        chatsViewModel = ViewModelProvider(this)[ChatsViewModel::class.java]
        binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatsViewModel.update()

        chatsViewModel.people.observe(viewLifecycleOwner){
            binding.contactLoading.visibility = VISIBLE
            binding.contactRy.layoutManager = LinearLayoutManager(activity)
            binding.contactRy.adapter = adapter
            adapter.setUpPeople(it)
            binding.contactLoading.visibility = GONE
        }

        adapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("chats", it)
            }
            findNavController().navigate(R.id.action_contact_screen_to_chat_screen, bundle)
        }

    }

}