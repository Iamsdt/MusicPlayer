package com.example.musicplayer.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.MainActivity
import com.example.musicplayer.R
import com.example.musicplayer.data.MainListData
import com.example.musicplayer.data.MainListItem
import com.example.musicplayer.ui.albums.AlbumActivity
import com.example.musicplayer.ui.artist.ArtistListActivity
import com.example.musicplayer.ui.playlist.PlaylistActivity
import com.example.musicplayer.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.iamsdt.androidextension.nextActivity
import kotlinx.android.synthetic.main.activity_main_ui.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainUIActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener, ClickListener<MainListItem> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_ui)
        setSupportActionBar(main_toolbar)

        loadTypeData(intent)

        //layout manager
        val manager = LinearLayoutManager(this)
        main_rcv.layoutManager = manager
        //setup rev
        val adapter = MainAdapter(this, MainListData.getMainList(), this)
        main_rcv.adapter = adapter
        // set item
        val dividerItemDecoration = DividerItemDecoration(
            main_rcv.context,
            manager.orientation
        )
        main_rcv.addItemDecoration(dividerItemDecoration)

        //setup navigation ui
        val toggle = object : ActionBarDrawerToggle(
            this, drawer_layout, main_toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                val managerNav = getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager

                managerNav.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            }
        }

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
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

            nextActivity<PlaylistActivity>(list = map)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.all_music -> {
                nextActivity<MainActivity>(finish = true)
            }
            R.id.playlist -> {
                nextActivity<PlaylistActivity>()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun click(model: MainListItem) {
        when (model.id) {
            1 -> nextActivity<ArtistListActivity>()
            2 -> nextActivity<AlbumActivity>()
            3 -> nextActivity<PlaylistActivity>()
            else -> {
                //nothing to do
            }
        }
    }


}
