package com.bzahov.weatherapp.data.provider

import android.content.Context
import android.util.Log
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.internal.enums.UnitSystem

const val UNIT_SYSTEM = "UNIT_SYSTEM"
const val TAG = "UnitProviderImpl"

class UnitProviderImpl(context: Context) : PreferenceProvider(context),UnitProvider {
    private var isUnitSystemChanged = false


    override fun getUnitSystem(): UnitSystem {
        val selectedName = preferences.getString(UNIT_SYSTEM, UnitSystem.METRIC.name)
        return UnitSystem.valueOf(selectedName!!)
    }

    override fun hasUnitSystemChanged(): Boolean {
        Log.d(TAG,"\nhasUnitSystemChanged $isUnitSystemChanged")
        return isUnitSystemChanged
    }

    override fun notifyUnitSystemChanged() {
        isUnitSystemChanged = true
    }
    override fun notifyNoNeedToChangeUnitSystem() {
        isUnitSystemChanged = false
    }
}