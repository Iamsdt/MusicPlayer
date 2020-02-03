package com.example.musicplayer.di

import com.example.musicplayer.ui.artist.ArtistVM
import com.example.musicplayer.ui.song.SongVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val vmModule = module {
    viewModel { ArtistVM(get()) }
    viewModel { SongVM(get()) }
}