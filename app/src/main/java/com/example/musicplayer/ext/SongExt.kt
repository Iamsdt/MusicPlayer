package com.example.musicplayer.ext

import com.example.musicplayer.utils.GeneralUtils

fun Int.format(): String {
    return GeneralUtils.formatMilliseconds(this.toLong())
}

fun Int.fix(): Int {
    var value = this
    while (value >= 1000) {
        value -= 1000
    }
    return value
}
