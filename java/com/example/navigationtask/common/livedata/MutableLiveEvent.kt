package com.example.navigationtask.common.livedata

import androidx.annotation.MainThread

class MutableLiveEvent<T> : LiveEvent<T>() {

    public override fun postValue(value: Event<T>) {
        super.postValue(value)
    }

    @MainThread
    public override fun setValue(value: Event<T>) {
        super.setValue(value)
    }

    @MainThread
    @JvmOverloads
    fun fire(e: T? = null) {
        setValue(Event(e))
    }
}
