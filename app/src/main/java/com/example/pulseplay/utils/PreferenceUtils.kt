package com.example.pulseplay.utils

import android.content.Context

object PreferenceUtils {
    private const val sharedPreferences = "SHARED_PREFERENCES"
    const val accessToken = "ACCESS_TOKEN"
    const val codeVerifier = "CODE_VERIFIER"
    const val loginStatus = "LOGIN_STATUS"


    fun setData(context: Context, name: String?, value: String?) {
        val settings = context
            .getSharedPreferences(
                context.packageName + sharedPreferences,
                0
            )
        val editor = settings.edit()
        editor.putString(name, value)
        editor.apply()
    }

    fun getData(context: Context, name: String?): String? {
        val settings = context
            .getSharedPreferences(
                context.packageName + sharedPreferences,
                0
            )
        return settings.getString(name, "")
    }

    fun setLongData(context: Context, name: String?, value: Long?) {
        val settings = context
            .getSharedPreferences(
                context.packageName + sharedPreferences,
                0
            )
        val editor = settings.edit()
        editor.putLong(name, value!!)
        editor.apply()
    }

    fun getLongData(context: Context, name: String?): Long {
        val settings = context
            .getSharedPreferences(
                context.packageName + sharedPreferences,
                0
            )
        return settings.getLong(name, 0)
    }

    fun setBoolData(context: Context, name: String?, value: Boolean) {
        val settings = context
            .getSharedPreferences(
                context.packageName + sharedPreferences,
                0
            )
        val editor = settings.edit()
        editor.putBoolean(name, value)
        editor.apply()
    }

    fun getBoolData(context: Context, name: String?): Boolean {
        val settings = context
            .getSharedPreferences(
                context.packageName + sharedPreferences,
                0
            )
        return settings.getBoolean(name, false)
    }

}