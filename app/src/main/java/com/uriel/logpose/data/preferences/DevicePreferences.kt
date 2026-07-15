package com.uriel.logpose.data.preferences

import android.content.Context

class DevicePreferences(context: Context) {

    private val preferences =
        context.getSharedPreferences("logpose_preferences", Context.MODE_PRIVATE)

    fun saveSelectedDevice(mac: String) {

        preferences.edit()
            .putString("selected_device", mac)
            .apply()

    }

    fun getSelectedDevice(): String? {

        return preferences.getString("selected_device", null)

    }

}