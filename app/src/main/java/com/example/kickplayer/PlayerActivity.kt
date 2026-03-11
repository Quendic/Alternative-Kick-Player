package com.example.kickplayer

import android.os.Bundle
import android.view.View
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import androidx.media3.common.C
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.datasource.DefaultHttpDataSource
import com.example.kickplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val playbackUrl = intent.getStringExtra("PLAYBACK_URL")
        val streamerName = intent.getStringExtra("STREAMER_NAME") ?: ""

        if (playbackUrl != null) {
            initializePlayer(playbackUrl)
            setupControls(streamerName)
        } else {
            finish()
        }
    }

    private fun setupControls(streamerName: String) {
        val btnFavorite = binding.playerView.findViewById<android.widget.ImageButton>(R.id.btnFavorite)
        val btnQuality = binding.playerView.findViewById<android.widget.ImageButton>(R.id.btnQuality)
        val btnFullScreen = binding.playerView.findViewById<android.widget.ImageButton>(R.id.btnFullScreen)

        updateFavoriteButton(streamerName, btnFavorite)

        btnFavorite?.setOnClickListener {
            if (com.example.kickplayer.util.FavoriteManager.isFavorite(this, streamerName)) {
                com.example.kickplayer.util.FavoriteManager.removeFavorite(this, streamerName)
                Toast.makeText(this, "Favorilerden kaldırıldı", Toast.LENGTH_SHORT).show()
            } else {
                com.example.kickplayer.util.FavoriteManager.addFavorite(this, streamerName)
                Toast.makeText(this, "Favorilere eklendi", Toast.LENGTH_SHORT).show()
            }
            updateFavoriteButton(streamerName, btnFavorite)
        }

        btnQuality?.setOnClickListener {
            showQualityDialog()
        }

        btnFullScreen?.setOnClickListener {
            toggleFullScreen()
        }
    }

    private fun showQualityDialog() {
        val mappedTrackInfo = (player?.trackSelector as? DefaultTrackSelector)?.currentMappedTrackInfo ?: return
        val renderersCount = mappedTrackInfo.rendererCount
        
        for (i in 0 until renderersCount) {
            if (player?.getRendererType(i) == C.TRACK_TYPE_VIDEO) {
                val trackGroups = mappedTrackInfo.getTrackGroups(i)
                val options = mutableListOf<String>()
                val trackIndices = mutableListOf<Pair<Int, Int>>()

                options.add("Otomatik")
                trackIndices.add(-1 to -1)

                for (j in 0 until trackGroups.length) {
                    val group = trackGroups.get(j)
                    for (k in 0 until group.length) {
                        val format = group.getFormat(k)
                        options.add("${format.height}p (${format.bitrate / 1000}kbps)")
                        trackIndices.add(j to k)
                    }
                }

                AlertDialog.Builder(this)
                    .setTitle("Kalite Seçin")
                    .setItems(options.toTypedArray()) { _, which ->
                        val selector = player?.trackSelector as? DefaultTrackSelector
                        if (which == 0) {
                            selector?.setParameters(selector.buildUponParameters().clearOverrides())
                        } else {
                            val (groupIndex, trackIndex) = trackIndices[which]
                            selector?.setParameters(
                                selector.buildUponParameters()
                                    .setOverrideForType(TrackSelectionOverride(trackGroups.get(groupIndex), trackIndex))
                            )
                        }
                    }
                    .show()
                break
            }
        }
    }

    private fun updateFavoriteButton(streamerName: String, btnFavorite: android.widget.ImageButton?) {
        val isFav = com.example.kickplayer.util.FavoriteManager.isFavorite(this, streamerName)
        btnFavorite?.setImageResource(
            if (isFav) R.drawable.ic_star_filled else R.drawable.ic_star_outline
        )
    }

    // Improved immersive full screen
    fun toggleFullScreen() {
        val isLandscape = resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
        requestedOrientation = if (isLandscape) {
            android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer(url: String) {
        // Set up track selection to favor highest quality
        val trackSelectionFactory = DefaultTrackSelector.ParametersBuilder(this)
            .setForceLowestBitrate(false)
            .build()
        val trackSelector = DefaultTrackSelector(this)
        trackSelector.parameters = trackSelectionFactory

        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            
            val dataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            
            val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
                .setAllowChunklessPreparation(true) // Faster preparation
                .createMediaSource(MediaItem.fromUri(url))

            exoPlayer.setMediaSource(hlsMediaSource)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true

            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    binding.playerProgressBar.visibility = if (state == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onStop() {
        super.onStop()
        player?.stop()
    }
}
