package com.example.musicplayer.data

import com.example.musicplayer.R

class MainListData {
    companion object {
        fun getMainList(): List<MainListItem> {
            return listOf(
                MainListItem(1, "Artists", "Songs by artists", R.drawable.ic_audio_player),
                MainListItem(2, "Albums", "Songs by albums", R.drawable.ic_audio_player),
                MainListItem(3, "Playlist", "Songs by playlist", R.drawable.ic_audio_player)
            )
        }
    }
}

class MainListItem(
    var id: Int, var title: String, var subtitle: String,
    var imageID: Int = 0
)