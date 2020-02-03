package com.example.musicplayer.data

class MainListData {
    companion object {
        fun getMainList(): List<MainListItem> {
            return listOf(
                MainListItem(1, "Artists", "Songs by artists"),
                MainListItem(2, "Albums", "Songs by albums"),
                MainListItem(3, "Playlist", "Songs by playlist")
            )
        }
    }
}

class MainListItem(
    var id: Int, var title: String, var subtitle: String,
    var imageID: String = ""
)