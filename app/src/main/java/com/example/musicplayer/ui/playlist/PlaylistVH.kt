package com.example.musicplayer.ui.playlist

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Playlist
import com.example.musicplayer.ext.toUri
import com.iamsdt.androidextension.addTextK
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.list_item.view.*

class PlaylistVH(view: View) : RecyclerView.ViewHolder(view) {
    private val img: CircleImageView = view.main_list_icon
    private val title: AppCompatTextView = view.main_list_title
    private val subtitle: AppCompatTextView = view.main_list_subtitle

    fun bind(model: Playlist, context: Context) {
        title.addTextK(model.name)
        subtitle.addTextK("Total ${model.songCount} songs")
        val uri = model.id.toUri()
        Glide.with(context)
            .load(uri)
            .placeholder(R.drawable.ic_playlist_music_black_24dp)
            .error(R.drawable.ic_playlist_music_black_24dp)
            .into(img)
    }
}