package com.example.navigationtask.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationtask.common.livedata.LiveEvent
import com.example.navigationtask.common.livedata.MutableLiveEvent
import com.example.navigationtask.data.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private var _shouldNavigateToLoginScreen = MutableLiveEvent<Boolean>()
    val shouldNavigateToLoginScreen: LiveEvent<Boolean>
        get() = _shouldNavigateToLoginScreen

    private var _shouldNavigateToFeedScreen = MutableLiveEvent<Boolean>()
    val shouldNavigateToFeedScreen: LiveEvent<Boolean>
        get() = _shouldNavigateToFeedScreen

    fun onScreenReady() {
        viewModelScope.launch {
            val isLoggedIn = accountRepository.isUserLogged()
            if (isLoggedIn) {
                _shouldNavigateToFeedScreen.fire(true)
            } else {
                _shouldNavigateToLoginScreen.fire(true)
            }
        }
    }
}