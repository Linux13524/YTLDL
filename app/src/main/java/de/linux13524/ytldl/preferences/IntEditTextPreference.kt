package de.linux13524.ytldl.preferences

import android.content.Context
import android.preference.EditTextPreference
import android.util.AttributeSet


class IntEditTextPreference : EditTextPreference {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun getPersistedString(defaultReturnValue: String?) = getPersistedInt(-1).toString()
    override fun persistString(value: String) = persistInt(Integer.valueOf(value))
}