package com.example.musicplayer.data.model

import android.database.Cursor
import com.google.gson.Gson


data class Album(
    var id: Long = 0,
    var title: String = "",
    var artist: String = "",
    var artistId: Long = 0,
    var songCount: Int = 0,
    var year: Int = 0
) : MediaItem(id) {

    companion object {
        fun createFromCursor(cursor: Cursor, artistId: Long = 0): Album {
            return Album(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                if (artistId == 0L) cursor.getLong(3) else artistId,
                if (artistId == 0L) cursor.getInt(4) else cursor.getInt(3),
                if (artistId == 0L) cursor.getInt(5) else cursor.getInt(4)
            )
        }
    }

    fun fixArtistLength(width: Int, length: Int, textSize: Int): String {
        return if ((length * textSize) > width) artist.substring(
            0,
            width / textSize - 10
        ) else artist
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }


}