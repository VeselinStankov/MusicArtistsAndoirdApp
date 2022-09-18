package com.example.navigationtask.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts_table")
data class Account(
    @PrimaryKey(autoGenerate = true)
    var accountId: Long = 0L,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "password")
    val password: String,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "is_logged")
    val isLogged: Boolean = false
)