package com.bzahov.weatherapp.ui.weather.future.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.LocalDateConverter
import com.bzahov.weatherapp.internal.UIUpdateViewUtils
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarTitle
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateIcon
import com.bzahov.weatherapp.internal.exceptions.DateNotFoundException
import com.bzahov.weatherapp.internal.glide.GlideApp
import com.bzahov.weatherapp.internal.gone
import com.bzahov.weatherapp.internal.hideSupportActionBar
import com.bzahov.weatherapp.ui.base.ScopedFragment
import com.bzahov.weatherapp.ui.base.states.EmptyState
import kotlinx.android.synthetic.main.future_detail_weather_fragment.*
import kotlinx.android.synthetic.main.future_detail_weather_fragment.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import java.time.LocalDateTime

@SuppressLint("SetTextI18n")
class FutureDetailWeatherFragment : ScopedFragment(), KodeinAware {
	private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
	private val TAG = "FutureDetailWeatherFragment"
	override val kodein by closestKodein()

	private val viewModelFactoryInstanceFactory: ((LocalDateTime) -> FutureDetailWeatherViewModelFactory)
			by factory<LocalDateTime, FutureDetailWeatherViewModelFactory>()

	private lateinit var viewModel: FutureDetailWeatherViewModel

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.future_detail_weather_fragment, container, false)
		mSwipeRefreshLayout = view.futureDetailWeatherFragmentSwipe
		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initRefresherLayout()
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		val safeArgs = arguments?.let { FutureDetailWeatherFragmentArgs.fromBundle(it) }
		val date = LocalDateConverter().stringToDateTime(safeArgs?.dateString)
			?: throw DateNotFoundException()

		Log.e(TAG, "received ${safeArgs?.dateString}safeargs: $date")

		viewModel = ViewModelProvider(this, viewModelFactoryInstanceFactory(date))
			.get(FutureDetailWeatherViewModel::class.java)
		bindUI()
	}

	override fun onStart() {
		super.onStart()
		this.hideSupportActionBar()
	}

	override fun onResume() {
		super.onResume()
		this.hideSupportActionBar()
	}

	private fun bindUI(): Job {
		return launch {
			viewModel.getDetailData()
			val futureDetailWeatherLiveData = viewModel.uiViewsState
			val weatherLocation = viewModel.weatherLocation.await()
			// REWORK Return City object temporary FiX for: futureDetail location isn't changed and stay with name futureDetail_weather_fragment all city attributes are null

			weatherLocation.observe(viewLifecycleOwner, Observer { location ->
				if (location == null) return@Observer
				Log.e(TAG, "Location : $location")
				updateActionBarTitle(location.name, requireActivity())
				Log.d(TAG, "Update location with that data: $location")
			})

			futureDetailWeatherLiveData.observe(viewLifecycleOwner, Observer {
				if (it == null) return@Observer

				when (it) {
					is FutureDetailState -> {
						Log.d(TAG, "Update UI with that data: $it")
						updateUI(it)
					}
					is EmptyState -> {
						updateEmptyStateUI(it)
						Log.e(TAG, "Update UI with EMPTY STATE $it")
					}
					else -> {
						Log.e(TAG, "Found UNKNOWN STATE $it")
					}
				}
			})
		}
	}

	private fun updateEmptyStateUI(it: EmptyState) {
		updateCondition(it.warningString)
		updateActionBarSubtitle(it.errorString)
		updatePrecipitation(it.errorString)
		updateTemperatures(it.errorString, it.errorString)
		futureDetailWind.text = it.errorString
		updateVisibility(it.errorString)

		GlideApp.with(this)
			.load(it.errorIconID)
			.into(futureDetailIConditionIcon)
	}

	private fun updateUI(it: FutureDetailState) {
		Log.d(TAG, "UpdateUI for FutureDayData with: \n $it \n")
		mSwipeRefreshLayout.isRefreshing = false
		futureDetailGroupLoading.gone()
		updateCondition(it.weatherConditionText)
		updateActionBarSubtitle(it.detailSubtitle)
		updatePrecipitation(it.rainPrecipitationText)
		updateTemperatures(it.detailTemperatureText, it.detailFeelsLikeTemperatureText)
		futureDetailWind.text = it.detailWindText
		updateVisibility(it.detailVisibilityText)
		updateBackground(it.isDay)
		updateIcon(it.iconNumber, futureDetailIConditionIcon)
	}

	private fun initRefresherLayout() {

		mSwipeRefreshLayout.isRefreshing = true
		mSwipeRefreshLayout.setOnRefreshListener {
			mSwipeRefreshLayout.isRefreshing = false
			if (viewModel.isOnline()) {
				Log.d(TAG, getAppString(R.string.snackbar_updation_weather_data))
				UIUpdateViewUtils.showSnackBarMessage(
					getAppString(R.string.snackbar_updation_weather_data),
					requireActivity()
				)
				launch { viewModel.requestRefreshOfData() }
			} else {
				Log.e(TAG, ForecastApplication.getAppString(R.string.warning_device_offline))
				UIUpdateViewUtils.showSnackBarMessage(
					ForecastApplication.getAppString(R.string.warning_device_offline),
					requireActivity(),
					false
				)
			}
		}
	}

	private fun updateActionBarSubtitle(subtitle: String) {

		(activity as? AppCompatActivity)?.supportActionBar?.subtitle = subtitle
	}

	// Rework change background with proper color to match to all pictures background
	private fun updateBackground(isDay: Boolean) {
//        if (isDay) {
//            futureDetailWeatherFragment.setBackgroundColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.colorWeatherIconBackgroundDay
//                )
//            )
//        } else {
//            futureDetailWeatherFragment.setBackgroundColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.colorWeatherIconBackgroundNight
//                )
//            )
//        }
	}

	private fun updateTemperatures(tempText: String, feelsLikeText: String) {
		futureDetailTextTemperature.text = tempText
		futureDetailFeelsLikeTemperature.text = feelsLikeText
	}

	private fun updateCondition(condition: String) {
		futureDetailTextCondition.text = condition
	}

	private fun updatePrecipitation(precipitationText: String) {
		futureDetailRainPrecipitation.text = precipitationText
	}

	private fun updateVisibility(visibilityDistanceText: String) {
		futureDetailVisibility.text = visibilityDistanceText
	}

	private suspend fun refreshWeather() {
		viewModel.requestRefreshOfData()
	}
}
