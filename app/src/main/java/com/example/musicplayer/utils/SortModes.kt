package com.example.musicplayer.utils

import android.provider.MediaStore
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.model.Song
import java.util.*
import kotlin.Comparator

object SortModes {
    class SongModes {
        companion object {
            const val SONG_DEFAULT = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
            const val SONG_A_Z = MediaStore.Audio.Media.TITLE
            const val SONG_Z_A = "$SONG_A_Z DESC"
            const val SONG_DURATION = MediaStore.Audio.Media.DURATION + " DESC"
            const val SONG_YEAR = MediaStore.Audio.Media.YEAR
            const val SONG_LAST_ADDED = MediaStore.Audio.Media.DATE_MODIFIED + " DESC"
            const val SONG_ALBUM = MediaStore.Audio.Media.ALBUM
            const val SONG_TRACK =
                MediaStore.Audio.Media.TRACK + ", " + MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        }
    }

    class AlbumModes {
        companion object {
            const val ALBUM_DEFAULT = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
            const val ALBUM_A_Z = MediaStore.Audio.Albums.ALBUM
            const val ALBUM_SONGS_LIST = "${MediaStore.Audio.Albums.NUMBER_OF_SONGS} DESC"
            const val ALBUM_Z_A = "$ALBUM_A_Z DESC"
            const val ALBUM_YEAR = "${MediaStore.Audio.Albums.FIRST_YEAR} DESC"
        }
    }

    class ArtistModes {
        companion object {
            const val ARTIST_DEFAULT = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
            const val ARTIST_A_Z = MediaStore.Audio.Artists.ARTIST
            const val ARTIST_Z_A = "$ARTIST_A_Z DESC"
            const val ARTIST_SONGS_LIST = "${MediaStore.Audio.Artists.NUMBER_OF_TRACKS} DESC"
            const val ARTIST_Album_LIST = "${MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS_FOR_ARTIST} DESC"
        }
    }

    fun sortSongList(songList: MutableList<Song>, sortMode: String) {
        when (sortMode) {
            SongModes.SONG_A_Z -> Thread {
                songList.sortWith(Comparator { a, b ->
                    a.title.toUpperCase(Locale.getDefault()).compareTo(b.title.toUpperCase())
                })
            }.start()
            SongModes.SONG_Z_A -> Thread {
                songList.sortWith(Comparator { a, b ->
                    b.title.toUpperCase().compareTo(a.title.toUpperCase())
                })
            }.start()
        }
    }

    fun sortAlbumList(albumList: MutableList<Album>, sortMode: String) {
        when (sortMode) {
            AlbumModes.ALBUM_A_Z -> albumList.sortWith(Comparator { a, b ->
                a.title.toUpperCase().compareTo(b.title.toUpperCase())
            })
            AlbumModes.ALBUM_Z_A -> albumList.sortWith(Comparator { a, b ->
                b.title.toUpperCase().compareTo(a.title.toUpperCase())
            })
        }
    }

    fun sortAlbumSongList(songList: MutableList<Song>) {
        songList.sortWith(Comparator { a, b -> a.trackNumber.compareTo(b.trackNumber) })
    }

    fun sortArtistList(artistList: MutableList<Artist>, sortMode: String) {
        when (sortMode) {
            ArtistModes.ARTIST_A_Z -> artistList.sortBy { it.name.toLowerCase() }
            ArtistModes.ARTIST_Z_A -> artistList.sortByDescending { it.name.toLowerCase() }
        }
    }
}
