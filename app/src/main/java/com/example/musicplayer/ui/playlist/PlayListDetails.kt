package com.example.musicplayer.ui.playlist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.data.DataHolder
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.repo.PlaylistRepository
import com.example.musicplayer.ui.play.PlayActivity
import com.example.musicplayer.utils.Constants
import com.example.musicplayer.utils.SwipeUtil
import com.iamsdt.androidextension.MyCoroutineContext
import com.iamsdt.androidextension.nextActivity
import kotlinx.android.synthetic.main.activity_playlist_details.*
import kotlinx.android.synthetic.main.content_playlist_details.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class PlayListDetails : AppCompatActivity(), PlayListListener<Song> {

    private val vm: PlaylistVM by viewModel()
    private val uiScope = MyCoroutineContext()

    private var title: String = ""
    private var type: String = ""
    private var id: Long = 0L

    private var widget = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_details)

        loadTypeData(intent)

        //set title
        toolbar.title = title
        //set action toolbar
        setSupportActionBar(toolbar)

        //set item layout
        //layout manager
        val manager = LinearLayoutManager(this)
        playlist_detailsRcv.layoutManager = manager
        setSwipeForRecyclerView(playlist_detailsRcv)
        //setup rev
        val adapter = PlaylistDetailsAdapter(this, this)
        playlist_detailsRcv.adapter = adapter
        // set item
        val dividerItemDecoration = DividerItemDecoration(
            playlist_detailsRcv.context,
            manager.orientation
        )
        playlist_detailsRcv.addItemDecoration(dividerItemDecoration)

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
        val playNow = intent.getBooleanExtra("playlist", false)

        if (widget){
            val map = mapOf(
                Pair(Constants.Type.Type, type),
                Pair(Constants.Songs.ID, id),
                Pair(Constants.Songs.Name, title),
                Pair("widget", widget),
                Pair("playlist", playNow)
            )

            nextActivity<PlayActivity>(list = map)
        }
    }

    private fun setSwipeForRecyclerView(recyclerView: RecyclerView) {

        val swipeHelper =
            object : SwipeUtil(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.START or ItemTouchHelper.END,
                this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val swipedPosition = viewHolder.adapterPosition
                    val adapter = recyclerView.adapter as PlaylistDetailsAdapter
                    adapter.pendingRemoval(swipedPosition)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onMoved(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    fromPos: Int,
                    target: RecyclerView.ViewHolder,
                    toPos: Int,
                    x: Int,
                    y: Int
                ) {
                    val adapter = recyclerView.adapter as PlaylistDetailsAdapter
                    adapter.reorder(fromPos, toPos)
                    //request to the server
                    vm.reorder(id, fromPos, toPos)
                    super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
                }

                override fun getSwipeDirs(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    val position = viewHolder.adapterPosition
                    val adapter = recyclerView.adapter as PlaylistDetailsAdapter
                    return if (adapter.isPendingRemoval(position)) {
                        0
                    } else super.getSwipeDirs(recyclerView, viewHolder)
                }
            }

        val mItemTouchHelper = ItemTouchHelper(swipeHelper)
        mItemTouchHelper.attachToRecyclerView(recyclerView)

        //set swipe label
        //swipeHelper.leftSwipeLabel = "Bookmark removed"
        //set swipe background-Color
        swipeHelper.leftColorCode = ContextCompat.getColor(this, R.color.red_300)
    }


    override fun click(model: Song) {
        Timber.i(model.title)
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

    override fun delete(model: Song) {
        val repo = PlaylistRepository.getInstance(this)
        uiScope.launch(Dispatchers.IO) {
            repo?.removeFromPlaylist(id, model.id)
            withContext(Dispatchers.Main) {
                vm.requestUpdate(id)
            }
        }
    }

}