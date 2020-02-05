package com.example.musicplayer.ui.songs

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.ui.playlist.LongClickListener

class SongAdapter(private val context: Context, private val listener: LongClickListener<Song>) :
    ListAdapter<Song, SongVH>(diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return SongVH(view)
    }

    override fun onBindViewHolder(holder: SongVH, position: Int) {
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
        val diff = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}