package com.example.musicplayer.ui.songs

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.ext.toTrack
import com.example.musicplayer.ui.main.ClickListener
import com.example.musicplayer.utils.Constants
import com.example.player.IPlayer
import com.example.player.Player
import kotlinx.android.synthetic.main.activity_artist_list.*
import kotlinx.android.synthetic.main.content_artist_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SongActivity : AppCompatActivity(), ClickListener<Song> {

    private val vm: SongVM by viewModel()

    private var title: String = ""
    private var type: String = ""
    private var playImmediately = false
    private var id: Long = 0L

    private val list: ArrayList<IPlayer.Track> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_list)

        loadTypeData(intent)

        playImmediately = intent.getBooleanExtra("requestForPlay", false)
        //set title
        toolbar.title = title
        //set action toolbar
        setSupportActionBar(toolbar)

        //init player
        Player.init(this)

        //layout manager
        val manager = LinearLayoutManager(this)
        artistRCV.layoutManager = manager
        //setup rev
        val adapter = SongAdapter(this, this)
        artistRCV.adapter = adapter
        // set item
        val dividerItemDecoration = DividerItemDecoration(
            artistRCV.context,
            manager.orientation
        )
        artistRCV.addItemDecoration(dividerItemDecoration)

        vm.getSongs(id, type).observe(this, Observer {
            adapter.submitList(it)
            preparePlayList(it)
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadTypeData(intent: Intent) {
        type = intent.getStringExtra(Constants.Type.Type) ?: ""
        when (type) {
            Constants.Type.TypeAlbums -> {
                id = intent.getLongExtra(Constants.Album.AlbumID, 0)
                title = intent.getStringExtra(Constants.Album.AlbumName) ?: ""
            }

            Constants.Type.TypeArtist -> {
                id = intent.getLongExtra(Constants.Artist.ArtistID, 0)
                title = intent.getStringExtra(Constants.Artist.ArtistName) ?: ""
            }

            Constants.Type.TypePlaylist -> {
                id = intent.getLongExtra(Constants.Playlist.PlaylistID, 0)
                title = intent.getStringExtra(Constants.Playlist.PlaylistName) ?: ""
            }

        }
    }


    private fun preparePlayList(it: List<Song>?) {
        //clear all previous data
        list.clear()
        it?.forEach { list.add(it.toTrack()) }
        Player.playList = list

        if (playImmediately && list.isNotEmpty()) {
            Player.start(list[0].id)
        }
    }


    override fun click(model: Song) {
        Player.start(model.id.toString())
        //Player.play()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.songs_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        } else if ((item.itemId == R.id.action_play)) {
            Player.play()
        }

        return super.onOptionsItemSelected(item)
    }

}