package com.example.musicplayer.utils

import android.net.Uri
import android.provider.MediaStore

object PlayerConstants {

    const val PLAY_LIST_DETAIL = "play_list_detail"
    const val NOW_PLAYING = "now_playing"
    const val ARTIST_DETAIL = "artist_detail"
    const val ARTIST_KEY = "artist_key"
    const val ALBUM_KEY = "album_key"
    const val FOLDER_KEY = "folder_key"
    const val LIBRARY = "library_fragment"
    const val SONG_DETAIL = "song_detail_fragment"
    const val ALBUM_DETAIL = "album_detail_fragment"
    val ARTWORK_URI: Uri = Uri.parse("content://media/external/audio/albumart")
    val SONG_URI: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    const val LIGHT_THEME = "light_theme"
    const val DARK_THEME = "dark_theme"
    const val SONG_KEY = "song_key"
    const val NO_DATA = "no_data"
}

enum class Repeat {
    ONE,
    ALL,
    LIST,
    OFF
}

enum class Shuffle {
    ON,
    OFF
}
