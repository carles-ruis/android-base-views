package com.carles.simple.data.cache

import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

data class CacheKey(val cacheItem: CacheItems, val itemId: Int = 0)

enum class CacheItems { MONSTERS, MONSTER_DETAIL }

@Singleton
class Cache @Inject constructor(private val preferences: AppPreferences) {

    private val map: MutableMap<CacheKey, Long> = mutableMapOf()

    fun isCached(key: CacheKey): Boolean {
        val expiration = map[key] ?: 0L
        if (expiration < now()) {
            map.remove(key)
        }
        return map.containsKey(key)
    }

    @SuppressWarnings("MagicNumber")
    fun set(key: CacheKey) {
        map[key] = now() + preferences.cacheExpirationTime * 60 * 1000
    }

    fun resetCacheExpiration() {
        for (key in map.keys) {
            set(key)
        }
    }

    private fun now() = Calendar.getInstance().timeInMillis
}
