package com.example.musicplayer.ui.song

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.repo.ArtistsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SongVM(private val context: Context) : ViewModel() {

    private val artists: MutableLiveData<List<Song>> = MutableLiveData()

    fun getSongs(id: Long): LiveData<List<Song>> {
        // post value
        viewModelScope.launch(Dispatchers.IO) {
            val list = ArtistsRepository.getInstance(context)!!.getSongsForArtist(id)
            artists.postValue(list)
        }

        return artists
    }
}