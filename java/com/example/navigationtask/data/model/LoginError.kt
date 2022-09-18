package com.example.navigationtask.data.model

import com.example.navigationtask.common.Err

sealed class LoginError : Err.Custom() {
    object InvalidPassword : LoginError()
    object EmailDoesNotExist : LoginError()
}