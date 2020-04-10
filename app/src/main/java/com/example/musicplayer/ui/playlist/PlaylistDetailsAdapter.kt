package com.example.musicplayer.ui.playlist

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.ext.toUri
import com.iamsdt.androidextension.addTextK
import com.iamsdt.androidextension.gone
import com.iamsdt.androidextension.show
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.playlist_item.view.*
import kotlinx.android.synthetic.main.undo_layout.view.*

class PlaylistDetailsAdapter(private val context: Context, private val listener: PlayListListener<Song>) :
    RecyclerView.Adapter<PlaylistDetailsAdapter.PlayListVH>() {

    var currentList = ArrayList<Song>()

    private val pendingItemRemoval = 3000 // 3sec
    private val handler = Handler() // handler for running delayed runnable
    private val pendingRunnable: MutableMap<Song?, Runnable> = HashMap() // map of items to pending runnables, so we can cancel a removal if need be

    private var itemsPendingRemoval: ArrayList<Song?> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlayListVH(view)
    }

    fun submitList(songs:List<Song>){
        currentList.clear()
        currentList.addAll(songs)
        notifyDataSetChanged()
    }

    private fun undoOpt(postTable: Song?) {
        val pendingRemovalRunnable = pendingRunnable[postTable]
        pendingRunnable.remove(postTable)
        if (pendingRemovalRunnable != null)
            handler.removeCallbacks(pendingRemovalRunnable)
        itemsPendingRemoval.remove(postTable)
        // this will rebind the row in "normal" state

        val id = currentList.indexOf(postTable)

        notifyItemChanged(id)
    }

    fun reorder(from:Int, to:Int){
        val fromModel = currentList[from]
        currentList[from] = currentList[to]
        currentList[to] = fromModel
        notifyItemMoved(from, to)
    }

    override fun onBindViewHolder(holder: PlayListVH, position: Int) {
        val model = currentList[position]

        holder.bind(model, context)
        holder.itemView.setOnClickListener {
            listener.click(model)
        }

        model.let {
            if (itemsPendingRemoval.contains(model)) {
                holder.regular.gone()
                holder.swipe.show()

                holder.undo.setOnClickListener { undoOpt(model) }

            } else {
                holder.swipe.gone()
                holder.regular.show()

                holder.bind(model, context)

                holder.itemView.tag = model
            }

            holder.itemView.setOnClickListener {
                listener.click(model)
            }
        }

    }

    fun pendingRemoval(position: Int) {

        val data: Song? = currentList[position]
        if (!itemsPendingRemoval.contains(data)) {
            itemsPendingRemoval.add(data)
            // this will redraw row in "undo" state
            notifyItemChanged(position)
            // let's create, store and post a runnable to remove the data
            val pendingRemovalRunnable = kotlinx.coroutines.Runnable {
                remove(currentList.indexOf(data))
            }
            handler.postDelayed(pendingRemovalRunnable, pendingItemRemoval.toLong())
            pendingRunnable[data] = pendingRemovalRunnable
        }
    }

    private fun remove(position: Int) {
        val data = currentList[position]

        if (itemsPendingRemoval.contains(data)) {
            itemsPendingRemoval.remove(data)
        }

        if (currentList.contains(data)) {
            listener.delete(data)
        }
    }

    fun isPendingRemoval(position: Int): Boolean {
        val data = currentList[position]
        return itemsPendingRemoval.contains(data)
    }

    class PlayListVH(view: View) : RecyclerView.ViewHolder(view) {
        private val img: CircleImageView = view.main_list_icon
        private val title: AppCompatTextView = view.main_list_title
        private val subtitle: AppCompatTextView = view.main_list_subtitle
        val regular: View = view.regular_layout
        val swipe: View = view.swipeLayout
        val delete: TextView = view.undo_deleteTxt
        val undo: TextView = view.undoBtn

        fun bind(model: Song, context: Context) {
            title.addTextK(model.title)
            subtitle.addTextK(model.artist)
            val uri = model.albumId.toUri()
            Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.ic_audio_player)
                .error(R.drawable.ic_audio_player)
                .into(img)
        }

    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}