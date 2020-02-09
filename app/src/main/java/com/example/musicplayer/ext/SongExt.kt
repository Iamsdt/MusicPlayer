package com.example.musicplayer.ext

import com.example.musicplayer.data.model.Song
import com.example.musicplayer.service.IPlayer
import com.example.musicplayer.utils.GeneralUtils

fun Int.format(): String {
    return GeneralUtils.formatMilliseconds(this.toLong())
}

fun Int.fix(): Int {
    var value = this
    while (value >= 1000) {
        value -= 1000
    }
    return value
}

fun Song.toTrack(): IPlayer.Track {
    return IPlayer.Track(
        id = this.id.toString(),
        mediaUri = this.path,
        title = this.title,
        artist = this.artist,
        album = this.album,
        imageUri = this.albumId.toUri().toString()
    )
}
