package com.example.musicplayer.ui.play

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.musicplayer.R
import com.example.musicplayer.data.DataHolder
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.ext.toTrack
import com.example.musicplayer.service.IPlayer
import com.example.musicplayer.service.MusicService
import com.example.musicplayer.service.Player
import com.example.musicplayer.service.Status
import com.example.musicplayer.ui.playlist.PlayListDetails
import com.example.musicplayer.ui.songs.SongVM
import com.example.musicplayer.utils.Constants
import com.example.musicplayer.utils.SecCountManager
import com.iamsdt.androidextension.MyCoroutineContext
import com.iamsdt.androidextension.nextActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.content_play.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList

class PlayActivity : AppCompatActivity() {

    private var title: String = ""
    private var type: String = ""
    private var id: Long = -10L
    private var songID: Long = -10L

    private var playImmediately = false
    private var widget = false

    private val vm: SongVM by viewModel()

    private val list: ArrayList<IPlayer.Track> = ArrayList()
    private var songsList: List<Song>? = ArrayList()

    private val mHandler: Handler = Handler()

    private val uiScope = MyCoroutineContext()

    //logger
    private var startDate: Date? = null
    private var endDate: Date? = null
    private var startTime: Long = 0
    private var finishTime: Long = 0

    private var isPlaying = false
    private var currentSongID = -10

    private var secManager: SecCountManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        setSupportActionBar(toolbar)

        Player.init(this)

        secManager = SecCountManager.getInstance(this)

        loadTypeData(intent)
        setUpActionbar(title)

        vm.getSongs(id, type).observe(this, Observer {
            preparePlayList(it)
        })

        Player.liveDataPlayNow.observe(this, Observer {
            drawUI(it)
        })

        Player.liveDataPlayerState.observe(this, Observer {
            togglePlayPauseIcon(it)
        })

        bindComponents()
        activateSeekabr()

