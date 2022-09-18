package com.example.navigationtask.artistdetails

import androidx.lifecycle.ViewModel
import com.example.navigationtask.common.livedata.LiveEvent
import com.example.navigationtask.common.livedata.MutableLiveEvent

class ArtistDetailsViewModel : ViewModel() {

    private val _shouldNavigateBack = MutableLiveEvent<Boolean>()
    val shouldNavigateBack: LiveEvent<Boolean>
        get() = _shouldNavigateBack

    fun onBackButtonClicked() {
        _shouldNavigateBack.fire(true)
    }
}