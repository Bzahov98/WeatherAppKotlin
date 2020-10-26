package com.bzahov.weatherapp.ui.weather.oneday

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.internal.UIUpdateViewUtils
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarTitle
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateIcon
import com.bzahov.weatherapp.ui.MainActivity
import com.bzahov.weatherapp.ui.animationUtils.AnimationUtils.Companion.showHideViewAndActionBarWithAnimation
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsFactory.Companion.showDialog
import com.bzahov.weatherapp.ui.anychartGraphs.specificUtils.OneDayChartUtils.Companion.createChart
import com.bzahov.weatherapp.ui.anychartGraphs.specificUtils.OneDayChartUtils.Companion.createPrecipitationsChart
import com.bzahov.weatherapp.ui.anychartGraphs.specificUtils.OneDayChartUtils.Companion.createTemperatureChart
import com.bzahov.weatherapp.ui.anychartGraphs.specificUtils.OneDayChartUtils.Companion.createWindChart
import com.bzahov.weatherapp.ui.base.ScopedFragment
import com.bzahov.weatherapp.ui.base.states.EmptyState
import com.bzahov.weatherapp.ui.weather.oneday.recyclerview.HourInfoItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_one_day.*
import kotlinx.android.synthetic.main.item_one_day.view.*
import kotlinx.android.synthetic.main.layout_day_night_description_view.view.*
import kotlinx.android.synthetic.main.layout_temperature_view.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class OneDayWeatherFragment : ScopedFragment(), KodeinAware, View.OnClickListener {


    private var currentStateData: OneDayWeatherState? = null
    private val TAG = "OneDayWeatherFragment"

    override val kodein by closestKodein()
    private lateinit var viewModel: OneDayWeatherViewModel

    private val viewModelFactory: OneDayWeatherViewModelFactory by instance<OneDayWeatherViewModelFactory>()
    private lateinit var groupAdapter: GroupAdapter<ViewHolder>
    private lateinit var recyclerView: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private var lastOrientation: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.item_one_day, container, false)
        mSwipeRefreshLayout = view.oneDayWeatherFragmentSwipe
        lastOrientation = resources.configuration.orientation;

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRefresherLayout()
        // TODO add listeners when add views in landscape layout
        oneDayPerHourChartTemperature.setOnClickListener(this);
        oneDayPerHourChartPrecipitation.setOnClickListener(this);
        oneDayPerHourChartWind?.setOnClickListener(this); // TODO ADD to landscape view
        oneDayPerHourChartText.setOnClickListener(this);
        //oneDayPerHourChartText.setOnClickListener(this);
    }

    private fun initRefresherLayout() {
        mSwipeRefreshLayout.isRefreshing = true
        mSwipeRefreshLayout.setOnRefreshListener {
            mSwipeRefreshLayout.isRefreshing = false
            if (viewModel.isOnline()) {
                Log.d(TAG, "Updating Weather Data")
                UIUpdateViewUtils.showSnackBarMessage("Updating Weather Data", requireActivity())
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViewModel()

        initRecyclerView()

        getActionBar().show()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(OneDayWeatherViewModel::class.java)
        viewModel.resetStartEndDates()
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        bindUI()
    }

    override fun onResume() {
        super.onResume()
        lastOrientation = resources.configuration.orientation

//        if (lastOrientation == Configuration.ORIENTATION_PORTRAIT && oneDayPerHourColumnChartView != null) {
//            defaultView(oneDayPerHourColumnChartView)
//        }
        lastOrientation = resources.configuration.orientation
        bindUI()
    }

    override fun onStop() {
        super.onStop()
        getActionBar().show()
        getBottomNavigationView().visibility = View.VISIBLE
    }

    override fun onClick(view: View) {
        when (view.id) {
            oneDayPerHourChartText.id -> {
                Log.d(TAG, "oneDayPerHourChartText")
                showWeatherDialog()
            }
            oneDayPerHourChartTemperature.id -> {
                Log.d(TAG, "oneDayPerHourChartTemperature")
                showWeatherTemperatureDialog()
            }
            oneDayPerHourChartPrecipitation.id -> {
                Log.d(TAG, "oneDayPerHourChartPrecipitation")
                showWeatherPrecipitationDialog()
            }
            oneDayPerHourChartWind?.id -> {
                Log.d(TAG, "oneDayPerHourChartWind")
                showWeatherWindDialog()
            }
        }
    }

    private fun bindUI(): Job {
        return launch {
            viewModel.getOneDayData()
            val oneDayWeatherLiveData = viewModel.uiViewsState
            val weatherLocation = viewModel.weatherLocation.await()

            Log.d(TAG, "buildUi $oneDayWeatherLiveData")
            weatherLocation.observe(viewLifecycleOwner, Observer { location ->
                if (location == null) return@Observer
                updateActionBarTitle(location.name, requireActivity())
                Log.d(TAG, "bindUI Update location with that data: $location")
            })

            oneDayWeatherLiveData.observe(viewLifecycleOwner, Observer {
                Log.d(TAG, "UpdateUI for List<FutureDayData> with: \n ${it ?: "null"} \n")
                when (it) {
                    null -> {

                        Log.e(TAG, "State is null, Update UI with EMPTY STATE $it")
                        currentStateData = null
                        updateEmptyStateUI(it)
                    }
                    is OneDayWeatherState -> {
                        currentStateData = it

                        Log.d(TAG, "Update UI with that data: $it")
                        updateUI(it)

                        updateRecyclerViewData(it.hourInfoItemsList)
                    }
                    is EmptyState -> {
                        Log.e(TAG, "Update UI with EMPTY STATE $it")
                        currentStateData = null
                        updateEmptyStateUI(it)
                    }
                    else -> {
                        Log.e(TAG, "Found UNKNOWN STATE $it")
                        updateEmptyStateUI(null)
                    }
                }
                //}
            })
        }
    }

    private fun updateEmptyStateUI(it: EmptyState?) {

        var warningString = getAppString(R.string.error_no_data_warning)
        var errorString = getAppString(R.string.error_no_data_warning)

        if (it != null) {
            warningString = it.warningString
            errorString = it.errorString
        }
        updateActionBarTitle("Loading...", requireActivity())
        updateActionBarDescription(warningString)
//        updateDayTemperatures(
//            null,
//            requireView().oneDayTemperatureView,
//            errorString
//        )
//        updateNightTemperatures(
//            null,
//            requireView().oneNightTemperatureView,
//            errorString
//        )
    }

    private fun updateUI(it: OneDayWeatherState) {
        oneDayGroupLoading.visibility = View.GONE
        mSwipeRefreshLayout.isRefreshing = false

        updateDayTemperatures(
            it.allDayWeatherAndAverageData,
            requireView().oneDayTemperatureView
        )
        updateNightTemperatures(
            it.allNightWeatherAndAverageData,
            requireView().oneNightTemperatureView
        )
        updateActionBarDescription(it.oneDaySubtitle)
        updateConditionIconsView(
            it.allDayWeatherAndAverageData,
            it.allNightWeatherAndAverageData,
            requireView().oneDayNightIconDescrView
        )
    }

    private fun initRecyclerView() {
        groupAdapter = GroupAdapter<ViewHolder>()
        recyclerView = oneDayPerHourRecyclerView.apply {
            adapter = groupAdapter
        }
        groupAdapter.setOnItemClickListener { item, view ->
            val itemDetail = (item as HourInfoItem).weatherEntry
            Toast.makeText(
                this@OneDayWeatherFragment.context,
                "Clicked: ${itemDetail.dtTxt}",
                Toast.LENGTH_SHORT
            ).show()
            Log.e(TAG, "GroupAdapter.setOnItemClickListener ${itemDetail}\n")
            showHourInfoDetails(itemDetail.dtTxt, view)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && getBottomNavigationView().isShown) {
                    getBottomNavigationView()?.visibility = View.GONE;
                } else if (dy < 0) {
                    getBottomNavigationView()?.visibility = View.VISIBLE;
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (lastOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if ((!recyclerView.canScrollVertically(1) || !recyclerView.canScrollHorizontally(
                            -1
                        )) &&/*newState == RecyclerView.SCROLL_STATE_SETTLING || */newState == RecyclerView.SCROLL_STATE_IDLE
                    ) {
                        //oneDayGroupData.clearAnimation()
                        if (!getActionBar().isShowing) {
                            showHideViewAndActionBarWithAnimation(
                                oneDayGroupData, View.VISIBLE,
                                actionBar = getActionBar()
                            )
                        }
                    } else {
                        //oneDayGroupData.clearAnimation()
                        if (getActionBar().isShowing) {
                            showHideViewAndActionBarWithAnimation(
                                oneDayGroupData,
                                View.GONE,
                                1111,
                                222,
                                getActionBar()
                            )

                        }
                    }
                }
            }
        })
    }

    private fun updateRecyclerViewData(hourInfoItemsList: List<HourInfoItem>) {
        groupAdapter.clear()
        groupAdapter.apply { groupAdapter.addAll(hourInfoItemsList) }
        groupAdapter.notifyDataSetChanged()
    }

    private fun showHourInfoDetails(dtTxt: String, view: View) {
        val actionShowDetail =
            OneDayWeatherFragmentDirections.actionShowDetail(dtTxt)
        Navigation.findNavController(view).navigate(actionShowDetail)
    }

    private fun updateConditionIconsView(
        allDayWeatherAndAverageData: MinMaxAvgTemp,
        allNightWeatherAndAverageData: MinMaxAvgTemp,
        view: View
    ) {
        // Day view
        val dayIconNumber = allDayWeatherAndAverageData.iconConditionID
        view.iconDayViewConditionText.text = allDayWeatherAndAverageData.iconViewConditionText
        updateIcon(dayIconNumber, view.iconViewDayConditionIcon)

        // Night view
        val nightIconNumber = allNightWeatherAndAverageData.iconConditionID
        view.iconNightViewConditionText.text =
            allNightWeatherAndAverageData.iconViewConditionText
        updateIcon(nightIconNumber, view.iconViewNightConditionIcon)
    }

    private fun updateDayTemperatures(
        calculatedDayData: MinMaxAvgTemp?,
        view: View,
        errorString: String = ""
    ) {
        view.tempViewTittleText.text = getAppString(R.string.weather_text_temp_title_day)
        if (calculatedDayData == null) {
            fillTempViews(null, view.oneNightTemperatureView, errorString)
            return
        } else
            fillTempViews(calculatedDayData, view.oneDayTemperatureView)
    }

    private fun updateNightTemperatures(
        calculatedNightData: MinMaxAvgTemp?,
        view: View,
        errorString: String = ""
    ) {
        view.tempViewTittleText.text = getAppString(R.string.weather_text_temp_title_night)
        if (calculatedNightData == null) {
            fillTempViews(null, view.oneNightTemperatureView, errorString)
            return
        } else
            fillTempViews(calculatedNightData, view.oneNightTemperatureView)
    }

    private fun fillTempViews(
        calculatedData: MinMaxAvgTemp?,
        view: View,
        errorString: String = ""
    ) {
        if (calculatedData == null) {
            view.tempViewMaxTemp.text = errorString
            view.tempViewMinTemp.text = errorString
            view.tempViewTemperature.text = errorString
            view.tempViewFeelsLike.text = errorString
            return
        }
        view.tempViewMaxTemp.text = calculatedData.maxTempText
        view.tempViewMinTemp.text = calculatedData.minTempText
        view.tempViewTemperature.text = calculatedData.averageTempText
        view.tempViewFeelsLike.text = calculatedData.averageFeelLikeTempText
    }

    private fun updateActionBarDescription(subtitle: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = subtitle
    }

    private fun getActionBar() = (requireActivity() as MainActivity).supportActionBar!!

    private fun getBottomNavigationView() = requireActivity().bottom_navigation

    private fun showWeatherDialog() {
        showDialog(
            requireContext(),
            R.layout.activity_graph_test,
            R.id.any_chart_view,
            currentStateData
        ) { createChart(currentStateData) }
    }

    private fun showWeatherTemperatureDialog() {
        showDialog(
            requireContext(),
            R.layout.activity_graph_test,
            R.id.any_chart_view,
            currentStateData
        ) { createTemperatureChart(currentStateData) }
    }

    private fun showWeatherPrecipitationDialog() {
        showDialog(
            requireContext(),
            R.layout.activity_graph_test,
            R.id.any_chart_view,
            currentStateData
        ) { createPrecipitationsChart(currentStateData) }
    }
    private fun showWeatherWindDialog() {
        showDialog(
            requireContext(),
            R.layout.activity_graph_test,
            R.id.any_chart_view,
            currentStateData
        ) { createWindChart(currentStateData) }
    }
}



