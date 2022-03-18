package com.vhytron.ui.chats

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vhytron.R
import com.vhytron.database.AppViewModel
import com.vhytron.databinding.FragmentChartsBinding

class ChatsFragment : Fragment() {

    private var _binding: FragmentChartsBinding? = null
    private lateinit var  chatsViewModel: AppViewModel
    private val storageRef = Firebase.storage.reference.child("profileImage")
    private lateinit var auth: FirebaseAuth
    private val database: DatabaseReference = Firebase.database.reference


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatsViewModel =
            ViewModelProvider(this)[AppViewModel::class.java]

        _binding = FragmentChartsBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PeopleAdapter(requireContext())
        chatsViewModel.getRecentChats()
        chatsViewModel.recentChats.observe(viewLifecycleOwner) {
            binding.chartRv.layoutManager = LinearLayoutManager(activity)
            binding.chartRv.adapter = adapter
            val list = mutableListOf<PeopleModel>()
            it.forEach {people ->

                list.add(people.people)
                adapter.setUpPeople(list)
            }

        }
        chatsViewModel.message.observe(viewLifecycleOwner){
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        adapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("chats", it)
            }
            findNavController().navigate(R.id.action_nav_home_to_chat_screen, bundle)
        }

        binding.newChatBt.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_contact_screen)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}