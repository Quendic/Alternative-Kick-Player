package com.example.kickplayer.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoriteManager {
    private const val PREFS_NAME = "kick_player_prefs"
    private const val KEY_FAVORITES = "favorites"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getFavorites(context: Context): MutableList<String> {
        val json = getPrefs(context).getString(KEY_FAVORITES, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<String>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun addFavorite(context: Context, username: String) {
        val favorites = getFavorites(context)
        if (!favorites.contains(username.lowercase())) {
            favorites.add(username.lowercase())
            saveFavorites(context, favorites)
        }
    }

    fun removeFavorite(context: Context, username: String) {
        val favorites = getFavorites(context)
        favorites.remove(username.lowercase())
        saveFavorites(context, favorites)
    }

    fun isFavorite(context: Context, username: String): Boolean {
        return getFavorites(context).contains(username.lowercase())
    }

    private fun saveFavorites(context: Context, favorites: List<String>) {
        val json = Gson().toJson(favorites)
        getPrefs(context).edit().putString(KEY_FAVORITES, json).apply()
    }
}
