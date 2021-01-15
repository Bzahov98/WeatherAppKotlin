package com.bzahov.weatherapp.ui.weather.future.list


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.internal.*
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarSubtitle
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarSubtitleWithResource
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarTitle
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsFactory
import com.bzahov.weatherapp.ui.anychartGraphs.specificUtils.FutureWeatherChartUtils.Companion.createChart
import com.bzahov.weatherapp.ui.anychartGraphs.specificUtils.FutureWeatherChartUtils.Companion.createPrecipitationsChart
import com.bzahov.weatherapp.ui.anychartGraphs.specificUtils.FutureWeatherChartUtils.Companion.createTemperatureChart
import com.bzahov.weatherapp.ui.anychartGraphs.specificUtils.FutureWeatherChartUtils.Companion.createWindChart
import com.bzahov.weatherapp.ui.base.ScopedFragment
import com.bzahov.weatherapp.ui.base.states.EmptyState
import com.bzahov.weatherapp.ui.weather.future.list.recyclerview.FutureWeatherItem
import com.bzahov.weatherapp.ui.weather.future.list.recyclerview.FutureWeatherRecyclerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.future_list_weather_fragment.*
import kotlinx.android.synthetic.main.future_list_weather_fragment.view.*
import kotlinx.android.synthetic.main.item_one_day.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class FutureListWeatherFragment : ScopedFragment(), KodeinAware, View.OnClickListener {

	private val TAG = "FutureDetailWeatherFragment"
	override val kodein by closestKodein()
	private val viewModelFactory: FutureListWeatherViewModelFactory by instance<FutureListWeatherViewModelFactory>()
	private lateinit var viewModel: FutureListWeatherViewModel
	private lateinit var groupAdapter: FutureWeatherRecyclerAdapter
	private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
	private lateinit var recyclerView: RecyclerView
	private var lastOrientation: Int = 0
	private var currentStateData: FutureListState? = null


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.future_list_weather_fragment, container, false)
		mSwipeRefreshLayout = view.futureListWeatherFragmentSwipe
		lastOrientation = resources.configuration.orientation
		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initRefresherLayout()

		futureChartTemperature?.setOnClickListener(this);
		futureChartPrecipitation?.setOnClickListener(this);
		futureChartWind?.setOnClickListener(this);
		futureChartText?.setOnClickListener(this);
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		initViewModel()

		initRecyclerView()

		updateEmptyStateUI(EmptyState())

		bindUI()

		resetControlViews()
	}
	override fun onStart() {
		super.onStart()
		this.hideSupportActionBar()
	}

	override fun onResume() {
		super.onResume()
		this.hideSupportActionBar()
		lastOrientation = resources.configuration.orientation
		bindUI()
	}

	override fun onStop() {
		super.onStop()
		resetControlViews()
	}

	override fun onClick(view: View) {
		when (view.id) {
			futureChartText?.id -> {
				Log.d(TAG, "futureChartText")
				showWeatherDialog()
			}
			futureChartTemperature?.id -> {
				Log.d(TAG, "futureChartTemperature")
				showWeatherTemperatureDialog()
			}
			futureChartPrecipitation?.id -> {
				Log.d(TAG, "futureChartPrecipitation")
				showWeatherPrecipitationDialog()
			}
			futureChartWind?.id -> {
				Log.d(TAG, "futureChartWind")
				showWeatherWindDialog()
			}
		}
	}

