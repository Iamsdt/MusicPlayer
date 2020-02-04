package com.example.musicplayer.ui.albums

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.ui.main.ClickListener
import com.example.musicplayer.ui.songs.SongActivity
import com.example.musicplayer.utils.Constants
import com.iamsdt.androidextension.nextActivity
import kotlinx.android.synthetic.main.activity_artist_list.*
import kotlinx.android.synthetic.main.content_artist_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlbumActivity : AppCompatActivity(), ClickListener<Album> {

    private val vm: AlbumVM by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_list)
        setSupportActionBar(toolbar)

        //layout manager
        val manager = LinearLayoutManager(this)
        artistRCV.layoutManager = manager
        //setup rev
        val adapter = AlbumAdapter(this, this)
        artistRCV.adapter = adapter
        // set item
        val dividerItemDecoration = DividerItemDecoration(
            artistRCV.context,
            manager.orientation
        )
        artistRCV.addItemDecoration(dividerItemDecoration)

        vm.getAlbums().observe(this, Observer {
            adapter.submitList(it)
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun click(model: Album) {
        val map = mapOf(
            Pair(Constants.Type.Type, Constants.Type.TypeAlbums),
            Pair(Constants.Album.AlbumID, model.id),
            Pair(Constants.Album.AlbumName, model.title)
        )

        nextActivity<SongActivity>(
            list = map
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

}