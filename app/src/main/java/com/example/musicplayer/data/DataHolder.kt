package com.example.musicplayer.data

import com.example.musicplayer.data.model.Song
import com.example.musicplayer.service.IPlayer

class DataHolder {

    var currentTrack: IPlayer.Track? = null
    var currentSong: Song? = null
    var ifFirstTime: Boolean = false
    var newType: String? = null
    var newID: Long? = null
    var newSongID: Long? = null
    var newTitle: String? = null

    var isPlaying: Boolean = false

    companion object {
        var dataHolder: DataHolder? = null

        fun getInstance(): DataHolder {
            if (dataHolder == null) {
                dataHolder = DataHolder()
            }

            return dataHolder!!
        }
    }

}