//	private fun showWeatherDialog() {
//		AnyChartGraphsFactory.showDialog(
//			requireContext(),
//			R.layout.activity_graph_test,
//			R.id.any_chart_view,
//			currentStateData
//		) { OneDayChartUtils.createChart(currentStateData) }
//	}

	private fun initViewModel() {
		viewModel = ViewModelProvider(this, viewModelFactory)
			.get(FutureListWeatherViewModel::class.java)
	}

	private fun bindUI(): Job {
		//Log.d(TAG, "bindUI")
		return launch {
			viewModel.getFutureListData()
			val futureWeatherLiveData = viewModel.uiViewsState
			val weatherLocation = viewModel.weatherLocation.await()

			Log.d(TAG, "buildUi $futureWeatherLiveData")
			weatherLocation.observe(viewLifecycleOwner, Observer { location ->
				if (location == null) return@Observer
				updateActionBarTitle(location.name, requireActivity())
				Log.d(TAG, "bindUI Update location with that data: $location")
			})

			futureWeatherLiveData.observe(viewLifecycleOwner, Observer {
				Log.d(TAG, "UpdateUI for List<FutureDayData> with: \n ${it ?: "null"} \n")
				if (it == null) {
					Log.e(TAG, "DATA IS EMPTY TRY TO FETCH AGAIN")
					launch {
						viewModel.requestRefreshOfData()
					}
					return@Observer
				} else {
					when (it) {
						null -> {

							Log.e(TAG, "State is null, Update UI with EMPTY STATE $it")
							currentStateData = null
							updateEmptyStateUI(it)
						}

						is FutureListState -> {
							Log.d(TAG, "Update UI with that data: $it")
							updateUI(it)
							currentStateData = it
							updateRecyclerViewData(it.weatherItems)
						}
						is EmptyState -> {
							currentStateData = null
							updateEmptyStateUI(it)
							Log.e(TAG, "Update UI with EMPTY STATE $it")
						}
						else -> {
							Log.e(TAG, "Found UNKNOWN STATE $it")
							updateEmptyStateUI(null)
						}
					}
				}
			})
		}
	}

	private fun updateEmptyStateUI(it: EmptyState?) {
		updateActionBarTitle("Loading...", requireActivity())

		var warningString = getAppString(R.string.error_no_data_warning)
		var errorString = getAppString(R.string.error_no_data_warning)

		if (it != null) {
			warningString = it.warningString
			errorString = it.errorString
		}

		updateActionBarTitle("Loading...", requireActivity())
		updateActionBarSubtitle(
			warningString,
			requireActivity()
		)
		updateRecyclerViewData(EmptyState.getEmptyItem(FutureWeatherItem()))
	}

	private fun updateUI(it: FutureListState) {
		futureGroupLoading.gone()
		mSwipeRefreshLayout.isRefreshing = false
		updateActionBarSubtitleWithResource(
			R.string.future_weather_five_days_next,
			requireActivity()
		)
	}

	private fun initRecyclerView() {
		groupAdapter = FutureWeatherRecyclerAdapter()
		recyclerView = futureRecyclerView.apply {
			layoutManager = LinearLayoutManager(this@FutureListWeatherFragment.context)
			adapter = groupAdapter
		}

		addRecyclerShowHideNavigationListener()
	}

	private fun addRecyclerShowHideNavigationListener() {
		recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				if (dy > 0 && getBottomNavigationView().isShown) {
					getBottomNavigationView()?.gone()//Animated()
					futureChartChipGroup?.gone()//Animated()
					futureChartDescriptionView?.gone()//Animated()
				} else if (dy < 0) {
					getBottomNavigationView()?.show()//Animated()
					futureChartChipGroup?.show()//Animated()
					futureChartDescriptionView?.show()//Animated()
				}
			}

			override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
				super.onScrollStateChanged(recyclerView, newState)
				//                if (lastOrientation == Configuration.ORIENTATION_LANDSCAPE) {
				//                    if ((!recyclerView.canScrollVertically(1) || !recyclerView.canScrollHorizontally(
				//                            -1
				//                        )) &&/*newState == RecyclerView.SCROLL_STATE_SETTLING || */newState == RecyclerView.SCROLL_STATE_IDLE
				//                    ) {
				//                        //oneDayGroupData.clearAnimation()
				//                        if (!getActionBar().isShowing) {
				//                            showHideViewAndActionBarWithAnimation(
				//                                oneDayGroupData, View.VISIBLE,
				//                                actionBar = getActionBar()
				//                            )
				//                        }
				//                    } else {
				//                        //oneDayGroupData.clearAnimation()
				//                        if (getActionBar().isShowing) {
				//                            showHideViewAndActionBarWithAnimation(
				//                                oneDayGroupData,
				//                                View.GONE,
				//                                1111,
				//                                222,
				//                                getActionBar()
				//                            )
				//
				//                        }
				//                    }
				//                }
			}
		})
	}

	private fun initRefresherLayout() {
		mSwipeRefreshLayout.isRefreshing = true
		mSwipeRefreshLayout.setOnRefreshListener {
			mSwipeRefreshLayout.isRefreshing = false
			if (viewModel.isOnline()) {
				Log.d(TAG, getAppString(R.string.loading_updating_data))
				UIUpdateViewUtils.showSnackBarMessage(
					getAppString(R.string.loading_updating_data),
					requireActivity()
				)
				launch { viewModel.requestRefreshOfData() }
			} else {
				Log.e(TAG, getAppString(R.string.warning_device_offline))
				UIUpdateViewUtils.showSnackBarMessage(
					getAppString(R.string.warning_device_offline),
					requireActivity(),
					false
				)
			}
		}
	}

	private fun updateRecyclerViewData(weatherItems: List<FutureWeatherItem>) {
		groupAdapter.apply {
			clear()
			groupAdapter.addAll(weatherItems)
			notifyDataSetChanged()
		}

		recyclerView.apply {
			animation?.cancel()
			scheduleLayoutAnimation()
		}
	}

	private fun showWeatherDialog() {
		AnyChartGraphsFactory.showDialog(
			requireContext(),
			R.layout.activity_graph_test,
			R.id.any_chart_view,
			currentStateData
		) { createChart(currentStateData) }
	}

	private fun showWeatherTemperatureDialog() {
		AnyChartGraphsFactory.showDialog(
			requireContext(),
			R.layout.activity_graph_test,
			R.id.any_chart_view,
			currentStateData
		) { createTemperatureChart(currentStateData) }
	}

	private fun showWeatherPrecipitationDialog() {
		AnyChartGraphsFactory.showDialog(
			requireContext(),
			R.layout.activity_graph_test,
			R.id.any_chart_view,
			currentStateData
		) { createPrecipitationsChart(currentStateData) }
	}

	private fun showWeatherWindDialog() {
		AnyChartGraphsFactory.showDialog(
			requireContext(),
			R.layout.activity_graph_test,
			R.id.any_chart_view,
			currentStateData
		) { createWindChart(currentStateData) }
	}
}
