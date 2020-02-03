package com.example.musicplayer.ui.artist

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.data.model.Artist
import com.iamsdt.androidextension.addTextK
import kotlinx.android.synthetic.main.main_item.view.*

class ArtistVH(view: View) : RecyclerView.ViewHolder(view) {
    private val img: ImageView = view.main_list_icon
    private val title: AppCompatTextView = view.main_list_title
    private val subtitle: AppCompatTextView = view.main_list_subtitle

    fun bind(model: Artist, context: Context) {
        title.addTextK(model.name)
        subtitle.addTextK("${model.name} songs")

        if (model.albumId > 0) {
            //img.background = context.getDrawable(model.imageID.toInt())
            //todo implement
        }
    }

}