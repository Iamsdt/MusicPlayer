package com.example.musicplayer.ui.artist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.ui.main.ClickListener

class ArtistAdapter(private val context: Context, private val listener: ClickListener<Artist>) :
    ListAdapter<Artist, ArtistVH>(diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_item, parent, false)
        return ArtistVH(view)
    }

    override fun onBindViewHolder(holder: ArtistVH, position: Int) {
        val model = getItem(position)

        if (model != null) {
            holder.bind(model, context)
            holder.itemView.setOnClickListener {
                listener.click(model)
            }
        }


    }


    companion object {
        val diff = object : DiffUtil.ItemCallback<Artist>() {
            override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}