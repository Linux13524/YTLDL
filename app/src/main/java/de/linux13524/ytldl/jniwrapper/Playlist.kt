package de.linux13524.ytldl.jniwrapper


class Playlist(private val nativeHandle: Long) {

    interface ProgressCallback {
        fun update(progress: Int, total: Int)
    }

    companion object {
        fun get(id: String): Playlist {
            val handle = getNative(id)
            return Playlist(handle)
        }

        external fun getNative(id: String): Long
    }

    external fun getId(): String
    external fun getChannelId(): String
    external fun getTitle(): String

    external fun loadVideos()
    external fun downloadVideos(itags: IntArray, callbackProgress : ProgressCallback?, folder : String?)
}