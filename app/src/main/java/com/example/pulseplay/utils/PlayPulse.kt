package com.example.pulseplay.utils

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PlayPulse : Application(), DefaultLifecycleObserver {
}
