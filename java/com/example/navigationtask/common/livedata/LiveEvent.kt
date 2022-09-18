package com.example.navigationtask.common.livedata

import androidx.annotation.MainThread
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

open class LiveEvent<T> : LiveData<Event<T>>() {

    class ConsumingObserver<T>(private val consumer: Consumer<T>) : Observer<Event<T>> {

        override fun onChanged(event: Event<T>?) {
            event?.consume(consumer)
        }
    }

    @MainThread
    fun consume(owner: LifecycleOwner, consumer: Consumer<T>) {
        observe(owner, ConsumingObserver(consumer))
    }
}