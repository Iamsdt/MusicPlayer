package com.example.musicplayer.ui.albums

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.repo.AlbumsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlbumVM(private val context: Context) : ViewModel() {

    private val artists: MutableLiveData<List<Album>> = MutableLiveData()

    init {
        update()
    }

    private fun update() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = AlbumsRepository.getInstance(context)!!.getAlbums()
            artists.postValue(list)
        }
    }

    fun getAlbums(): LiveData<List<Album>> {
        return artists
    }
}