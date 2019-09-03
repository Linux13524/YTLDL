package de.linux13524.ytldl.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.linux13524.ytldl.R
import de.linux13524.ytldl.jniwrapper.Channel
import de.linux13524.ytldl.jniwrapper.Playlist
import de.linux13524.ytldl.jniwrapper.Video
import kotlinx.android.synthetic.main.fragment_add_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddListFragment : Fragment() {

    private var currentVideo: Video? = null
    private var currentPlaylist: Playlist? = null
    private var currentChannel: Channel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_search.setOnClickListener { onButtonSearchClick() }
        btn_print_formats.setOnClickListener { onButtonPrintFormatsClick() }
        btn_download.setOnClickListener { onButtonDownloadClick() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_list, container, false)
    }

    private fun onButtonSearchClick() {
        pb_download.progress = 0
        btn_download.isEnabled = false
        btn_print_formats.isEnabled = false

        listTypedCall({ searchVideo() }, { searchPlaylist() }, { searchChannel(it) })
    }

    private fun onButtonPrintFormatsClick() {
        currentVideo?.printFormats()
    }

    private fun onButtonDownloadClick() {
        pb_download.progress = 0
        btn_download.isEnabled = false

        listTypedCall({ downloadVideo() }, { downloadPlaylist() }, { downloadChannel() })
    }

    private fun listTypedCall(
        isVideo: () -> Unit, isPlaylist: () -> Unit,
        isChannel: (Boolean) -> Unit
    ) {
        when (spn_list_type.selectedItem.toString()) {
            "Video" -> isVideo()
            "Playlist" -> isPlaylist()
            "Channel" -> isChannel(false)
            "User" -> isChannel(true)
        }
    }

    private fun searchVideo() = GlobalScope.launch(Dispatchers.Main) {
        currentVideo = withContext(Dispatchers.Default) { Video.get(et_id.text.toString()) }

        tv_result.text = currentVideo?.getTitle()
        // currentVideo = null
        if (currentVideo?.getTitle() == "") return@launch


        withContext(Dispatchers.Default) {
            currentVideo?.loadDownloadLinks()
            currentVideo?.loadThumbnail()
        }

        btn_download.isEnabled = true
        btn_print_formats.isEnabled = true


        // TODO: currently not stored in db
        // val tags = currentVideo?.getTags()
    }

    private fun searchPlaylist() = GlobalScope.launch(Dispatchers.Main) {
        currentPlaylist = withContext(Dispatchers.Default) { Playlist.get(et_id.text.toString()) }

        tv_result.text = currentPlaylist?.getTitle()

        if (currentPlaylist?.getTitle() == "") return@launch

        withContext(Dispatchers.Default) { currentPlaylist?.loadVideos() }

        btn_download.isEnabled = true
    }

    private fun searchChannel(isUsername: Boolean) = GlobalScope.launch(Dispatchers.Main) {
        currentChannel = withContext(Dispatchers.Default) { Channel.get(et_id.text.toString(), isUsername) }

        tv_result.text = currentChannel?.getTitle()

        if (currentChannel?.getTitle() == "") return@launch

        withContext(Dispatchers.Default) { currentChannel?.loadVideos() }

        btn_download.isEnabled = true
    }

    private fun downloadVideo() = GlobalScope.launch(Dispatchers.Main) {
        val activity = activity ?: return@launch

        withContext(Dispatchers.Default) {
            currentVideo?.download()
        }

        pb_download?.progress = pb_download?.max ?: 0
        btn_download?.isEnabled = true
    }

    private fun downloadPlaylist() = GlobalScope.launch(Dispatchers.Main) {
        val activity = activity ?: return@launch

        withContext(Dispatchers.Default) {
            currentPlaylist?.downloadVideos(playlistProgress)
        }

        pb_download?.progress = pb_download?.max ?: 0
        btn_download?.isEnabled = true
    }

    private fun downloadChannel() = GlobalScope.launch(Dispatchers.Main) {
        val activity = activity ?: return@launch

        withContext(Dispatchers.Default) {
            currentChannel?.downloadVideos(channelProgress)
        }

        pb_download?.progress = pb_download?.max ?: 0
        btn_download?.isEnabled = true
    }

    private val playlistProgress = object : Playlist.ProgressCallback {
        override fun update(progress: Int, total: Int) {
            pb_download?.max = total
            pb_download?.progress = progress
        }
    }

    private val channelProgress = object : Channel.ProgressCallback {
        override fun update(progress: Int, total: Int) {
            pb_download?.max = total
            pb_download?.progress = progress
        }
    }
}
