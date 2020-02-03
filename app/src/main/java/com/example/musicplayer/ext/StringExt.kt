package com.example.musicplayer.ext

import com.google.gson.Gson
import com.example.musicplayer.data.model.*

fun String?.toSong(): Song {
    return Gson().fromJson(this, Song::class.java)
}

fun String?.toAlbum(): Album {
    return Gson().fromJson(this, Album::class.java)
}

fun String?.toArtist(): Artist {
    return Gson().fromJson(this, Artist::class.java)
}

//fun String?.toPlaylist(): Playlist {
//    return Gson().fromJson(this, Playlist::class.java)
//}
//
//fun String?.toFolder(): Folder {
//    return Gson().fromJson(this, Folder::class.java)
//}