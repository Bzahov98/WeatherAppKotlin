package com.bzahov.weatherapp.ui.animationUtils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.appcompat.app.ActionBar

class AnimationUtils {
    companion object {
        fun showHideViewAndActionBarWithAnimation(
            animatedView: View,
            visibility: Int,
            duration: Long = 222,
            delay: Long = 1111,
            actionBar: ActionBar
        ) {
            animatedView.animate()
                .alpha(0f)
                .setDuration(duration)
                .setStartDelay(delay)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationRepeat(animation: Animator?) {
                        super.onAnimationRepeat(animation)
//                            if (oneDayGroupData != null) {
//                                when (oneDayGroupData.visibility) {
//                                    View.GONE -> {
//                                        //oneDayGroupData.visibility = View.VISIBLE
//                                        animation?.removeAllListeners()
//                                    }
//                                    View.VISIBLE ->{
//                                        //oneDayGroupData.visibility = View.GONE
//                                        animation?.removeAllListeners()
//                                    }
//                                }
//                            }
                        animation?.cancel()
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        animatedView.visibility = visibility
                        if (visibility == View.GONE) {
                            actionBar.hide()
                        } else {
                            actionBar.show()
                        }
                    }
                })
        }
    }
}