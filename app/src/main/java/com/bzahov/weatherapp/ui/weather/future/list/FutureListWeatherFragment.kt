package com.bzahov.weatherapp.ui.weather.future.list


import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.internal.UIUpdateViewUtils
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarSubtitle
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarSubtitleWithResource
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarTitle
import com.bzahov.weatherapp.ui.MainActivity
import com.bzahov.weatherapp.ui.animationUtils.AnimationUtils
import com.bzahov.weatherapp.ui.base.ScopedFragment
import com.bzahov.weatherapp.ui.base.states.EmptyState
import com.bzahov.weatherapp.ui.weather.future.list.recyclerview.FutureWeatherItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.future_list_weather_fragment.*
import kotlinx.android.synthetic.main.future_list_weather_fragment.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class FutureListWeatherFragment : ScopedFragment(), KodeinAware {

    private val TAG = "FutureDetailWeatherFragment"
    override val kodein by closestKodein()
    private val viewModelFactory: FutureListWeatherViewModelFactory by instance<FutureListWeatherViewModelFactory>()
    private lateinit var viewModel: FutureListWeatherViewModel
    private lateinit var groupAdapter: GroupAdapter<ViewHolder>
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private var lastOrientation: Int = 0


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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(FutureListWeatherViewModel::class.java)
        initRecyclerView()
        bindUI()
        resetControlViews()
    }

    override fun onResume() {
        super.onResume()
        lastOrientation = resources.configuration.orientation
        bindUI()
    }

    override fun onStop() {
        super.onStop()
        resetControlViews()
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
                        is FutureListState -> {
                            Log.d(TAG, "Update UI with that data: $it")
                            updateUI(it)
                            updateRecyclerViewData(it.weatherItems)
                        }
                        is EmptyState -> {
                            updateEmptyStateUI(it)
                            Log.e(TAG, "Update UI with EMPTY STATE $it")
                        }
                        else -> {
                            Log.e(TAG, "Found UNKNOWN STATE $it")
                        }
                    }
                }
            })
        }
    }

    private fun updateUI(it: FutureListState) {
        futureGroupLoading.visibility = View.GONE
        mSwipeRefreshLayout.isRefreshing = false
        updateActionBarSubtitleWithResource(
            R.string.future_weather_five_days_next,
            requireActivity()
        )
    }

    private fun updateEmptyStateUI(it: EmptyState) {
        updateActionBarTitle("Loading...", requireActivity())
        updateActionBarSubtitle(
            it.warningString,
            requireActivity()
        )
    }

    private fun initRecyclerView() {
        groupAdapter = GroupAdapter()
        recyclerView = futureRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FutureListWeatherFragment.context)
            adapter = groupAdapter
        }

        groupAdapter.setOnItemClickListener { item, view ->
            Toast.makeText(
                this@FutureListWeatherFragment.context,
                "Clicked: ${item.itemCount}",
                Toast.LENGTH_SHORT
            ).show()

            val itemDetail = (item as FutureWeatherItem).weatherEntry
            Log.e(TAG, "\n\nGroupAdapter.setOnItemClickListener ${itemDetail}\n")
            showWeatherDetail(itemDetail.dtTxt, view)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && getBottomNavigationView().isShown) {
                    getBottomNavigationView()?.visibility = View.GONE
                } else if (dy < 0) {
                    getBottomNavigationView()?.visibility = View.VISIBLE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (lastOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if ((!recyclerView.canScrollVertically(1) || !recyclerView.canScrollHorizontally(
                            -1
                        )) && newState == RecyclerView.SCROLL_STATE_IDLE
                    ) {
                        //oneDayGroupData.clearAnimation()
                        if (!getActionBar().isShowing) {
                            AnimationUtils.showHideViewAndActionBarWithAnimation(
                                futureTextDescriptionView,
                                View.VISIBLE,
                                actionBar = getActionBar()
                            )
                        }
                    } else {
                        //oneDayGroupData.clearAnimation()
                        if (getActionBar().isShowing) {
                            AnimationUtils.showHideViewAndActionBarWithAnimation(
                                futureTextDescriptionView,
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
        groupAdapter.clear()
        groupAdapter.apply { groupAdapter.addAll(weatherItems) }
        groupAdapter.notifyDataSetChanged()
    }

    private fun showWeatherDetail(string: String, view: View) {
        resetControlViews()
        val actionShowDetail = FutureListWeatherFragmentDirections.actionShowDetail(string)
        Navigation.findNavController(view).navigate(actionShowDetail)
    }

    private fun resetControlViews() {
        getActionBar().show()
        getBottomNavigationView().visibility = View.VISIBLE
    }

    private fun getActionBar() = (requireActivity() as MainActivity).supportActionBar!!

    private fun getBottomNavigationView() = requireActivity().bottom_navigation
}
