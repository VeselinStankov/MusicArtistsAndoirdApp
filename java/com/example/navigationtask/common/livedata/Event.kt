package com.example.navigationtask.common.livedata

import androidx.core.util.Consumer
import java.util.concurrent.atomic.AtomicBoolean

class Event<T>(val value: T?) {
    private val consumed: AtomicBoolean = AtomicBoolean(false)

    fun consume(consumer: Consumer<T>) {
        if (consumed.compareAndSet(false, true)) {
            consumer.accept(value)
        }
    }
}