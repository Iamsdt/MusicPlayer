package com.example.musicplayer.data.model

import android.database.Cursor
import com.google.gson.Gson

data class Playlist(
    val id: Long,
    val name: String,
    val songCount: Int
) : MediaItem(id) {
    companion object {
        fun fromCursor(cursor: Cursor, songCount: Int): Playlist {
            return Playlist(
                id = cursor.getLong(0),
                name = cursor.getString(1),
                songCount = songCount
            )
        }
    }

    override fun compare(other: MediaItem): Boolean {
        other as Playlist
        return id == other.id && name == other.name && songCount == other.songCount
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }
}