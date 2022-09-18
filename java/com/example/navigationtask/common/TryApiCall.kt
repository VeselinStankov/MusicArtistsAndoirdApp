package com.example.navigationtask.common

import java.lang.Exception

interface TryApiCall {
    suspend fun <T> tryApiCall(apiCall: suspend () -> T): Try<T> {
        return try {
            apiCall.invoke().ok()
        } catch (e: Exception) {
            Err.Unknown.err()
        }
    }
}