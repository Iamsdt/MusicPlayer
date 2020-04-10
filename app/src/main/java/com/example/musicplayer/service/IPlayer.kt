package com.example.musicplayer.service

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import java.util.*

interface IPlayer {
    enum class State { PLAY, PAUSE, STOP, PREPARING, ERROR, SKIP_TO_NEXT, SKIP_TO_PREVIOUS, NONE, DISTORTED, CREATED, CHANGED}

    val liveDataPlayerState: LiveData<State>
    val liveDataPlayNow: LiveData<Track>
    val liveDataPlayList: LiveData<List<Track>>
    val trackDuration: Long
    val currentPosition: Long
    var speed: Float
    var playList: List<Track>?
    fun play()
    fun start(mediaId: String)
    fun pause()
    fun stop()
    fun next()
    fun prev()
    fun togglePlayPause()
    fun seekTo(millis: Long)
    fun setRepeatMode(mode: Int)

    data class Track @JvmOverloads constructor(
        val id: String,
        val mediaUri: String,
        val title: String? = null,
        val artist: String? = null,
        val album: String? = null,
        val imageUri: String? = null,
        val albumArt: Bitmap? = null
    ) {
        override fun hashCode(): Int {
            return Objects.hash(
                id, mediaUri, title, artist, album, imageUri/*,albumArt?.generationId*/
            )
        }

        override fun equals(other: Any?): Boolean {
            return other is Track
                    && id == other.id
                    && mediaUri == other.mediaUri
                    && title == other.title
                    && artist == other.artist
                    && album == other.album
                    && imageUri == other.imageUri
                    && (albumArt === other.albumArt || albumArt?.sameAs(other.albumArt) == true)
        }
    }
}