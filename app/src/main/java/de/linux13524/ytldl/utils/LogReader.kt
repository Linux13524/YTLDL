package de.linux13524.ytldl.utils

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread


class LogReader(val getLine: (String) -> Unit) {

    var isRunning = false

    fun run() {
        if (isRunning) return
        thread {
            try {
                isRunning = true
                Runtime.getRuntime().exec("logcat -c")
                val logProcess = Runtime.getRuntime().exec("logcat -v raw YoutubeListDownloader:* *:S")

                val reader = BufferedReader(InputStreamReader(logProcess.inputStream))

                while (isRunning) {
                    val line = reader.readLine()
                    getLine(line)
                }
            } catch (e: Exception) {
                Log.w("LogReader", e.message)
            }
        }
    }

    fun stop() {
        isRunning = false
    }
}