package com.example.musicplayer.data.repo

import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import com.example.musicplayer.data.model.Playlist
import com.example.musicplayer.ext.ContentProviderLiveData
import com.example.musicplayer.ext.toList

class ContentLiveData(
    uri: Uri,
    val context: Context
) : ContentProviderLiveData<List<Playlist>>(context, uri) {

    override fun getContentProviderValue(): List<Playlist> {

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
            arrayOf(BaseColumns._ID, MediaStore.Audio.PlaylistsColumns.NAME),
            null,
            null,
            MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER
        )

        val list = cursor.toList {
            val id: Long = getLong(0)
            val songCount = getSongCountForPlaylist(id)
            Playlist.fromCursor(this, songCount)
        }.filter { it.name.isNotEmpty() }

        cursor?.close()

        return list
    }

    private fun getSongCountForPlaylist(playlistId: Long): Int {
        try {
            val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
            return context.contentResolver.query(
                uri,
                arrayOf(BaseColumns._ID),
                "${MediaStore.Audio.AudioColumns.TITLE} != ''",
                null,
                null
            )?.use {
                if (it.moveToFirst()) {
                    it.count
                } else {
                    0
                }
            } ?: 0
        } catch (ex: SecurityException) {
            Log.println(Log.ERROR, "Exception", ex.message!!)
        }
        return -1
    }
}