package com.vhytron.ui.chats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vhytron.R
import com.vhytron.databinding.FragmentChartsBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChatsFragment : Fragment() {

    private var _binding: FragmentChartsBinding? = null
    private val chatsViewModel: ChatsViewModel by sharedViewModel()
    private lateinit var auth: FirebaseAuth
    private val database: DatabaseReference = Firebase.database.reference
    private val refRecent = database.child("recent").ref


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartsBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        chatsViewModel.getRecentChats()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PeopleAdapter(requireContext())
        binding.chartRv.layoutManager = LinearLayoutManager(activity)
        binding.chartRv.adapter = adapter

        val recentChatsListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                chatsViewModel.getRecentChats()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        }
        refRecent.addChildEventListener(recentChatsListener)

        chatsViewModel.recentChats.observe(viewLifecycleOwner) { recentChats ->
            chatsViewModel.delete(recentChats)
            val list = mutableListOf<PeopleModel>()
            recentChats.forEach { people ->
                list.add(people.people)
            }
            Log.d("let's see", list.toString())
            adapter.differ.submitList(list.sortedByDescending { it.time.toInt() })

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