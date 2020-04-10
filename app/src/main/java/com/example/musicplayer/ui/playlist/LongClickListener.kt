package com.example.musicplayer.ui.playlist

interface LongClickListener<T> {
    fun click(model: T)
    fun longClick(model: T)
}

interface PlayListListener<T> {
    fun click(model: T)
    fun delete(model: T)
}