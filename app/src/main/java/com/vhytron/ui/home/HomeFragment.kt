package com.vhytron.ui.home

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vhytron.R
import com.vhytron.databinding.EditProfileAlertBinding
import com.vhytron.databinding.FragmentHomeBinding
import com.vhytron.databinding.ProfileAlertBinding
import com.vhytron.ui.ViewPagerAdapter
import com.vhytron.ui.chats.ChatsFragment
import com.vhytron.ui.todos.TodosFragment

class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var auth: FirebaseAuth


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
        auth = Firebase.auth

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().setActionBar(binding.toolbar)

        //profile alert
        val builder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        val profileBinding = ProfileAlertBinding.inflate(layoutInflater)
        builder.setView(profileBinding.root)
        val profileAlert = builder.create()
        profileAlert?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //edit profile alert
        val editProfileBuilder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        val editProfileBinding = EditProfileAlertBinding.inflate(layoutInflater)
        editProfileBuilder.setView(editProfileBinding.root)
        val editProfileAlert = editProfileBuilder.create()
        editProfileAlert?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //set edit profile button on click listener
        profileBinding.editProfile.setOnClickListener {
            editProfileAlert.show()
        }
        
        //title array
        val titles = arrayOf("Developer", "Secretary", "Marketer", "Designer", "Technician", "Blockchain", "Idea Owner")
        //create spinner adapter
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, titles)
        editProfileBinding.titleSpinner.adapter = spinnerAdapter
        editProfileBinding.titleSpinner.onItemSelectedListener = this
        binding.profilePic.setOnClickListener {
            profileAlert.show()
        }


//        this.requireActivity().setActionBar(binding.toolbar)

        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        adapter.addFragment(TodosFragment(), "Todos")
        adapter.addFragment(ChatsFragment(), "Chats")
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabs, binding.viewPager){ tab, position ->
            tab.text = adapter.getPageTitle(position)
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()

        profileBinding.logOutBt.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_nav_home_to_login)
            profileAlert.dismiss()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {}

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}