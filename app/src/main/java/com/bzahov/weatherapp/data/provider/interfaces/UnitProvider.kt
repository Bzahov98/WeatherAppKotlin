package com.bzahov.weatherapp.data.provider.interfaces

import com.bzahov.weatherapp.internal.enums.UnitSystem

interface UnitProvider {
    fun getUnitSystem(): UnitSystem
}
