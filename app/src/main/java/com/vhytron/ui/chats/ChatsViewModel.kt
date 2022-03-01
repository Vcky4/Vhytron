package com.vhytron.ui.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vhytron.R

class ChatsViewModel : ViewModel() {
    private val _user = MutableLiveData<String>()
    private val _people = MutableLiveData<MutableList<PeopleModel>>(
        mutableListOf(
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
        )
    )
    private val _text = MutableLiveData<String>().apply {
        value = "This is chats Fragment"
    }
    val text: LiveData<String> = _text
    val people: LiveData<MutableList<PeopleModel>> = _people
}