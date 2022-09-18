package com.example.navigationtask.songdetails

import androidx.lifecycle.ViewModel
import com.example.navigationtask.common.livedata.LiveEvent
import com.example.navigationtask.common.livedata.MutableLiveEvent

class SongDetailsViewModel : ViewModel() {

    private val _shouldNavigateBack = MutableLiveEvent<Boolean>()
    val shouldNavigateBack: LiveEvent<Boolean>
        get() = _shouldNavigateBack

    fun onBackButtonClicked() {
        _shouldNavigateBack.fire(true)
    }
}