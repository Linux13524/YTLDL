package de.linux13524.ytldl


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ScrollView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import de.linux13524.ytldl.jniwrapper.Filesystem
import de.linux13524.ytldl.utils.LogReader
import de.linux13524.ytldl.utils.PermissionManager
import kotlinx.android.synthetic.main.activity_main.*


@Suppress("UNUSED_PARAMETER")
class MainActivity : Activity() {

    private val sdCardDir = "${Environment.getExternalStorageDirectory()}/YT-DL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        findViewById<Toolbar>(R.id.toolbar).setupWithNavController(navController, appBarConfiguration)


        Filesystem.Settings.setDbPath("/data/data/de.linux13524.ytldl/databases/")
        Filesystem.Settings.setVideoPath(sdCardDir)

        PermissionManager.checkPermissions(this)

        LogReader(::updateLog).run()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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
