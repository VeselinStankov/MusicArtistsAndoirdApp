package com.example.navigationtask.common.extentions

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
fun EditText.textChanges(): Flow<String> = callbackFlow {
    val listener = addTextChangedListener {
        trySendBlocking(it.toString())
    }
    awaitClose {
        removeTextChangedListener(listener)
    }
}