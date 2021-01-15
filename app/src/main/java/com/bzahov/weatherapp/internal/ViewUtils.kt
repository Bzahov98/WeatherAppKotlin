package com.bzahov.weatherapp.internal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar


// ProgressBar
fun ProgressBar.show() {
	visibility = View.VISIBLE
}

fun ProgressBar.hide() {
	visibility = View.GONE
}
// View
fun View.show() {
	visibility = View.VISIBLE
}

fun View.gone() {
	visibility = View.GONE
}

fun View.hide() {
	visibility = View.INVISIBLE
}

fun View.snackbar(message: String, length: Int = Snackbar.LENGTH_SHORT) {
	Snackbar.make(this, message, length).also { snackbar ->
		snackbar.setAction("Ok") {
			snackbar.dismiss()
		}
	}.show()
}

fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
	Toast.makeText(this, message, length).show()
}

fun hideSoftKeyboard(activity: Activity) {
	val inputMethodManager =
		activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
	inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
}

fun View.goneAnimated(){
	this.animate()
		.alpha(1.0f)
		.setDuration(150)
		.setListener(object : AnimatorListenerAdapter() {
			override fun onAnimationEnd(animation: Animator?) {
				super.onAnimationEnd(animation)
				this@goneAnimated.gone()
				this@goneAnimated.alpha = 0.0f
			}
		})
}

fun View.showAnimated(){
	this.animate()
		.alpha(1.0f)
		.setDuration(50)
		.setListener(object : AnimatorListenerAdapter() {
			override fun onAnimationEnd(animation: Animator?) {
				super.onAnimationEnd(animation)
				this@showAnimated.show()
			}
		})
}