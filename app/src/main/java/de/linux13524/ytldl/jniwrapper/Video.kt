package de.linux13524.ytldl.jniwrapper

import java.util.ArrayList

class Video(private val nativeHandle: Long) {

    inner class Quality(var itag: Int, var url: String)

    companion object {

        fun get(id: String): Video {
            val handle = getNative(id)
            return Video(handle)
        }

        external fun getNative(id: String): Long
    }

    external fun getId(): String
    external fun getChannelId(): String
    external fun getTitle(): String
    external fun getDescription(): String
    external fun getTags(): ArrayList<String>
    external fun getQualities(): ArrayList<Quality>

    external fun loadThumbnail()
    external fun loadDownloadLinks()
    external fun printFormats()
    external fun download()
}
