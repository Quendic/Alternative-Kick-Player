package com.example.kickplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kickplayer.api.StreamFetcher
import com.example.kickplayer.databinding.ActivityMainBinding
import com.example.kickplayer.util.FavoriteManager
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fetcher = StreamFetcher()
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFavoritesList()

        binding.watchButton.setOnClickListener {
            val username = binding.streamerEditText.text.toString().trim()
            if (username.isNotEmpty()) {
                startWatching(username)
            } else {
                Toast.makeText(this, "Lütfen bir yayıncı ismi girin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFavoritesList() {
        favoriteAdapter = FavoriteAdapter(this, FavoriteManager.getFavorites(this), 
            onStreamerClick = { streamer -> startWatching(streamer) },
            onLongClick = { streamer -> showRemoveFavoriteDialog(streamer) }
        )
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.favoritesRecyclerView.adapter = favoriteAdapter
    }

    private fun startWatching(username: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.watchButton.isEnabled = false

        fetcher.fetchPlaybackUrl(username) { playbackUrl ->
            runOnUiThread {
                binding.progressBar.visibility = View.GONE
                binding.watchButton.isEnabled = true

                if (playbackUrl != null) {
                    // Add to favorites if not already there
                    FavoriteManager.addFavorite(this, username)
                    favoriteAdapter.updateList(FavoriteManager.getFavorites(this))

                    val intent = Intent(this, PlayerActivity::class.java).apply {
                        putExtra("PLAYBACK_URL", playbackUrl)
                        putExtra("STREAMER_NAME", username)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Yayın bulunamadı veya yayıncı offline.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showRemoveFavoriteDialog(streamer: String) {
        AlertDialog.Builder(this)
            .setTitle("Favorilerden Kaldır")
            .setMessage("$streamer favorilerden kaldırılsın mı?")
            .setPositiveButton("Evet") { _, _ ->
                FavoriteManager.removeFavorite(this, streamer)
                favoriteAdapter.updateList(FavoriteManager.getFavorites(this))
            }
            .setNegativeButton("Hayır", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh statuses
        favoriteAdapter.updateList(FavoriteManager.getFavorites(this))
    }
}
