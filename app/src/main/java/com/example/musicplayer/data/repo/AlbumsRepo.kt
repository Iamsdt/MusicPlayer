package com.example.musicplayer.data.repo

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.ext.toList
import com.example.musicplayer.utils.PlayerConstants
import com.example.musicplayer.utils.SettingsUtility
import com.example.musicplayer.utils.SortModes

interface AlbumsRepositoryInterface {
    fun getAlbum(id: Long): Album
    fun getSongsForAlbum(albumId: Long): List<Song>
    fun getAlbums(): List<Album>
}

class AlbumsRepository() : AlbumsRepositoryInterface {

    private lateinit var contentResolver: ContentResolver
    private lateinit var settingsUtility: SettingsUtility

    companion object {
        private var instance: AlbumsRepository? = null

        fun getInstance(context: Context?): AlbumsRepository? {
            if (instance == null) instance = AlbumsRepository(context)
            return instance
        }
    }

    constructor(context: Context? = null) : this() {
        contentResolver = context!!.contentResolver
        settingsUtility = SettingsUtility.getInstance(context)
    }

    private fun getAlbum(cursor: Cursor?): Album {
        return cursor?.use {
            if (cursor.moveToFirst()) {
                Album.createFromCursor(cursor)
            } else {
                null
            }
        } ?: Album()
    }

    override fun getAlbum(id: Long): Album {
        return getAlbum(makeAlbumCursor("_id=?", arrayOf(id.toString())))
    }

    override fun getSongsForAlbum(albumId: Long): List<Song> {
        val list = makeAlbumSongCursor(albumId)
            .toList(true) { Song.createFromCursor(this, albumId) }
        SortModes.sortAlbumSongList(list)
        return list
    }

    override fun getAlbums(): List<Album> {
        val sl = makeAlbumCursor(null, null)
            .toList(true) { Album.createFromCursor(this) }
        SortModes.sortAlbumList(sl, settingsUtility.albumSortOrder)
        return sl
    }

    fun search(paramString: String, limit: Int): List<Album> {
        val result = makeAlbumCursor("album LIKE ?", arrayOf("$paramString%"))
            .toList(true) { Album.createFromCursor(this) }
        if (result.size < limit) {
            val moreResults = makeAlbumCursor("album LIKE ?", arrayOf("%_$paramString%"))
                .toList(true) { Album.createFromCursor(this) }
            result += moreResults
        }
        return if (result.size < limit) {
            result
        } else {
            result.subList(0, limit)
        }
    }

    private fun makeAlbumCursor(selection: String?, paramArrayOfString: Array<String>?): Cursor? {
        return contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            arrayOf("_id", "album", "artist", "artist_id", "numsongs", "minyear"),
            selection,
            paramArrayOfString,
            settingsUtility.albumSortOrder
        )
    }

    private fun makeAlbumSongCursor(albumID: Long): Cursor? {
        val selection = "is_music=1 AND title != '' AND album_id=$albumID"
        return contentResolver.query(
            PlayerConstants.SONG_URI,
            arrayOf("_id", "title", "artist", "album", "duration", "track", "artist_id", "_data"),
            selection,
            null,
            "track"
        )
    }
}