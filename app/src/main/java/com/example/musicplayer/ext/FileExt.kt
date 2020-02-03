package com.example.musicplayer.ext

import android.os.Environment
import android.util.Log
import java.io.File

private const val INTERNAL_STORAGE = "/Internal Storage"
private const val EXTERNAL_STORAGE = "/SD Card"

fun File?.fixedPath(): String {
    try {
        val fixedPath =
            StringBuilder(if (Environment.isExternalStorageEmulated(this!!)) INTERNAL_STORAGE else EXTERNAL_STORAGE)
        val parts = path.split("/")
        for ((i, part) in parts.withIndex()) {
            if (i > if (fixedPath.contains(EXTERNAL_STORAGE)) 2 else 3) {
                fixedPath.append("/$part")
            }
        }
        return fixedPath.toString()
    } catch (ex: IllegalArgumentException) {
        Log.println(Log.ERROR, "Exception", ex.message!!)
    }
    return this?.name!!
}

fun File?.fixedName(): String {
    try {
        val fixedPath =
            if (Environment.isExternalStorageEmulated(this!!)) INTERNAL_STORAGE else EXTERNAL_STORAGE
        val parts = path.split("/")
        return if ((parts.size == 3 && fixedPath == EXTERNAL_STORAGE) || (parts.size == 4 && fixedPath == INTERNAL_STORAGE)) fixedPath.substring(
            fixedPath.indexOf("/") + 1
        ) else parts[parts.size - 1]
    } catch (ex: IllegalArgumentException) {
        Log.println(Log.ERROR, "Exception", ex.message!!)
    }
    return this?.name!!
}