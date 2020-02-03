package com.example.musicplayer.data.model

open class MediaItem(var _id: Long = 0) {
    open fun compare(other: MediaItem): Boolean {
        return this == other
    }
}