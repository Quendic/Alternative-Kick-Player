package com.example.kickplayer.api

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class StreamFetcher {
    private val client = OkHttpClient()
    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"

    fun fetchPlaybackUrl(username: String, callback: (String?) -> Unit) {
        val url = "https://kick.com/api/v1/channels/$username"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", userAgent)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    try {
                        val json = JSONObject(body)
                        // Try root playback_url first, then fallback to livestream object
                        var playbackUrl = json.optString("playback_url", null)
                        
                        if (playbackUrl == null || playbackUrl.isEmpty()) {
                            val livestream = json.optJSONObject("livestream")
                            playbackUrl = livestream?.optString("playback_url")
                        }
                        
                        callback(playbackUrl)
                    } catch (e: Exception) {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }
        })
    }

    fun isLive(username: String, callback: (Boolean) -> Unit) {
        val url = "https://kick.com/api/v1/channels/$username"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", userAgent)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    try {
                        val json = JSONObject(body)
                        val isLive = json.optJSONObject("livestream") != null
                        callback(isLive)
                    } catch (e: Exception) {
                        callback(false)
                    }
                } else {
                    callback(false)
                }
            }
        })
    }
}
