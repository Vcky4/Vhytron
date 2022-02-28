package com.vhytron.ui.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vhytron.R
import com.vhytron.databinding.FragmentChartsBinding

class ChatsFragment : Fragment() {

    private var _binding: FragmentChartsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val chatsViewModel =
            ViewModelProvider(this)[ChatsViewModel::class.java]

        _binding = FragmentChartsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PeopleAdapter()
        binding.chartRv.layoutManager = LinearLayoutManager(activity)
        binding.chartRv.adapter = adapter
        adapter.setUpPeople(listOf(
            PeopleModel(R.drawable.profile, "Victor", "Mobile developer"),
            PeopleModel(R.drawable.profile, "Abasiefon", "Designer"),
            PeopleModel(R.drawable.profile, "Ubongabasi Ndak", "Designer"),
            PeopleModel(R.drawable.profile, "Uduak Ime", "Secretary"),
            PeopleModel(R.drawable.profile, "Salomie", "Marketer"),
            PeopleModel(R.drawable.profile, "Victor", "Mobile developer"),
            PeopleModel(R.drawable.profile, "Victor", "Mobile developer"),
            PeopleModel(R.drawable.profile, "Victor", "Mobile developer"),
            PeopleModel(R.drawable.profile, "Victor", "Mobile developer"),
            PeopleModel(R.drawable.profile, "Victor", "Mobile developer"),
            PeopleModel(R.drawable.profile, "Victor", "Mobile developer"),
            PeopleModel(R.drawable.profile, "Victor", "Mobile developer"),
            PeopleModel(R.drawable.profile, "Victor", "Mobile developer"),
            PeopleModel(R.drawable.profile, "Victor", "Mobile developer"),
            PeopleModel(R.drawable.profile, "Victor", "Mobile developer")
        ))

        adapter.setOnItemClickListener {
            findNavController().navigate(R.id.action_nav_home_to_chat_screen)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}