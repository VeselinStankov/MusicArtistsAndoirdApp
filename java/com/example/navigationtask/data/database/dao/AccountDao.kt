package com.example.navigationtask.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.navigationtask.data.database.model.Account

@Dao
interface AccountDao {

    @Update
    fun update(account: Account)

    @Insert
    fun insert(account: Account)

    @Query("SELECT * FROM user_accounts_table WHERE is_logged = 1 LIMIT 1")
    fun getCurrentAccount(): Account?

    @Query("DELETE FROM user_accounts_table")
    fun clear()

    @Query("DELETE FROM user_accounts_table WHERE email = :email")
    fun deleteByEmail(email: String)

    @Query("SELECT * FROM user_accounts_table WHERE email = :email")
    fun getByEmail(email: String): Account?
}