package com.bzahov.weatherapp.ui.weather.oneday.recyclerview

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.afollestad.materialdialogs.utils.MDUtil.inflate
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.provider.TAG
import com.bzahov.weatherapp.internal.enums.RecyclerviewItemTypes
import com.bzahov.weatherapp.internal.enums.RecyclerviewItemTypes.PER_THREE_HOURS_ITEM
import com.bzahov.weatherapp.ui.weather.oneday.OneDayWeatherFragmentDirections
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class OneDayRecyclerAdapter(
	private val withHeader: Boolean = false
) : GroupAdapter<OneDayViewHolder>() {
	//
//	override fun getItemCount() = 64
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OneDayViewHolder {
		val res1 = R.layout.item_per_three_hours
//		return when (viewType) {
//			HEADER_VIEW.typeID -> {
//				inflateViewHolder(parent, HEADER_VIEW)
//			}
//			EMPTY_VIEW.typeID -> {
//				inflateViewHolder(parent, EMPTY_VIEW)
//			}
//			PER_THREE_HOURS_VIEW.typeID -> {
			return	inflateViewHolder(parent, PER_THREE_HOURS_ITEM)
//			}
//			else -> { // EMPTY_VIEW
//				inflateViewHolder(parent, EMPTY_VIEW)
//			}
//		}
//
	}

	private fun inflateViewHolder(
		parent: ViewGroup,
		res: RecyclerviewItemTypes
	): OneDayViewHolder {
		return OneDayViewHolder(
			parent.inflate(
				parent.context,
				res.resLayoutID
			),
			res
		)
	}

	override fun onBindViewHolder(holder: OneDayViewHolder, position: Int) = Unit
	override fun getItemViewType(position: Int): Int {
		return /*if (withHeader && position == 0) EMPTY_VIEW.typeID else*/ PER_THREE_HOURS_ITEM.typeID
	}
}

class OneDayViewHolder(itemView: View, val type: RecyclerviewItemTypes) : ViewHolder(itemView) {

	init {
//		if (item is HourInfoItem) {
			itemView.setOnClickListener {

				val itemDetail = (item as HourInfoItem).weatherEntry
				Toast.makeText(
					it.context,
					"Clicked: ${itemDetail.dtTxt}",
					Toast.LENGTH_SHORT
				).show()
				Log.e(TAG, "GroupAdapter.setOnItemClickListener ${itemDetail}\n")
				showHourInfoDetails(itemDetail.dtTxt, it)
			}
//		} else if(item is EmptyItem){
//			val animator = ObjectAnimator.ofFloat(itemView, View.ALPHA, 0f)
//			animator.repeatCount = 1
//			animator.repeatMode = ObjectAnimator.REVERSE
//			animator.start()
//		}
	}

	private fun showHourInfoDetails(dtTxt: String, view: View) {
		val actionShowDetail =
			OneDayWeatherFragmentDirections.actionShowDetail(dtTxt)

		Navigation.findNavController(view).navigate(actionShowDetail)
	}
}

//private fun ViewGroup.inflate(@LayoutRes layout: Int, attach: Boolean = false) =
//	LayoutInflater.from(context).inflate(layout, this, attach)
////class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
// usage:
//OneDayViewHolder(parent.inflate( R.layout.recyclerview_row_header))

