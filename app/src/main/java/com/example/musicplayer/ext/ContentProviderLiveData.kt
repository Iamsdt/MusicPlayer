package com.example.musicplayer.ext

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import androidx.lifecycle.MutableLiveData

abstract class ContentProviderLiveData<T>(
    private val context: Context,
    private val uri: Uri
) : MutableLiveData<T>() {
    private lateinit var observer: ContentObserver

    override fun onActive() {
        observer = object : ContentObserver(null) {
            override fun onChange(self: Boolean) {
                // Notify LiveData listeners an event has happened
                postValue(getContentProviderValue())
            }
        }

        context.contentResolver.registerContentObserver(uri, true, observer)
    }

    override fun onInactive() {
        context.contentResolver.unregisterContentObserver(observer)
    }

    /**
     * Implement if you need to provide [T] value to be posted
     * when observed content is changed.
     */
    abstract fun getContentProviderValue(): T
}