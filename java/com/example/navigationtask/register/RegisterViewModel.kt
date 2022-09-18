package com.example.navigationtask.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationtask.common.livedata.LiveEvent
import com.example.navigationtask.common.livedata.MutableLiveEvent
import com.example.navigationtask.data.AccountRepository
import com.example.navigationtask.data.model.RegistrationError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private var _shouldNavigateToLoginScreen = MutableLiveEvent<Boolean>()
    val shouldNavigateToLoginScreen: LiveEvent<Boolean>
        get() = _shouldNavigateToLoginScreen

    private var _emailError = MutableLiveData<Boolean>()
    val emailError: LiveData<Boolean>
        get() = _emailError

    private var _passwordError = MutableLiveData<Boolean>()
    val passwordError: LiveData<Boolean>
        get() = _passwordError

    private var _nameError = MutableLiveData<Boolean>()
    val nameError: LiveData<Boolean>
        get() = _nameError

    private var _existingUserError = MutableLiveData<Boolean>()
    val existingUserError: LiveData<Boolean>
        get() = _existingUserError

    fun onRegisterClicked(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _existingUserError.value = false
            _emailError.value = false
            _passwordError.value = false
            _nameError.value = false

            val registrationResult = accountRepository.registerNewUser(
                email,
                password,
                fullName
            )

            registrationResult.fold(
                { error ->
                    when (error as RegistrationError) {
                        RegistrationError.ExistingUser -> _existingUserError.value = true
                        RegistrationError.InvalidEmail -> _emailError.value = true
                        RegistrationError.PasswordTooShort -> _passwordError.value = true
                        RegistrationError.EmptyFullName -> _nameError.value = true
                    }
                },
                { _shouldNavigateToLoginScreen.fire(true) }
            )
        }
    }
}