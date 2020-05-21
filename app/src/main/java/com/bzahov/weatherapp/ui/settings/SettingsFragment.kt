package com.bzahov.weatherapp.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.provider.CUSTOM_LOCATION
import com.bzahov.weatherapp.data.provider.UNIT_SYSTEM
import com.bzahov.weatherapp.data.provider.USE_DEVICE_LOCATION
import com.bzahov.weatherapp.internal.OtherUtils.Companion.isOnline
import com.bzahov.weatherapp.ui.base.ScopedPreferenceCompatFragment
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

private const val TAG = "SettingsFragment"

class SettingsFragment : ScopedPreferenceCompatFragment(),
    SharedPreferences.OnSharedPreferenceChangeListener, KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: SettingsFragmentViewModelFactory by instance<SettingsFragmentViewModelFactory>()
    private lateinit var viewModel: SettingsFragmentViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        makeSelectablePreferences()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Setting"
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = null
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(SettingsFragmentViewModel::class.java)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val key = preference?.key ?: return super.onPreferenceTreeClick(preference)

        return if (key == UNIT_SYSTEM) {
            true
        } else super.onPreferenceTreeClick(preference)
    }

    override fun onResume() {
        super.onResume()
        makeSelectablePreferences()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    // TODO: check is there connection and forbid changing UnitSystem when there is no internet
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == UNIT_SYSTEM) {
            makeSelectablePreferences()
            //if(viewModel.isMetric && sharedPreferences.get)
            Log.d(TAG, "notifyForUnitSystemChanged")
            //viewModel.notifyForUnitSystemChanged()
            launch {
                viewModel.requestRefreshOfData();
            }
        }
        if (key == CUSTOM_LOCATION || key == USE_DEVICE_LOCATION) {
            launch {
                viewModel.requestRefreshOfData();
            }
        }
    }

    private fun makeSelectablePreferences() {
        val unitSystemPreference = preferenceManager.findPreference<ListPreference>(UNIT_SYSTEM)
        unitSystemPreference?.isSelectable = isOnline(requireContext())
        val locationPreference =
            preferenceManager.findPreference<EditTextPreference>(CUSTOM_LOCATION)
        locationPreference?.isSelectable = isOnline(requireContext())
        val currentLocationPreference =
            preferenceManager.findPreference<SwitchPreference>(USE_DEVICE_LOCATION)
        currentLocationPreference?.isSelectable = isOnline(requireContext())
    }
}