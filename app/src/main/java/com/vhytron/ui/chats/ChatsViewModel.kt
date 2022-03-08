package com.vhytron.ui.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vhytron.R
import com.vhytron.ui.home.DummyData

class ChatsViewModel : ViewModel() {
    private val _user = MutableLiveData<String>()
    private val _people = MutableLiveData<MutableList<PeopleModel>>(
       DummyData.people
    )
    private val _text = MutableLiveData<String>().apply {
        value = "This is chats Fragment"
    }
    val text: LiveData<String> = _text
    val people: LiveData<MutableList<PeopleModel>> = _people
}