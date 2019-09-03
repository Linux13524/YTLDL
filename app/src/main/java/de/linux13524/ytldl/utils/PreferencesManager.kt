package de.linux13524.ytldl.utils

import android.app.Activity
import android.content.SharedPreferences
import android.preference.PreferenceManager
import de.linux13524.ytldl.jniwrapper.download.GlobalOptions

object PreferencesManager {

    fun Activity.getItags(): IntArray? {
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val itag = sharedPref.getInt("pref_itag", 0)

        return if (itag > 0) intArrayOf(itag) else null
    }

    fun Activity.syncPreferencesWithGlobalDownloadOptions() {
        getItags()?.let { GlobalOptions.setItags(it) }
    }
}