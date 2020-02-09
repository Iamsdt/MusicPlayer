package com.example.musicplayer.utils

import android.content.Context
import android.os.Environment
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SecCount2 {

    companion object {
        private var writeTitle: String = ""

        private var list = ArrayList<Track>()

        private fun getFile(context: Context): File {
            val path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(path, "secCount.txt")
            Timber.w("SEC_COUNT", Environment.DIRECTORY_DOWNLOADS.toString() + " secCount.txt")

            //todo do it latter
//        val path1 = Environment.getDataDirectory()
//        val path2 = context.filesDir.absolutePath
//        val path3 = context.getDir("Code", Context.MODE_PRIVATE)
//        val path4 = Environment.getRootDirectory()
//        val path5 = Environment.getExternalStorageDirectory()
//        val path6 = Environment.getExternalStoragePublicDirectory("")

            return file
        }

        private fun isTimeForWrite(
            title: String, startPos: Long, endPos: Long,
            startTime: Date, endTime: Date, force: Boolean = false
        ): Boolean {

            if (force) {
                return true
            }

            val track = Track(title, startPos, endPos, startTime, endTime)

            if (endPos == 0L) {
                list[0] = track
                return false
            }

            var status = false

            if (list.isEmpty()) {
                list.add(track)
                status = true
            } else {
                if (list[0].title == title) {
                    list[0] = track
                    status = false
                } else {
                    list[0] = track
                    return true
                }
            }

            return status
        }

        fun writeToFile(
            context: Context, title: String, startPos: Long, endPos: Long,
            startTime: Date, endTime: Date, force: Boolean = false
        ) {

            if (!isTimeForWrite(title, startPos, endPos, startTime, endTime, force)) {
                return
            }

            try {
                val path = getFile(context)
                val fileWriter = FileWriter(path, true)
                Timber.i(
                    "Writing file $title on ${path.absoluteFile} properties" + "" +
                            "\tStart pos: $startPos" +
                            "\tEnd pos: $endPos" +
                            "\tStart time: ${startTime.readable()}" +
                            "\tEnd time: ${endTime.readable()}"
                )
                fileWriter.append(
                    title + "\n" +
                            startPos + "\n" +
                            endPos + "\n" +
                            startTime.readable() + "\n" +
                            endTime.readable() + "\n" +
                            "===" + "\n"
                )
                fileWriter.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Timber.e(
                    "SEC_COUNT",
                    Environment.DIRECTORY_DOWNLOADS.toString() + " secCount.txt File not found. Did you" +
                            " add a WRITE_EXTERNAL_STORAGE permission to the manifest?"
                )
                throw RuntimeException(e)
            } catch (e: IOException) {
                e.printStackTrace()
                throw RuntimeException(e)
            }
        }
    }

    data class Track(
        val title: String, val startPos: Long, val endPos: Long,
        val startTime: Date, val endTime: Date
    )
}

fun Date.readable(): String {
    val formatter = SimpleDateFormat("E MMM dd HH:mm:ss zz yyyy", Locale.getDefault())
    return formatter.format(this)
}