package com.example.musicplayer.ext

import android.content.ContentUris
import com.example.musicplayer.utils.PlayerConstants

fun Long.toUri() = ContentUris.withAppendedId(PlayerConstants.ARTWORK_URI, this)