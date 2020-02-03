package com.example.musicplayer.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

inline val Fragment.safeActivity: FragmentActivity
    get() = activity ?: throw IllegalStateException("Fragment not attached")