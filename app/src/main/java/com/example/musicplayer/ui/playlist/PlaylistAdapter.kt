package com.example.musicplayer.ui.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Playlist

class PlaylistAdapter(private val context: Context, private val listener: LongClickListener<Playlist>) :
    ListAdapter<Playlist, PlaylistVH>(diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return PlaylistVH(view)
    }

    override fun onBindViewHolder(holder: PlaylistVH, position: Int) {
        val model = getItem(position)

        if (model != null) {
            holder.bind(model, context)
            holder.itemView.setOnClickListener {
                listener.click(model)
            }
            holder.itemView.setOnLongClickListener {
                listener.longClick(model)
                true
            }
        }


    }


    companion object {
        val diff = object : DiffUtil.ItemCallback<Playlist>() {
            override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}