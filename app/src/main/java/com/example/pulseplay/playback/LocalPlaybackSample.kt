package com.example.pulseplay.playback

import android.content.Context
import android.net.Uri
import com.example.pulseplay.R

/**
 * Ten bundled demo MP3s: `res/raw/demo_track_1` … `demo_track_10`.
 * [shuffledDemoUris] returns those resources in a **random order** each call; assign to list
 * positions with `uriPool[index % uriPool.size]` so long lists cycle through the shuffled pool.
 */
object LocalPlaybackSample {

    private val demoTrackResIds: List<Int> = listOf(
        R.raw.demo_track_1,
        R.raw.demo_track_2,
        R.raw.demo_track_3,
        R.raw.demo_track_4,
        R.raw.demo_track_5,
        R.raw.demo_track_6,
        R.raw.demo_track_7,
        R.raw.demo_track_8,
        R.raw.demo_track_9,
        R.raw.demo_track_10,
    )

    fun shuffledDemoUris(context: Context): List<String> =
        demoTrackResIds.shuffled().map { resId ->
            Uri.parse("android.resource://${context.packageName}/$resId").toString()
        }
}
