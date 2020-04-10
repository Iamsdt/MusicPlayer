package com.example.musicplayer.ui.songs

import SongsRepository
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.Playlist
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.repo.AlbumsRepository
import com.example.musicplayer.data.repo.ArtistsRepository
import com.example.musicplayer.data.repo.PlaylistRepository
import com.example.musicplayer.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SongVM(private val context: Context) : ViewModel() {

    private val liveData: MutableLiveData<List<Song>> = MutableLiveData()
    val status = MutableLiveData<Boolean>()

    fun getSongs(id: Long, type: String): LiveData<List<Song>> {
        // post value
        viewModelScope.launch(Dispatchers.IO) {
            when (type) {
                Constants.Type.TypeArtist -> {
                    val list = ArtistsRepository.getInstance(context)?.getSongsForArtist(id)
                    liveData.postValue(list)
                }

                Constants.Type.TypeAlbums -> {
                    val list = AlbumsRepository.getInstance(context)?.getSongsForAlbum(id)
                    liveData.postValue(list)
                }

                Constants.Type.TypePlaylist -> {
                    val list = PlaylistRepository.getInstance(context)?.getSongsInPlaylist(id)
                    liveData.postValue(list)
                }
            }

        }

        return liveData
    }

    fun getSong(songID: Long) = SongsRepository.getInstance(context)?.getSongForId(songID)

    fun requestNewPLayList(id: Long, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (type) {
                Constants.Type.TypeArtist -> {
                    val list = ArtistsRepository.getInstance(context)?.getSongsForArtist(id)
                    liveData.postValue(list)
                }
                Constants.Type.TypeAlbums -> {
                    val list = AlbumsRepository.getInstance(context)?.getSongsForAlbum(id)
                    liveData.postValue(list)
                }
                Constants.Type.TypePlaylist -> {
                    val list = PlaylistRepository.getInstance(context)?.getSongsInPlaylist(id)
                    liveData.postValue(list)
                }
            }
        }
    }

    fun addToPlaylist(play: Playlist, songsID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = PlaylistRepository.getInstance(context)
                ?.addToPlaylist(play.id, listOf(songsID).toLongArray()) ?: 0

            if (id > 0) {
                status.postValue(true)
            }
        }
    }

    fun addToTopPlaylist(play: Playlist, songsID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = PlaylistRepository.getInstance(context)
                ?.addToTopPlaylist(play.id, listOf(songsID).toLongArray()) ?: 0

            if (id > 0) {
                status.postValue(true)
            }
        }
    }
}