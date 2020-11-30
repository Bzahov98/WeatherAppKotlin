package com.bzahov.weatherapp.ui.base.states

class EmptyState() : AbstractErrorState() {
	private val TAG = "EmptyState"
//	val emptyItemList: List<T>
//		get() {
//			val emptyHourInfo = T()
//			return arrayListOf<T>(
//				emptyHourInfo,
//				emptyHourInfo,
//				emptyHourInfo,
//				emptyHourInfo,
//				emptyHourInfo,
//				emptyHourInfo
//			)
//		}


	override var errorString = super.errorString + "!!!"
	var warningString = "Please check your internet connection and app permissions"
	val errorIconID: Int = com.bzahov.weatherapp.R.drawable.icons8_no_data_48

	@Deprecated("Not usable method!!!")
	override fun calculateData() {
//		Log.e(TAG,)
//		Int
	}

	companion object{
		fun <S>getEmptyItem(emptyHourInfo: S) : List<S>{
			return arrayListOf<S>(
				emptyHourInfo,
				emptyHourInfo,
				emptyHourInfo,
				emptyHourInfo,
				emptyHourInfo,
				emptyHourInfo
			)
		}
	}
}