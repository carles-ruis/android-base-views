package com.carles.simple.data.cache

import android.app.Application
import androidx.preference.PreferenceManager
import com.carles.simple.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(private val app: Application) {

    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(app.applicationContext) }

    val cacheExpirationTime: Long
        get() = preferences.getString(
            app.applicationContext.getString(R.string.preferences_cache_key),
            app.applicationContext.getString(R.string.preferences_cache_default_value)
        )?.toLong() ?: 0L
}