package com.bzahov.weatherapp.internal

import android.app.Activity
import kotlinx.coroutines.*

class DelayTaskUtils {
	companion object {
		fun afterMillisOnMain(delayMillis: Long, activity: Activity, process: () -> Unit) {

			GlobalScope.launch(context = Dispatchers.Main) {
				delay(delayMillis)
				process.invoke()
				this.cancel()
			}
		}
	}
}