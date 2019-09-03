package de.linux13524.ytldl.fragments


import android.content.SharedPreferences
import de.linux13524.ytldl.utils.PreferencesManager.syncPreferencesWithGlobalDownloadOptions
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import de.linux13524.ytldl.R

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(p: SharedPreferences?, prefName: String?) {
        activity?.syncPreferencesWithGlobalDownloadOptions()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }
}
