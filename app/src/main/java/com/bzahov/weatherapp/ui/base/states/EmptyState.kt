package com.bzahov.weatherapp.ui.base.states

class EmptyState() : AbstractErrorState() {
    override var errorString = super.errorString + "!!!"
    var warningString = "Please check your internet connection and app permissions"
    val errorIconID: Int = com.bzahov.weatherapp.R.drawable.icons8_no_data_48

    override fun calculateData() {
        TODO("Not yet implemented")
    }
}