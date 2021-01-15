package com.bzahov.weatherapp.internal

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bzahov.weatherapp.ui.activities.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

 fun Fragment.resetControlViews(showActionBar : Boolean = false) {
	if(showActionBar){
		this.getActionBar().show()
	}
	getBottomNavigationView().visibility = View.VISIBLE
}

 fun Fragment.getActionBar() = (requireActivity() as MainActivity).supportActionBar!!

 fun Fragment.getBottomNavigationView() = requireActivity().bottom_navigation

fun FragmentActivity.hideSupportActionBar() {
	val supportActionBar = (this as AppCompatActivity).supportActionBar
	if (supportActionBar != null && supportActionBar.isShowing) {
		supportActionBar.hide() // Hide action bar for that view
	}
}

fun Fragment.hideSupportActionBar() {
	val supportActionBar = (this.requireActivity() as AppCompatActivity).supportActionBar
	if (supportActionBar != null && supportActionBar.isShowing) {
		supportActionBar.hide() // Hide action bar for that view
	}
}
