package de.linux13524.ytldl


import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.view.Menu
import android.view.View
import de.linux13524.ytldl.jniwrapper.Channel
import de.linux13524.ytldl.jniwrapper.Playlist
import de.linux13524.ytldl.jniwrapper.Video
import de.linux13524.ytldl.utils.LogReader
import de.linux13524.ytldl.utils.PermissionManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread
import android.widget.Toast
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ScrollView
import de.linux13524.ytldl.jniwrapper.Filesystem


@Suppress("UNUSED_PARAMETER")
class MainActivity : Activity() {

    private val sdCardDir = "${Environment.getExternalStorageDirectory()}/YT-DL"

    private var currentVideo: Video? = null
    private var currentPlaylist: Playlist? = null
    private var currentChannel: Channel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_search.setOnClickListener(::onButtonSearchClick)
        btn_print_formats.setOnClickListener(::onButtonPrintFormatsClick)
        btn_download.setOnClickListener(::onButtonDownloadClick)

        Filesystem.Settings.setDbPath("/data/data/de.linux13524.ytldl/databases/")
        Filesystem.Settings.setVideoPath(sdCardDir)

        PermissionManager.checkPermissions(this)

        LogReader(::updateLog).run()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        PermissionManager.callback(this, requestCode, permissions, grantResults)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        MenuInflater(this).inflate(R.menu.menu_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getItags(): IntArray {
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val itag = sharedPref.getInt("pref_itag", 0)

        return if (itag == 0)
            intArrayOf(37, 22, 18)
        else intArrayOf(itag)
    }

    private fun onButtonSearchClick(v: View?) {
        pb_download.progress = 0
        btn_download.isEnabled = false
        btn_print_formats.isEnabled = false

        listTypedCall(::searchVideo, ::searchPlaylist, ::searchChannel)
    }

    private fun onButtonPrintFormatsClick(v: View?) {
        currentVideo?.printFormats()
    }

    private fun onButtonDownloadClick(v: View?) {
        pb_download.progress = 0
        btn_download.isEnabled = false

        listTypedCall(::downloadVideo, ::downloadPlaylist, ::downloadChannel)
    }

    private fun listTypedCall(isVideo: () -> Unit, isPlaylist: () -> Unit,
                              isChannel: (Boolean) -> Unit) {
        when (spn_list_type.selectedItem.toString()) {
            "Video" -> isVideo()
            "Playlist" -> isPlaylist()
            "Channel" -> isChannel(false)
            "User" -> isChannel(true)
        }
    }

    private fun searchVideo() {
        thread {
            currentVideo = Video.get(et_id.text.toString())

            runOnUiThread { tv_result.text = currentVideo?.getTitle() }

            if (currentVideo?.getTitle() == "") return@thread

            runOnUiThread { btn_download.visibility = View.VISIBLE }

            currentVideo?.loadDownloadLinks()

            runOnUiThread {
                btn_download.isEnabled = true
                btn_print_formats.isEnabled = true
            }

            currentVideo?.loadThumbnail()

            // TODO: currently not stored in db
            // val tags = currentVideo?.getTags()
        }
    }

    private fun searchPlaylist() {
        thread {
            currentPlaylist = Playlist.get(et_id.text.toString())

            runOnUiThread { tv_result.text = currentPlaylist?.getTitle() }

            if (currentPlaylist?.getTitle() == "") return@thread

            runOnUiThread { btn_download.visibility = View.VISIBLE }

            currentPlaylist?.loadVideos()

            runOnUiThread { btn_download.isEnabled = true }
        }
    }

    private fun searchChannel(isUsername: Boolean) {
        thread {
            currentChannel = Channel.get(et_id.text.toString(), isUsername)

            runOnUiThread { tv_result.text = currentChannel?.getTitle() }

            if (currentChannel?.getTitle() == "") return@thread

            runOnUiThread { btn_download.visibility = View.VISIBLE }

            currentChannel?.loadVideos()

            runOnUiThread { btn_download.isEnabled = true }
        }
    }

    private fun downloadVideo() {
        thread {
            currentVideo?.downloadDir(getItags(), sdCardDir)
            runOnUiThread {
                pb_download?.progress = pb_download?.max ?: 0
                btn_download?.isEnabled = true
            }
        }
    }

    private fun downloadPlaylist() {
        thread {
            currentPlaylist?.downloadVideos(
                getItags(),
                object : Playlist.ProgressCallback {
                    override fun update(progress: Int, total: Int) {
                        runOnUiThread {
                            pb_download?.max = total
                            pb_download?.progress = progress
                        }
                    }
                },
                sdCardDir)
            runOnUiThread {
                pb_download?.progress = pb_download?.max ?: 0
                btn_download?.isEnabled = true
            }
        }
    }

    private fun downloadChannel(isUsername: Boolean) {
        thread {
            currentChannel?.downloadVideos(
                getItags(),
                object : Channel.ProgressCallback {
                    override fun update(progress: Int, total: Int) {
                        runOnUiThread {
                            pb_download?.max = total
                            pb_download?.progress = progress
                        }
                    }
                },
                sdCardDir)
            runOnUiThread {
                pb_download?.progress = pb_download?.max ?: 0
                btn_download?.isEnabled = true
            }
        }
    }

    private fun updateLog(newMessage: String) {
        runOnUiThread {
            tv_log.text = "${tv_log.text}\n$newMessage"
            lyt_scroll.post({ lyt_scroll.fullScroll(ScrollView.FOCUS_DOWN) })
        }
    }

    companion object {
        init {
            System.loadLibrary("native")
        }
    }
}