        MusicService.musicPlayerState.observe(this, Observer {
            it?.let {
                fileSaver(it)
            }
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun fileSaver(it: Status) {
        when (it) {
            Status.STATE_READY -> {
                Timber.i("Play State: Player.STATE_Ready")
            }
            Status.STATE_ENDED -> {
                secManager?.endTracking(Player.trackDuration)
                Timber.i("Play State: Player.STATE_ENDED")
            }
            Status.STATE_Tracks_Changed -> {
                if (stateReady) {
                    secManager?.endTracking(Player.currentPosition)
                    Timber.i("Play State: Player.STATE_Tracks_Changed")
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val newType = intent?.getStringExtra(Constants.Type.Type) ?: ""
        val newID = intent?.getLongExtra(Constants.Songs.ID, 0) ?: 0
        val newTitle = intent?.getStringExtra(Constants.Songs.Name) ?: ""
        val newPlayImmediately = intent?.getBooleanExtra("playlist", false) ?: false
        val newSongID = intent?.getLongExtra(Constants.Songs.SONG_ID, -10L) ?: 0
        widget = intent?.getBooleanExtra("widget", false) ?: false

        if (id != newID) {
            id = newID
            vm.requestNewPLayList(id, newType)
        }

        if (newType != type) type = newType

        if (title != newTitle) {
            title = newTitle
            setUpActionbar(title)
        }

        if (songID != newSongID) {
            songID = newSongID
            playImmediately = newPlayImmediately
        }
    }

    private fun togglePlayPauseIcon(it: IPlayer.State?) {
        if (it == null) return

        when (it) {
            IPlayer.State.PLAY -> {
                play_pause.setImageDrawable(getDrawable(R.drawable.uamp_ic_pause_white_48dp))
                isPlaying = true
                Timber.i("Play State: PLAY")
                stateReady = true
            }

            IPlayer.State.PAUSE -> {
                play_pause.setImageDrawable(getDrawable(R.drawable.uamp_ic_play_arrow_white_48dp))
                DataHolder.getInstance().isPlaying = false
                isPlaying = false
                endDate = Date()
                finishTime = Player.currentPosition
                Timber.i("Play State: Pause")
                stateReady = false
            }

            IPlayer.State.ERROR -> {
                play_pause.setImageDrawable(getDrawable(R.drawable.uamp_ic_play_arrow_white_48dp))
                DataHolder.getInstance().isPlaying = false
                isPlaying = false
                endDate = Date()
                finishTime = Player.currentPosition
            }

            IPlayer.State.STOP -> {
                play_pause.setImageDrawable(getDrawable(R.drawable.uamp_ic_play_arrow_white_48dp))
                DataHolder.getInstance().isPlaying = false
                endDate = Date()
                finishTime = Player.currentPosition
                isPlaying = false
                secManager?.endTracking(Player.currentPosition)
            }

            IPlayer.State.PREPARING -> {
                play_pause.setImageDrawable(getDrawable(R.drawable.uamp_ic_play_arrow_white_48dp))
                DataHolder.getInstance().isPlaying = false
            }
            else -> {
                //nothing
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        secManager?.endTracking(Player.currentPosition)
    }


    private fun bindComponents() {
        play_pause.setOnClickListener {
            Player.togglePlayPause()
        }

        next.setOnClickListener {
            Player.next()
            endDate = Date()
            finishTime = Player.currentPosition

            if (isAlreadyPlaying()) {
                secManager?.endTracking(Player.currentPosition)
                stateReady = false
            }
        }

        prev.setOnClickListener {
            Player.prev()
            endDate = Date()
            finishTime = Player.currentPosition
            if (isAlreadyPlaying()) {
                secManager?.endTracking(Player.currentPosition)
                stateReady = false
                //start tracking again
                try {
                    secManager?.startTracking(songTitle, Player.currentPosition)
                } catch (e: Exception) {
                    Timber.i("Play State: Tracking error on $songTitle")
                }
            }
        }
    }

    private fun setUpActionbar(title: String) {
        //set title
        toolbar.title = title
        //set action toolbar
        setSupportActionBar(toolbar)
    }

    private fun drawUI(it: IPlayer.Track?) {
        if (it == null) return

        Picasso.get()
            .load(it.imageUri)
            .placeholder(R.drawable.ic_audio_player)
            .error(R.drawable.ic_audio_player)
            .into(play_bcg)

        play_title.text = it.title
        play_artist.text = it.artist
        play_album.text = it.album

        //set title
        songTitle = it.title ?: ""

        play_endText.text = search(it.id.toLong())
        DataHolder.getInstance().currentTrack = it

        currentSongID = it.id.toInt()

        Timber.i("Play State: UI DRAW")
        try {
            secManager?.startTracking(songTitle, 0)
        } catch (e: Exception) {
            Timber.i("Play State: Tracking error on $songTitle")
        }
    }

    private fun search(id: Long): String {
        val data = songsList?.filter {
            it.id == id
        }

        var status = "0:0"

        if (data?.isEmpty() != true) {
            DataHolder.getInstance().currentSong = data!![0]
            var finished = data[0].duration.toDouble()
            finished /= 1000.0
            finished /= 60.0
            val res = BigDecimal(finished).setScale(2, RoundingMode.HALF_EVEN)
            status = "$res"
        }

        return status
    }

    private fun preparePlayList(it: List<Song>?) {
        //save songs list
        songsList = it

        //clear all previous data
        list.clear()
        it?.forEach { list.add(it.toTrack()) }
        Player.playList = list

        if (playImmediately && list.isNotEmpty() && songID < 0) {
            playSong(list[0].id)
            startDate = Date()
            startTime = 0
        }

        if (songID > 0) {
            if (!isAlreadyPlaying()) {
                playSong(songID.toString())
                startDate = Date()
                startTime = 0
            }
        }
    }

    private fun isAlreadyPlaying(): Boolean {
        var status = false
        val holder = DataHolder.getInstance()
        if (holder.isPlaying && holder.currentTrack?.id == songID.toString()) {
            status = true
        }

        return status
    }

    private fun playSong(playID: String) {
        if (!DataHolder.getInstance().ifFirstTime) {
            uiScope.launch {
                delay(1000)
                Player.start(playID)
                play_endText.text = search(playID.toLong())
            }
            DataHolder.getInstance().ifFirstTime = true
        } else {
            Player.start(playID)
            play_endText.text = search(playID.toLong())
        }
    }

    private fun activateSeekabr() {
        runOnUiThread(object : Runnable {
            override fun run() {
                if (Player.trackDuration != 0L) {
                    val s = Player.currentPosition.toDouble()
                    val f = Player.trackDuration.toDouble()
                    val diff = (s / f) * 100
                    seekBar1.progress = diff.toInt()
                }

                var start = Player.currentPosition.toDouble()
                start /= 1000.0
                start /= 60.0
                val res = BigDecimal(start).setScale(2, RoundingMode.HALF_EVEN)
                play_startText.text = "$res"
                mHandler.postDelayed(this, 1000)
            }
        })
    }

    private fun loadTypeData(intent: Intent) {
        type = intent.getStringExtra(Constants.Type.Type) ?: ""
        id = intent.getLongExtra(Constants.Songs.ID, 0)
        title = intent.getStringExtra(Constants.Songs.Name) ?: ""
        playImmediately = intent.getBooleanExtra("playlist", false)
        songID = intent.getLongExtra(Constants.Songs.SONG_ID, -10L)
        widget = intent.getBooleanExtra("widget", false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (widget) {
                //nextActivity<MainActivity>()
                val map = mapOf(
                    Pair(Constants.Type.Type, type),
                    Pair(Constants.Playlist.PlaylistID, id),
                    Pair(Constants.Playlist.PlaylistName, title),
                    Pair("widget", true)
                )

                if (!isPlaying) {
                    secManager?.endTracking(Player.currentPosition)
                }

                nextActivity<PlayListDetails>(list = map)

            } else {
                if (!isPlaying) {
                    secManager?.endTracking(Player.currentPosition)
                }
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private var songTitle = ""
        var stateReady = false
    }
}
