package com.bzahov.weatherapp.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.provider.CUSTOM_LOCATION
import com.bzahov.weatherapp.data.provider.UNIT_SYSTEM
import com.bzahov.weatherapp.ui.base.ScopedPreferenceCompatFragment
import org.kodein.di.KodeinAware

import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
private const val TAG = "SettingsFragment"
class SettingsFragment : ScopedPreferenceCompatFragment(),
    SharedPreferences.OnSharedPreferenceChangeListener , KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: SettingsFragmentViewModelFactory by instance()
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

        return if(key == UNIT_SYSTEM){
            true
        }else super.onPreferenceTreeClick(preference)
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
        if(key == UNIT_SYSTEM){
//            if(isOnline()){}
            makeSelectablePreferences()

            Log.d(TAG,"notifyForUnitSystemChanged")
            viewModel.notifyForUnitSystemChanged()

        }
    }

    private fun makeSelectablePreferences() {
        val unitSystemPreference = preferenceManager.findPreference<ListPreference>(UNIT_SYSTEM)
        unitSystemPreference?.isSelectable = isOnline()
        val locationPreference =
            preferenceManager.findPreference<EditTextPreference>(CUSTOM_LOCATION)
       // unitSystemPreference?.isSelectable = isOnline()
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        // QUESTION: deprecated?
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}