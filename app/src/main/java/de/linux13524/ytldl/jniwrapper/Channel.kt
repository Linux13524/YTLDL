package de.linux13524.ytldl.jniwrapper


class Channel(private val nativeHandle: Long) {

    interface ProgressCallback {
        fun update(progress: Int, total: Int)
    }

    companion object {
        fun get(id: String, isUsername: Boolean): Channel {
            val handle = getNative(id, isUsername)
            return Channel(handle)
        }

        external fun getNative(id: String, isUsername: Boolean): Long
    }

    external fun getId(): String
    external fun getTitle(): String

    external fun loadVideos()
    external fun downloadVideos(itags: IntArray, callbackProgress: ProgressCallback?, folder: String?)
}