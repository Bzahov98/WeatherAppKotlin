package com.bzahov.weatherapp.internal

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.bzahov.weatherapp.ui.activities.MainActivity

class IntentUtils {
	companion object {
		fun startMainActivity(context: Context) {
			Intent(context, MainActivity::class.java).also {
				//it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
				context.toast("Start Activity")
				ContextCompat.startActivity(context, it, it.extras)
				//finish()
			}
		}


	}
}