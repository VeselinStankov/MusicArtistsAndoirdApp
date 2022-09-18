package com.example.navigationtask.data

import com.example.navigationtask.common.Err
import com.example.navigationtask.common.Try
import com.example.navigationtask.common.err
import com.example.navigationtask.common.ok
import com.example.navigationtask.data.database.model.Account
import com.example.navigationtask.data.database.dao.AccountDao
import com.example.navigationtask.data.model.LoginError
import com.example.navigationtask.data.model.RegistrationError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {

    override suspend fun registerNewUser(
        email: String,
        password: String,
        fullName: String
    ): Try<Unit> = withContext(Dispatchers.IO) {
        return@withContext if (accountDao.getByEmail(email) != null) {
            RegistrationError.ExistingUser.err()
        } else {
            when {
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches() -> RegistrationError.InvalidEmail.err()
                password.length < 8 -> RegistrationError.PasswordTooShort.err()
                fullName.trim().isEmpty() -> RegistrationError.EmptyFullName.err()
                else -> {
                    accountDao.insert(
                        Account(
                            email = email,
                            password = password,
                            fullName = fullName
                        )
                    )
                    Unit.ok()
                }
            }
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Try<Unit> = withContext(Dispatchers.IO) {
        val currentAccount = accountDao.getByEmail(email)
        return@withContext if (currentAccount != null) {
            if (currentAccount.password == password) {
                accountDao.update(currentAccount.copy(isLogged = true))
                Unit.ok()
            } else {
                LoginError.InvalidPassword.err()
            }
        } else {
            LoginError.EmailDoesNotExist.err()
        }
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        val currentAccount = accountDao.getCurrentAccount()
        if (currentAccount != null) {
            accountDao.update(currentAccount.copy(isLogged = false))
        }
    }

    override suspend fun deleteCurrentAccount(): Try<Unit> =
        withContext(Dispatchers.IO) {
            val currentAccount = accountDao.getCurrentAccount()
            return@withContext if (currentAccount != null) {
                accountDao.deleteByEmail(currentAccount.email)
                Unit.ok()
            } else {
                DeleteAccountError.EmailNotFound.err()
            }
        }

    override suspend fun isUserLogged(): Boolean = withContext(Dispatchers.IO) {
        val currentAccount = accountDao.getCurrentAccount()
        return@withContext currentAccount != null
    }

    override suspend fun getCurrentUser(): Try<Account> = withContext(Dispatchers.IO) {
        val currentAccount = accountDao.getCurrentAccount()
        return@withContext currentAccount?.ok() ?: SessionError.SessionExpired.err()
    }
}

sealed class DeleteAccountError : Err.Custom() {
    object EmailNotFound : DeleteAccountError()
}

sealed class SessionError : Err.Custom() {
    object SessionExpired : SessionError()
}