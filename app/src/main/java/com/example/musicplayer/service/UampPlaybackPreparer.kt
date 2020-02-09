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

    override fun getSupportedPrepareActions(): Long =
        PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID


    override fun onPrepare() = Unit

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
    override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
        val metadataList =
            com.example.musicplayer.service.Player.playList?.map { it.toMediaMetadata() }
                ?: emptyList()
        val itemToPlay: MediaMetadataCompat? = metadataList.find { item ->
            item.id == mediaId
        }
        if (itemToPlay == null) {
            Log.w(TAG, "Content not found: MediaID=$mediaId")

            // TODO: Notify caller of the error.
        } else {
            val mediaSource = metadataList.toMediaSource(dataSourceFactory)
//            (mediaSource as ConcatenatingMediaSource).
            // Since the playlist was probably based on some ordering (such as tracks
            // on an album), find which window index to play first so that the song the
            // user actually wants to hear plays first.
            val initialWindowIndex = metadataList.indexOf(itemToPlay)
            val seekTo = if (extras?.getBoolean("needSeekTo") == true)
                com.example.musicplayer.service.Player.currentPosition
            else 0
            exoPlayer.prepare(mediaSource)
            exoPlayer.seekTo(initialWindowIndex, seekTo)
        }
    }

    private fun setSpeed(speed: Float) {
        exoPlayer.playbackParameters = PlaybackParameters(speed)
    }

    /**
     * Handles callbacks to both [MediaSessionCompat.Callback.onPrepareFromSearch]
     * *AND* [MediaSessionCompat.Callback.onPlayFromSearch] when using [MediaSessionConnector].
     * (See above for details.)
     *
     * This method is used by the Google Assistant to respond to requests such as:
     * - Play Geisha from Wake Up on UAMP
     * - Play electronic music on UAMP
     * - Play music on UAMP
     *
     * For details on how search is handled, see [AbstractMusicSource.search].
     */
    override fun onPrepareFromSearch(query: String?, extras: Bundle?) {
//        musicSource.whenReady {
//            val metadataList = musicSource.search(query ?: "", extras ?: Bundle.EMPTY)
//            if (metadataList.isNotEmpty()) {
//                val mediaSource = metadataList.toMediaSource(dataSourceFactory)
//                exoPlayer.prepare(mediaSource)
//            }
//        }
    }

    override fun onCommand(
        player: Player?,
        controlDispatcher: ControlDispatcher?,
        command: String?,
        extras: Bundle?,
        cb: ResultReceiver?
    ): Boolean {
        when (command) {
            COMMAND_SPEED -> setSpeed(extras?.getFloat(COMMAND_SPEED, 1F) ?: 1F)
        }

        return true
    }

    override fun onPrepareFromUri(uri: Uri?, extras: Bundle?) = Unit


    //    /**
//     * Builds a playlist based on a [MediaMetadataCompat].
//     *
//     * TODO: Support building a playlist by artist, genre, etc...
//     *
//     * @param item Item to base the playlist on.
//     * @return a [List] of [MediaMetadataCompat] objects representing a playlist.
//     */
//    private fun buildPlaylist(item: MediaMetadataCompat): List<MediaMetadataCompat> =
//            musicSource.filter { it.album == item.album }.sortedBy { it.trackNumber }
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
