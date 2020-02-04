package com.example.musicplayer.ui.playlist

import com.example.musicplayer.data.model.Playlist

interface PlayistClickListener {
    fun click(model: Playlist)
    fun longClick(model: Playlist)
}