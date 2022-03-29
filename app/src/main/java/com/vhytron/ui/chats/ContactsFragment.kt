package com.vhytron.ui.chats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vhytron.R
import com.vhytron.database.AppViewModel
import com.vhytron.databinding.FragmentContactBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactBinding
    private val chatsViewModel: AppViewModel by sharedViewModel()
    private lateinit var adapter: PeopleAdapter
    private val auth: FirebaseAuth = Firebase.auth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentContactBinding.inflate(inflater, container, false)
        adapter = PeopleAdapter(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatsViewModel.updatePeople()


        //set up recycler
        binding.contactRy.layoutManager = LinearLayoutManager(activity)
        binding.contactRy.adapter = adapter
        chatsViewModel.allPeople.observe(viewLifecycleOwner){ people ->
            Log.d("userID", auth.currentUser?.uid.toString())
            adapter.differ.submitList(people.filter { it.uId != auth.currentUser?.uid.toString() }.sortedBy { it.name })
            binding.contactLoading.visibility = GONE
        }

        //display errors
        chatsViewModel.message.observe(viewLifecycleOwner){
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        adapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("chats", it)
            }
            findNavController().navigate(R.id.action_contact_screen_to_chat_screen, bundle)
        }

    }

}