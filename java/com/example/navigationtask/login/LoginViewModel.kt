package com.example.navigationtask.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationtask.common.livedata.LiveEvent
import com.example.navigationtask.common.livedata.MutableLiveEvent
import com.example.navigationtask.data.AccountRepository
import com.example.navigationtask.data.model.LoginError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val accountRepository: AccountRepository) : ViewModel() {

    private var _shouldNavigateToRegisterScreen = MutableLiveEvent<Boolean>()
    val shouldNavigateToRegisterScreen: LiveEvent<Boolean>
        get() = _shouldNavigateToRegisterScreen

    private var _shouldNavigateToFeedScreen = MutableLiveEvent<Boolean>()
    val shouldNavigateToFeedScreen: LiveEvent<Boolean>
        get() = _shouldNavigateToFeedScreen

    private var _emailError = MutableLiveData<Boolean>()
    val emailError: LiveData<Boolean>
        get() = _emailError

    private var _passwordError = MutableLiveData<Boolean>()
    val passwordError: LiveData<Boolean>
        get() = _passwordError

    fun onRegisterClicked() {
        _shouldNavigateToRegisterScreen.fire(true)
    }

    fun onLoginClicked(email: String, password: String) {
        viewModelScope.launch {
            _passwordError.value = false
            _emailError.value = false

            val loginResult = accountRepository.login(email, password)

            loginResult.fold(
                { error ->
                    when (error as LoginError) {
                        LoginError.InvalidPassword -> _passwordError.value = true
                        LoginError.EmailDoesNotExist -> _emailError.value = true
                    }
                },
                { _shouldNavigateToFeedScreen.fire(true) }
            )
        }
    }
}