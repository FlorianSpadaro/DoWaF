package com.example.dowaf.ui.aliment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlimentViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is aliment Fragment"
    }
    val text: LiveData<String> = _text
}