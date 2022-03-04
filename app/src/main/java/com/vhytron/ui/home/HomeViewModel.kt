package com.vhytron.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _image = MutableLiveData<Int>()
    private val _userName = MutableLiveData<String>()
    private val _name = MutableLiveData<String>()
    private val _title = MutableLiveData<String>()


    val name: LiveData<String> = _name
    val userName: LiveData<String> = _userName
    val title: LiveData<String> = _title
    val image: LiveData<Int> = _image

    fun addDetails(name: String, userName: String, title: String, image: Int){
        _image.value = image
        _name.value = name
        _userName.value = userName
        _title.value = title
    }
}