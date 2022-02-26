package com.vhytron.ui.home

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vhytron.databinding.FragmentHomeBinding
import com.vhytron.databinding.ProfileAlertBinding
import com.vhytron.ui.ViewPagerAdapter
import com.vhytron.ui.chats.ChatsFragment
import com.vhytron.ui.todos.TodosFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val builder = AlertDialog.Builder(context)
        val profileBinding = ProfileAlertBinding.inflate(layoutInflater)
        builder.setView(profileBinding.root)
        val profileAlert = builder.create()
        profileAlert?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.profilePic.setOnClickListener {
            profileAlert.show()
        }


//        this.requireActivity().setActionBar(binding.toolbar)

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(TodosFragment(), "Todos")
        adapter.addFragment(ChatsFragment(), "Chats")
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}