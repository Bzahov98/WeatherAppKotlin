package com.bzahov.weatherapp.ui.weather.future.list.recyclerview

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.afollestad.materialdialogs.utils.MDUtil.inflate
import com.bzahov.weatherapp.internal.enums.RecyclerviewItemTypes
import com.bzahov.weatherapp.internal.enums.RecyclerviewItemTypes.FUTURE_WEATHER_ITEM
import com.bzahov.weatherapp.ui.weather.future.list.FutureListWeatherFragmentDirections
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class FutureWeatherRecyclerAdapter
	: GroupAdapter<FutureViewHolder>() {

	private val TAG = "FutureWeatherRecyclerAdapter"

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FutureViewHolder {

		return inflateViewHolder(parent, FUTURE_WEATHER_ITEM)
	}

	private fun inflateViewHolder(
		parent: ViewGroup,
		res: RecyclerviewItemTypes
	): FutureViewHolder {
		return FutureViewHolder(
			parent.inflate(
				parent.context,
				res.resLayoutID
			),
			res
		)
	}

	override fun onBindViewHolder(holder: FutureViewHolder, position: Int) = Unit
	override fun getItemViewType(position: Int): Int {
		return /*if (withHeader && position == 0) EMPTY_VIEW.typeID else*/ FUTURE_WEATHER_ITEM.typeID
	}
}

class FutureViewHolder(itemView: View, val type: RecyclerviewItemTypes) : ViewHolder(itemView) {
	private val TAG = "FutureViewHolder"
	init {
		itemView.setOnClickListener {

			val itemDetail = (item as FutureWeatherItem).weatherEntry
			Toast.makeText(
				it.context,
				"Clicked: ${itemDetail.dtTxt}",
				Toast.LENGTH_SHORT
			).show()
			Log.e(TAG, "GroupAdapter.setOnItemClickListener ${itemDetail}\n")
			showWeatherDetail(itemDetail.dtTxt, it)
		}
	}

	private fun showWeatherDetail(string: String, view: View) {
		// TODO
		//resetControlViews()
		val actionShowDetail = FutureListWeatherFragmentDirections.actionShowDetail(string)
		Navigation.findNavController(view).navigate(actionShowDetail)
	}
}

//private fun ViewGroup.inflate(@LayoutRes layout: Int, attach: Boolean = false) =
//	LayoutInflater.from(context).inflate(layout, this, attach)
////class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
// usage:
//OneDayViewHolder(parent.inflate( R.layout.recyclerview_row_header))

