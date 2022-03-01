package com.vhytron.ui.chats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vhytron.databinding.FragmentChatScreenBinding


class ChatScreenFragment : Fragment() {

    private var _binding: FragmentChatScreenBinding? = null
    private val adapter = ChatAdapter()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val chatsViewModel =
            ViewModelProvider(this)[ChatsViewModel::class.java]

        _binding = FragmentChatScreenBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if (bundle == null){
            Log.e("chats", "chats did not receive people information")
            return
        }

        val args = ChatsFragmentArgs.fromBundle(bundle)

        binding.chatRv.layoutManager = LinearLayoutManager(activity)
        binding.chatRv.adapter = adapter
        adapter.setUpChats(
            listOf(
                ChatModel("thanks for your message I really appreciate", "1:00pm"),
                ChatModel("its fine any time", "1:00pm"),
                ChatModel("so what do we do next?", "1:00pm"),
                ChatModel("so what do we do next?", "1:00pm"),
                ChatModel("so what do we do next?", "1:00pm"),
                ChatModel("so what do we do next?", "1:00pm"),
                ChatModel("so what do we do next?", "1:00pm"),
                ChatModel("so what do we do next?", "1:00pm"),
                ChatModel("so what do we do next?", "1:00pm"),
                ChatModel("so what do we do next?", "1:00pm"),
            )
        )
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.toolbar.title = args.chats.name
    }


}