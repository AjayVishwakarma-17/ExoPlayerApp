package com.example.demoappjio

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.MediaController
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import com.example.demoappjio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MediaController.MediaPlayerControl {
    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    private val playbackStateListener: Player.Listener = playbackStateListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        // auto play from the last index where it stopped
        savedInstanceState?.getInt("mediaItem")?.let { media->
            val seekTime = savedInstanceState.getLong("seekTime")
            player!!.seekTo(media,seekTime)
            player!!.play()
        }
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer()
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        outState.putLong("seekTime", player!!.currentPosition)
        outState.putInt("mediaItem", player!!.currentMediaItemIndex)
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                viewBinding.videoView.player = exoPlayer
                val mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4))
                exoPlayer.setMediaItem(mediaItem)

                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentItem, playbackPosition)
                exoPlayer.addListener(playbackStateListener)
                exoPlayer.prepare()
            }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, viewBinding.videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE -"
            }
            Log.d("Player", "changed state to $stateString")
            Toast.makeText(this@MainActivity,stateString, Toast.LENGTH_LONG).show()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        player!!.playWhenReady = false
        Toast.makeText(this@MainActivity,"Paused", Toast.LENGTH_LONG).show()
    }

    override fun getDuration(): Int {
        TODO("Not yet implemented")
    }

    override fun getCurrentPosition(): Int {
        TODO("Not yet implemented")
    }

    override fun seekTo(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun isPlaying(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBufferPercentage(): Int {
        TODO("Not yet implemented")
    }

    override fun canPause(): Boolean {
        TODO("Not yet implemented")
    }

    override fun canSeekBackward(): Boolean {
        TODO("Not yet implemented")
    }

    override fun canSeekForward(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAudioSessionId(): Int {
        TODO("Not yet implemented")
    }
}