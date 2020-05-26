package com.bzahov.weatherapp.data.provider

import android.content.Context
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.internal.enums.UnitSystem

const val UNIT_SYSTEM = "UNIT_SYSTEM"
const val TAG = "UnitProviderImpl"

class UnitProviderImpl(context: Context) : PreferenceProvider(context),UnitProvider {

      override fun getUnitSystem(): UnitSystem {
        val selectedName = preferences.getString(UNIT_SYSTEM, UnitSystem.METRIC.name)
        return UnitSystem.valueOf(selectedName!!)
    }


}