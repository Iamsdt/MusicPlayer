package com.example.musicplayer.ui.playlist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.data.DataHolder
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.repo.PlaylistRepository
import com.example.musicplayer.ui.play.PlayActivity
import com.example.musicplayer.ui.songs.SongAdapter
import com.example.musicplayer.utils.Constants
import com.iamsdt.androidextension.MyCoroutineContext
import com.iamsdt.androidextension.nextActivity
import kotlinx.android.synthetic.main.activity_artist_list.*
import kotlinx.android.synthetic.main.content_artist_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListDetails : AppCompatActivity(), LongClickListener<Song> {

    private val vm: PlaylistVM by viewModel()
    private val uiScope = MyCoroutineContext()

    private var title: String = ""
    private var type: String = ""
    private var id: Long = 0L

    private var widget = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_list)

        loadTypeData(intent)

        //set title
        toolbar.title = title
        //set action toolbar
        setSupportActionBar(toolbar)

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

        vm.getSongs(id).observe(this, Observer {
            adapter.submitList(it)
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadTypeData(intent: Intent) {
        type = intent.getStringExtra(Constants.Type.Type) ?: ""
        id = intent.getLongExtra(Constants.Playlist.PlaylistID, 0)
        title = intent.getStringExtra(Constants.Playlist.PlaylistName) ?: ""
        widget = intent.getBooleanExtra("widget", false)
    }


    override fun click(model: Song) {
        val map = mapOf(
            Pair(Constants.Type.Type, type),
            Pair(Constants.Songs.ID, id),
            Pair(Constants.Songs.Name, title),
            Pair("playlist", true),
            Pair(Constants.Songs.SONG_ID, model.id)
        )

        DataHolder.getInstance().newSongID = model.id

        nextActivity<PlayActivity>(list = map)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.songs_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            if (widget) {
                nextActivity<PlaylistActivity>()
            } else
                onBackPressed()
        } else if ((item.itemId == R.id.action_play)) {
            val map = mapOf(
                Pair(Constants.Type.Type, type),
                Pair(Constants.Songs.ID, id),
                Pair(Constants.Songs.Name, title),
                Pair("playlist", true)
            )

            nextActivity<PlayActivity>(list = map)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun longClick(model: Song) {
        showWarning(model)
    }

    private fun showWarning(model: Song) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete?")
        builder.setMessage("Are you want to remove this songs from playlist?")
        builder.setPositiveButton("yes") { _, _ ->
            val repo = PlaylistRepository.getInstance(this)
            uiScope.launch(Dispatchers.IO) {
                repo?.removeFromPlaylist(id, model.id)
                withContext(Dispatchers.Main) {
                    vm.requestUpdate(id)
                }
            }
        }

        builder.setNegativeButton("cancel") { _, _ ->
            //nothing to do
        }

        val dialog = builder.create()
        dialog.show()
    }
}