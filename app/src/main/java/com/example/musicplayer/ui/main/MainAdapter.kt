package com.example.musicplayer.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.data.MainListItem

class MainAdapter(
    private val context: Context,
    private val list: List<MainListItem>,
    private val clickListener: ClickListener<MainListItem>
) :
    RecyclerView.Adapter<MainVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_item, parent, false)
        return MainVH(view)
    }

    override fun onBindViewHolder(holder: MainVH, position: Int) {
        val model = list[position]
        holder.bind(model, context)
        holder.itemView.setOnClickListener {
            clickListener.click(model)
        }
    }

    override fun getItemCount(): Int = list.size

}