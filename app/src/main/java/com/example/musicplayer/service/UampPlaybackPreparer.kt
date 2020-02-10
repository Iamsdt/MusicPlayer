package com.example.musicplayer.service

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.example.musicplayer.service.ext.*
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DataSource

/**
 * Class to bridge UAMP to the ExoPlayer MediaSession extension.
 */
class UampPlaybackPreparer(
//        private val musicSource: MusicSource,
    private val exoPlayer: ExoPlayer,
    private val dataSourceFactory: DataSource.Factory
) : MediaSessionConnector.PlaybackPreparer {


    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle) {

    }

    override fun getSupportedPrepareActions(): Long =
        PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID


    override fun onPrepare(playWhenReady: Boolean) {
        //nothing to da
    }

    /**
     * Handles callbacks to both [MediaSessionCompat.Callback.onPrepareFromMediaId]
     * *AND* [MediaSessionCompat.Callback.onPlayFromMediaId] when using [MediaSessionConnector].
     * This is done with the expectation that "play" is just "prepare" + "play".
     *
     * If your app needs to do something special for either 'prepare' or 'play', it's possible
     * to check [ExoPlayer.getPlayWhenReady]. If this returns `true`, then it's
     * [MediaSessionCompat.Callback.onPlayFromMediaId], otherwise it's
     * [MediaSessionCompat.Callback.onPrepareFromMediaId].
     */

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle) {
        val metadataList =
            com.example.musicplayer.service.Player.playList?.map { it.toMediaMetadata() }
                ?: emptyList()
        val itemToPlay: MediaMetadataCompat? = metadataList.find { item ->
            item.id == mediaId
        }
        if (itemToPlay == null) {
            Log.w(TAG, "Content not found: MediaID=$mediaId")
        } else {
            val mediaSource = metadataList.toMediaSource(dataSourceFactory)
            val initialWindowIndex = metadataList.indexOf(itemToPlay)
            val seekTo = if (extras.getBoolean("needSeekTo"))
                com.example.musicplayer.service.Player.currentPosition
            else 0
            exoPlayer.prepare(mediaSource)
            exoPlayer.playWhenReady = playWhenReady
            exoPlayer.seekTo(initialWindowIndex, seekTo)
        }
    }

    private fun setSpeed(speed: Float) {
        exoPlayer.setPlaybackParameters(PlaybackParameters(speed))
    }

    override fun onCommand(
        player: Player,
        controlDispatcher: ControlDispatcher,
        command: String,
        extras: Bundle,
        cb: ResultReceiver
    ): Boolean {
        when (command) {
            COMMAND_SPEED -> setSpeed(extras.getFloat(COMMAND_SPEED, 1F))
        }
        return true
    }


    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle) {

    }

    companion object {
        const val COMMAND_SPEED = "speed"
    }
}

private const val TAG = "MediaSessionHelper"

private fun IPlayer.Track.toMediaMetadata(
): MediaMetadataCompat {
    return MediaMetadataCompat.Builder().also {
        it.id = id
        it.title = title
        it.artist = artist
        it.album = album
//        it.duration = durationMs
//        it.genre = jsonMusic.genre
        it.mediaUri = mediaUri
        it.albumArtUri = imageUri
//        it.trackNumber = trackNumber
//        it.trackCount = totalTrackCount
        it.flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE

        // To make things easier for *displaying* these, set the display properties as well.
        it.displayTitle = title
        it.displaySubtitle = artist
        it.displayDescription = album
        it.displayIconUri = imageUri

        it.albumArt = albumArt

        // Add downloadStatus to force the creation of an "extras" bundle in the resulting
        // MediaMetadataCompat object. This is needed to send accurate metadata to the
        // media session during updates.
        it.downloadStatus = MediaDescriptionCompat.STATUS_NOT_DOWNLOADED
    }.build()
}
