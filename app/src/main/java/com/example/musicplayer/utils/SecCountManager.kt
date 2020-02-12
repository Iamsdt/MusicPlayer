package com.example.musicplayer.utils

import android.content.Context
import android.os.Environment
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.IOException
import java.util.*
import kotlin.math.abs

class SecCountManager(val context: Context) {

    companion object {
        private var manager: SecCountManager? = null

        fun getInstance(context: Context): SecCountManager {
            if (manager == null) {
                manager = SecCountManager(context)
            }
            return this.manager!!
        }
    }

    private var curSecCount: SecCount? = null
    fun startTracking(
        title: String,
        currentStreamPosition: Long
    ) {
        if (curSecCount != null) {
            Timber.e("SEC_COUNT", "We are currently tracking another SEC_COUNT")
            return
        }

        curSecCount = SecCount(
            title,
            currentStreamPosition,
            context, Date()
        )
        Timber.i("Play State: Tracking")
    }

    fun endTracking() {
        if (curSecCount == null) {
            return
        }
        Timber.i("Play State: Hey I am saving file")
        curSecCount!!.endSecCount(0)
        curSecCount!!.writeToFile()
        curSecCount = null
    }
}

internal class SecCount(
    val songId: String,
    val startPos: Long,
    val context: Context,
    val startTime: Date
) {
    var endPos: Long = 0
    var endTime: Date? = null


    fun endSecCount(endPos: Long) {
        this.endPos = endPos
        endTime = Date()
    }

    private fun getFile(fileName: String): File { // Get the directory for the user's public pictures directory.
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ), fileName
        )
        Timber.w("SEC_COUNT", Environment.DIRECTORY_DOWNLOADS + " secCount.txt")
        return file
    }

    fun writeToFile() {
        endPos = abs(startTime.time - endTime!!.time)

        if (endPos < startPos) {
            endPos += startPos
        }

        Timber.w(
            "SEC_COUNT" + "Log Sec Count: Song: " + songId +
                    " Pos: " + startPos + " - " + endPos +
                    " Time: " + startTime.toString() + " - " + endTime.toString()
        )
        try {
            val fileWriter = FileWriter(getFile("secCount.txt"), true)
            val text = songId + "\n" +
                    startPos + "\n" +
                    endPos + "\n" +
                    startTime.toString() + "\n" +
                    endTime.toString() + "\n" +
                    "===" + "\n"
            fileWriter.append(text)
            fileWriter.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Timber.e(
                "SEC_COUNT",
                Environment.DIRECTORY_DOWNLOADS + " secCount.txt File not found. Did you" +
                        " add a WRITE_EXTERNAL_STORAGE permission to the manifest?"
            )
        } catch (e: IOException) {
            e.printStackTrace()
            //throw RuntimeException(e)
        }
    }

}