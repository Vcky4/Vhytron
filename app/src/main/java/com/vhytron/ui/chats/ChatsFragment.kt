package com.vhytron.ui.chats

import android.graphics.Bitmap
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vhytron.R
import com.vhytron.databinding.FragmentChartsBinding

class ChatsFragment : Fragment() {

    private var _binding: FragmentChartsBinding? = null
    private lateinit var  chatsViewModel: ChatsViewModel
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
            ViewModelProvider(this)[ChatsViewModel::class.java]

        _binding = FragmentChartsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PeopleAdapter()
        binding.chartRv.layoutManager = LinearLayoutManager(activity)
        binding.chartRv.adapter = adapter

        chatsViewModel.people.observe(viewLifecycleOwner) {
            adapter.setUpPeople(it)
        }

        adapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("chats", it)
            }
            findNavController().navigate(R.id.action_nav_home_to_chat_screen, bundle)
        }
    }
    private fun getImages(){
        val oneMegaByte: Long = 1024 * 1024
        storageRef.child("${auth.currentUser?.uid}.jpg").getBytes(oneMegaByte)
            .addOnSuccessListener { bytes ->
                if (bytes != null){
                    val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}