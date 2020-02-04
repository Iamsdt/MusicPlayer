package com.example.musicplayer.di

import com.example.musicplayer.ui.albums.AlbumVM
import com.example.musicplayer.ui.artist.ArtistVM
import com.example.musicplayer.ui.playlist.PlaylistVM
import com.example.musicplayer.ui.songs.SongVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val vmModule = module {
    viewModel { ArtistVM(get()) }
    viewModel { SongVM(get()) }
    viewModel { AlbumVM(get()) }
    viewModel { PlaylistVM(get()) }
}