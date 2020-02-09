package com.example.musicplayer.ui.songs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
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
import com.example.musicplayer.data.repo.PlaylistRepository.Companion.getInstance
import com.example.musicplayer.ui.play.PlayActivity
import com.example.musicplayer.ui.playlist.LongClickListener
import com.example.musicplayer.utils.Constants
import com.iamsdt.androidextension.MyCoroutineContext
import com.iamsdt.androidextension.nextActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_artist_list.*
import kotlinx.android.synthetic.main.content_artist_list.*
import kotlinx.android.synthetic.main.playlist_dialogs.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SongActivity : AppCompatActivity(), LongClickListener<Song> {

    private val vm: SongVM by viewModel()
    private val uiScope = MyCoroutineContext()

    private var title: String = ""
    private var type: String = ""
    private var id: Long = 0L

    private var savedString = ""

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

        vm.getSongs(id, type).observe(this, Observer {
            adapter.submitList(it)
        })

        vm.status.observe(this, Observer {
            it?.let {
                Toasty.success(this, "Successfully added to playlist").show()
            }
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


    override fun click(model: Song) {
        val map = mapOf(
            Pair(Constants.Type.Type, type),
            Pair(Constants.Songs.ID, id),
            Pair(Constants.Songs.Name, title),
            Pair("requestForPlay", true),
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
        showPlaylistDialog(model)
    }

    private fun showPlaylistDialog(model: Song) {

        val repo = getInstance(this)!!

        val mList = repo.getPlayLists()

        val list = ArrayList<String>()
        list.add("Create new list")

        for ((_, name) in mList) {
            list.add(name)
        }

        val cs = list.toTypedArray<CharSequence>()

        val builder =
            AlertDialog.Builder(this)
        builder.setTitle("Select a playlist")
        builder.setItems(cs) { _, position ->
            if (position == 0) {
                showAddDialogPlaylistDialog()
            } else {
                val play = mList[position - 1]
                vm.addToPlaylist(play, model.id)
            }

        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showAddDialogPlaylistDialog() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(
            R.layout.playlist_dialogs, songs_root, false
        )
        builder.setView(view)
        val dialog = builder.create()

        view.playlist_dialog_btn.setOnClickListener {
            val name = view.playlist_dialog_et.text.toString()
            if (name.isEmpty()) {
                Toasty.warning(this, "Please input valid name").show()
            } else {
                val repo = getInstance(this)
                uiScope.launch(Dispatchers.IO) {
                    repo?.createPlaylist(name)
                    withContext(Dispatchers.Main) {
                        if (dialog.isShowing) {
                            dialog.dismiss()
                        }
                    }
                }
            }
        }

        dialog.show()
    }

}