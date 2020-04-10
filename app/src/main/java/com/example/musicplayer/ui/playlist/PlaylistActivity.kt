package com.example.musicplayer.ui.playlist

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.MainActivity
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Playlist
import com.example.musicplayer.data.repo.ContentLiveData
import com.example.musicplayer.data.repo.PlaylistRepository
import com.example.musicplayer.utils.Constants
import com.iamsdt.androidextension.MyCoroutineContext
import com.iamsdt.androidextension.gone
import com.iamsdt.androidextension.nextActivity
import com.iamsdt.androidextension.show
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.content_playlist.*
import kotlinx.android.synthetic.main.playlist_dialogs.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistActivity : AppCompatActivity(), LongClickListener<Playlist> {

    private val uiScope = MyCoroutineContext()

    private val vm: PlaylistVM by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)
        setSupportActionBar(toolbar)
        //add observer to coroutines
        lifecycle.addObserver(uiScope)

        loadTypeData(intent)

        //text
        playlist_text.setOnClickListener {
            showPlaylistDialog()
        }

        //recyclerview
        val adapter = PlaylistAdapter(this, this)
        val manager = LinearLayoutManager(this)
        playlist_rcv.layoutManager = manager
        playlist_rcv.adapter = adapter

        //item decoration
        val dividerItemDecoration = DividerItemDecoration(
            this,
            manager.orientation
        )
        playlist_rcv.addItemDecoration(dividerItemDecoration)

        val uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
        val provider = ContentLiveData(uri, this)
        //observer provider
        provider.observe(this, Observer {
            if (it == null || it.isEmpty()) {
                emptyView()
            } else {
                regularView()
                //submit list to the adapter
                adapter.submitList(it)
            }
        })

        vm.isUpdated.observe(this, Observer {
            it?.let {
                val list = provider.getContentProviderValue()
                if (list.isEmpty()) {
                    emptyView()
                } else {
                    regularView()
                    adapter.submitList(list)
                }
            }
        })

        val list = provider.getContentProviderValue()
        if (list.isEmpty()) {
            emptyView()
        } else {
            regularView()
            adapter.submitList(list)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadTypeData(intent: Intent) {
        val type = intent.getStringExtra(Constants.Type.Type) ?: ""
        val id = intent.getLongExtra(Constants.Playlist.PlaylistID, 0)
        val title = intent.getStringExtra(Constants.Playlist.PlaylistName) ?: ""
        val playImmediately = intent.getBooleanExtra("playlist", false)
        val widget = intent.getBooleanExtra("widget", false)

        if (widget) {
            val map = mapOf(
                Pair(Constants.Type.Type, type),
                Pair(Constants.Playlist.PlaylistID, id),
                Pair(Constants.Playlist.PlaylistName, title),
                Pair("playlist", playImmediately),
                Pair("widget", widget)
            )

            nextActivity<PlayListDetails>(list = map)
        }
    }

    private fun showPlaylistDialog() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(
            R.layout.playlist_dialogs, playlist_root, false
        )
        builder.setView(view)
        val dialog = builder.create()

        view.playlist_dialog_btn.setOnClickListener {
            val name = view.playlist_dialog_et.text.toString()
            if (name.isEmpty()) {
                Toasty.warning(this, "Please input valid name").show()
            } else {
                val repo = PlaylistRepository.getInstance(this)
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
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun regularView() {
        playlist_rcv.show()
        playlist_text.gone()
    }

    private fun emptyView() {
        playlist_rcv.gone()
        playlist_text.show()
    }

    override fun click(model: Playlist) {
        val map = mapOf(
            Pair(Constants.Type.Type, Constants.Type.TypePlaylist),
            Pair(Constants.Playlist.PlaylistID, model.id),
            Pair(Constants.Playlist.PlaylistName, model.name)
        )

        nextActivity<PlayListDetails>(list = map)
    }

    override fun longClick(model: Playlist) {
        showWarning(model)
    }

    private fun showWarning(model: Playlist) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete?")
        builder.setMessage("Are you want to delete ${model.name} playlist?")
        builder.setPositiveButton("yes") { dialog, which ->
            val repo = PlaylistRepository.getInstance(this)
            uiScope.launch(Dispatchers.IO) {
                repo?.deletePlaylist(model.id)
            }
        }

        builder.setNegativeButton("cancel") { _, _ ->
            //nothing to do
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.playlist_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            nextActivity<MainActivity>(finish = true)
        } else if (item.itemId == R.id.action_add) {
            showPlaylistDialog()
        }

        return super.onOptionsItemSelected(item)
    }

}
