package com.mehmet_inci.kotlin_hc05_ws2812.ui.lichteffekte

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LichteffekteViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        // value = "This is lichteffekte Fragment"
    }
    val text: LiveData<String> = _text
}
