package com.example.musicplayer.ui.artist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.data.repo.ArtistsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArtistVM(private val context: Context) : ViewModel() {

    private val artists: MutableLiveData<List<Artist>> = MutableLiveData()

    init {
        update()
    }

    private fun update() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = ArtistsRepository.getInstance(context)!!.getAllArtist()
            artists.postValue(list)
        }
    }

    fun getArtists(): LiveData<List<Artist>> {
        return artists
    }
}