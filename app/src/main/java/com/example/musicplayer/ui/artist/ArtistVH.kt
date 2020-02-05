package com.example.musicplayer.ui.artist

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Artist
import com.example.musicplayer.ext.toUri
import com.iamsdt.androidextension.addTextK
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.list_item.view.*

class ArtistVH(view: View) : RecyclerView.ViewHolder(view) {
    private val img: CircleImageView = view.main_list_icon
    private val title: AppCompatTextView = view.main_list_title
    private val subtitle: AppCompatTextView = view.main_list_subtitle

    fun bind(model: Artist, context: Context) {
        title.addTextK(model.name)
        subtitle.addTextK("${model.name} songs")
        val uri = model.albumId.toUri()
        Glide.with(context)
            .load(uri)
            .placeholder(R.drawable.ic_audio_player)
            .error(R.drawable.ic_audio_player)
            .into(img)
    }

}