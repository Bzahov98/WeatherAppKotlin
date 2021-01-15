package com.bzahov.weatherapp.internal.enums

import com.bzahov.weatherapp.R

enum class RecyclerviewItemTypes(val typeID: Int, val resLayoutID: Int) {
	EMPTY_VIEW          (0, R.layout.recyclerview_row_empty_card),
	HEADER_VIEW         (1, R.layout.recyclerview_row_header),
	PER_THREE_HOURS_ITEM(2, R.layout.item_per_three_hours),
	FUTURE_WEATHER_ITEM (3, R.layout.item_future_weather)
}