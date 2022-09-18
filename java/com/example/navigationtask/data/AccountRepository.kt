package com.example.navigationtask.data

import com.example.navigationtask.common.Try
import com.example.navigationtask.data.database.model.Account

interface AccountRepository {

    suspend fun registerNewUser(
        email: String,
        password: String,
        fullName: String
    ): Try<Unit>

    suspend fun login(
        email: String,
        password: String
    ): Try<Unit>

    suspend fun logout()

    suspend fun deleteCurrentAccount(): Try<Unit>

    suspend fun isUserLogged(): Boolean

    suspend fun getCurrentUser(): Try<Account>
}