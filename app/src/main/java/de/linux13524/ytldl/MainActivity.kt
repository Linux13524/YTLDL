package de.linux13524.ytldl


import android.os.Bundle
import android.os.Environment
import android.widget.ScrollView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import de.linux13524.ytldl.utils.LogReader
import de.linux13524.ytldl.utils.PermissionManager
import de.linux13524.ytldl.utils.PreferencesManager.syncPreferencesWithGlobalDownloadOptions
import kotlinx.android.synthetic.main.activity_main.*
import de.linux13524.ytldl.jniwrapper.download.GlobalOptions as GlobalDownloadOptions
import de.linux13524.ytldl.jniwrapper.filesystem.Settings as FilesystemSettings


@Suppress("UNUSED_PARAMETER")
class MainActivity : FragmentActivity() {

    private val sdCardDir = "${Environment.getExternalStorageDirectory()}/YT-DL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPermissions()
        initNavigation()
        initDownloadOptions()
        initFilesystem()

        LogReader(::updateLog).run()
    }

    private fun initNavigation(){
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)

        toolbar.setupWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)
    }

    private fun initDownloadOptions(){
        syncPreferencesWithGlobalDownloadOptions()
        // TODO: Add to settings fragment/preferences
        GlobalDownloadOptions.setSaveVideoName(true)
        GlobalDownloadOptions.setPath(sdCardDir)
    }

    private fun initFilesystem(){
        FilesystemSettings.setDbPath("/data/data/de.linux13524.ytldl/databases/")
    }

    private fun initPermissions(){
        PermissionManager.checkPermissions(this)
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
