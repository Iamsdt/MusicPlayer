package com.example.musicplayer.ui.main

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.data.MainListItem
import com.iamsdt.androidextension.addTextK
import kotlinx.android.synthetic.main.main_item.view.*

class MainVH(view: View) : RecyclerView.ViewHolder(view) {
    private val img: ImageView = view.main_list_icon
    private val title: AppCompatTextView = view.main_list_title
    private val subtitle: AppCompatTextView = view.main_list_subtitle

    fun bind(model: MainListItem, context: Context) {
        title.addTextK(model.title)
        subtitle.addTextK(model.subtitle)

        if (model.imageID != 0) {
            //img.background = context.getDrawable(model.imageID)
        }
    }

}