package com.example.rmp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    private val _currentUser = MutableLiveData<String>()
    val currentUser: LiveData<String> = _currentUser

    fun setCurrentUser(user: String) {
        _currentUser.value = user
    }
}