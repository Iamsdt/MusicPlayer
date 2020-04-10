package com.example.musicplayer.ui.playlist

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.Playlist
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.repo.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistVM(val context: Context) : ViewModel() {

    private val playlist = MutableLiveData<List<Playlist>>()
    private val songs = MutableLiveData<List<Song>>()
    val isUpdated = MutableLiveData<Boolean>()

    init {
        update()
    }

    private fun update() {
        viewModelScope.launch(Dispatchers.IO) {
            val play = PlaylistRepository.getInstance(context)?.getPlayLists()
            playlist.postValue(play)
        }
    }

    fun getPlaylist() = playlist

    fun requestUpdate(id: Long) {
        isUpdated.postValue(true)
        val list = PlaylistRepository.getInstance(context)?.getSongsInPlaylist(id)
        songs.postValue(list)
    }

    fun getSongs(id: Long): MutableLiveData<List<Song>> {
        val list = PlaylistRepository.getInstance(context)?.getSongsInPlaylist(id)
        songs.postValue(list)
        return songs
    }

    fun reorder(id: Long, fromPos: Int, toPos: Int) {
        val status = PlaylistRepository.getInstance(context)?.reorderMedia(id, fromPos, toPos)
        println(status)
    }

}