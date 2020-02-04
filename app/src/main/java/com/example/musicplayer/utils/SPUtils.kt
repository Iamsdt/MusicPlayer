package com.example.musicplayer.utils

import android.content.Context
import androidx.core.content.edit
import com.example.musicplayer.data.model.Playlist
import org.jetbrains.annotations.NotNull

class SPUtils {
    companion object {

        fun getPLaylistInfo(
            context: @NotNull Context,
            appWidgetId: Int
        ): @NotNull Playlist {
            val sp = getSp(context, "Playlist$appWidgetId")

            return Playlist(
                id = sp.getLong("PlaylistID", 0),
                name = sp.getString("PlaylistName", "") ?: "",
                songCount = sp.getInt("PlaylistSongs", 0)
            )
        }

        fun savePlaylist(playlist: Playlist, context: Context, appWidgetId: Int) {
            getSp(context, "Playlist$appWidgetId").edit {
                putLong("PlaylistID", playlist.id)
                putString("PlaylistName", playlist.name)
                putInt("PlaylistSongs", playlist.songCount)
            }
        }


        private fun getSp(context: Context, name: String) =
            context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }
}