package com.example.musicplayer.ui.albums

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.ui.main.ClickListener

class AlbumAdapter(private val context: Context, private val listener: ClickListener<Album>) :
    ListAdapter<Album, AlbumVH>(diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return AlbumVH(view)
    }

    override fun onBindViewHolder(holder: AlbumVH, position: Int) {
        val model = getItem(position)

        if (model != null) {
            holder.bind(model, context)
            holder.itemView.setOnClickListener {
                listener.click(model)
            }
        }


    }


    companion object {
        val diff = object : DiffUtil.ItemCallback<Album>() {
            override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}