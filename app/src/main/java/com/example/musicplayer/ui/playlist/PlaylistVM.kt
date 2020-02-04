package com.example.musicplayer.ui.playlist

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.Playlist
import com.example.musicplayer.data.repo.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistVM(val context: Context) : ViewModel() {

    private var data = MutableLiveData<List<Playlist>>()

    init {
        update()
    }

    private fun update() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist = PlaylistRepository.getInstance(context)?.getPlayLists()
            data.postValue(playlist)
        }
    }

    fun getPlaylist() = data

}