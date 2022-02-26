package com.vhytron.ui.todos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TodosViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is todos Fragment"
    }
    val text: LiveData<String> = _text
}