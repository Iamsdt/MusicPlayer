package com.example.musicplayer.di

import com.example.musicplayer.ui.artist.ArtistVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val vmModule = module {
    viewModel { ArtistVM(get()) }
}