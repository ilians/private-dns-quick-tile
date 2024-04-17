package com.jpwolfso.privdnsqt

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrivateDnsPreferences(context: Context) {

    private val preferences = context.getSharedPreferences("togglestates", Context.MODE_PRIVATE)

    var toggleOff by preferences.boolean("toggle_off")
    var toggleAuto by preferences.boolean("toggle_auto")
    var toggleOn by preferences.boolean("toggle_on")
    var isFirstRun by preferences.boolean("first_run")
}

private fun SharedPreferences.boolean(key: String, defaultValue: Boolean = true) =
    object : ReadWriteProperty<Any, Boolean> {

        override fun getValue(thisRef: Any, property: KProperty<*>) =
            getBoolean(key, defaultValue)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) =
            edit().putBoolean(key, value).apply()
    }
