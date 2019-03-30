package de.linux13524.ytldl.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast

object PermissionManager {
    private val REQUEST_CODE = 0
    private val PERMISSIONS = arrayListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    fun checkPermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        PERMISSIONS
                .filter { activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
                .forEach { activity.requestPermissions(arrayOf(it), REQUEST_CODE) }

    }

    fun callback(activity: Activity, requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        val result = grantResults.getOrNull(0)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || result == null
                || requestCode != REQUEST_CODE) return

        if (result == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(activity, "Permissions were denied! Closing app...", Toast.LENGTH_LONG).show()
            activity.finish()
        }

        if (result != PackageManager.PERMISSION_GRANTED)
            activity.requestPermissions(permissions, REQUEST_CODE)
    }
}