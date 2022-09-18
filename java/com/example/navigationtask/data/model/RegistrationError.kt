package com.example.navigationtask.data.model

import com.example.navigationtask.common.Err

sealed class RegistrationError : Err.Custom() {
    object ExistingUser : RegistrationError()
    object PasswordTooShort : RegistrationError()
    object EmptyFullName : RegistrationError()
    object InvalidEmail : RegistrationError()
}