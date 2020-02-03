package com.example.musicplayer.ui.song

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.ext.toTrack
import com.example.musicplayer.ui.main.ClickListener
import com.example.player.IPlayer
import com.example.player.Player
import kotlinx.android.synthetic.main.activity_artist_list.*
import kotlinx.android.synthetic.main.content_artist_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SongActivity : AppCompatActivity(), ClickListener<Song> {

    private val vm: SongVM by viewModel()

    private var title: String = ""

    private val list: ArrayList<IPlayer.Track> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_list)

        val albumID = intent.getLongExtra("AlbumId", 0)

        title = intent.getStringExtra("AlbumName") ?: ""
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

        vm.getSongs(albumID).observe(this, Observer {
            adapter.submitList(it)
            preparePlayList(it)
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun preparePlayList(it: List<Song>?) {
        //clear all previous data
        list.clear()
        it?.forEach { list.add(it.toTrack()) }
        Player.playList = list
    }


    override fun click(model: Song) {
        Player.start(model.id.toString())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

}