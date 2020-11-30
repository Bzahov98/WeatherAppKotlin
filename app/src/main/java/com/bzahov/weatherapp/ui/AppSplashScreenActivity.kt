package com.bzahov.weatherapp.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.internal.DelayTaskUtils
import com.bzahov.weatherapp.internal.IntentUtils

class AppSplashScreenActivity : AppCompatActivity() {
	private val TAG = "AppSplashScreenActivity"
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_app_splash_screen)
	}

	override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
		val v = super.onCreateView(name, context, attrs)
		return v
	}

	override fun onStart() {
		super.onStart()
		val duration : Long = calculateAnimationDuration()
		val a = DelayTaskUtils.afterMillisOnMain(duration, this) {
			Log.d(TAG, "afterMillisOnMain:, before start ")
			IntentUtils.startMainActivity(this)
			Log.d(TAG, "afterMillisOnMain: after start")
			this.finish()
		}
	}

	private fun calculateAnimationDuration() =
		(resources.getInteger(R.integer.anim_splash_screen_duration) + resources.getInteger(R.integer.anim_splash_screen_duration_wait)).toLong()

	override fun onWindowFocusChanged(hasFocus: Boolean) {
		super.onWindowFocusChanged(hasFocus)
		if (hasFocus) {
			hideSystemUIAndNavigation(this)
		}
	}

	private fun hideSystemUIAndNavigation(activity: Activity) {
		val decorView: View = activity.window.decorView
		decorView.systemUiVisibility =
			(View.SYSTEM_UI_FLAG_IMMERSIVE
					// Set the content to appear under the system bars so that the
					// content doesn't resize when the system bars hide and show.
					or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
					or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					or View.SYSTEM_UI_FLAG_FULLSCREEN)
	}

}