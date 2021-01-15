package com.bzahov.weatherapp.ui.animationUtils

import android.animation.ObjectAnimator
import android.view.View
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.R

class ObjectAnimatorUtils {
	companion object{

		fun startFadeAnimationToView(view: View) : ObjectAnimator{
			var animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0.4f)
			animator?.repeatMode = ObjectAnimator.REVERSE
			animator?.repeatCount = ObjectAnimator.INFINITE
			animator?.setAutoCancel(true)
			animator?.duration = getFadeAnimationDuration()
			animator?.start()
			return animator
		}

		fun setAlphaToViewToNormal(view: View) {
			val doNothing = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 1f)
			doNothing.apply {
				repeatCount = 1
				setAutoCancel(true)
				start()
			}
		}

		private fun getFadeAnimationDuration() =
			ForecastApplication.getAppResources()!!.getInteger(R.integer.anim_recycler_empty_duration).toLong()
	}
}