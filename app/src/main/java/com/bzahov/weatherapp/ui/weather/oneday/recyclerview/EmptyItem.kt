package com.bzahov.weatherapp.ui.weather.oneday.recyclerview

import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.internal.hide
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.item_per_three_hours.view.*

public class EmptyItem(
) : Item<OneDayViewHolder>() {
	override fun bind(viewHolder: OneDayViewHolder, position: Int) {
		viewHolder.apply {
			val view = viewHolder.itemView
			view.perThreeHoursDataView.hide()
		}
	}
	override fun getLayout() = R.layout.item_per_three_hours

}