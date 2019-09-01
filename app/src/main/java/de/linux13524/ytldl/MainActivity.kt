package de.linux13524.ytldl


import android.os.Bundle
import android.os.Environment
import android.widget.ScrollView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import de.linux13524.ytldl.jniwrapper.Filesystem
import de.linux13524.ytldl.utils.LogReader
import de.linux13524.ytldl.utils.PermissionManager
import kotlinx.android.synthetic.main.activity_main.*


@Suppress("UNUSED_PARAMETER")
class MainActivity : FragmentActivity() {

    private val sdCardDir = "${Environment.getExternalStorageDirectory()}/YT-DL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)

        toolbar.setupWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)

        Filesystem.Settings.setDbPath("/data/data/de.linux13524.ytldl/databases/")
        Filesystem.Settings.setVideoPath(sdCardDir)

        PermissionManager.checkPermissions(this)

        LogReader(::updateLog).run()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        PermissionManager.callback(this, requestCode, permissions, grantResults)
    }

    private fun updateLog(newMessage: String) {
        runOnUiThread {
            tv_log.text = "${tv_log.text}\n$newMessage"
            lyt_scroll.post { lyt_scroll.fullScroll(ScrollView.FOCUS_DOWN) }
        }
    }

    companion object {
        init {
            System.loadLibrary("native")
        }
    }
}
