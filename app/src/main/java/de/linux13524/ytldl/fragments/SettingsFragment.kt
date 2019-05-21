package de.linux13524.ytldl.fragments


import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

import de.linux13524.ytldl.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        //addPreferencesFromResource(R.xml.preferences)
    }
}
