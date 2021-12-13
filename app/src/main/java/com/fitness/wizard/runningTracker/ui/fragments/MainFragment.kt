package com.fitness.wizard.runningTracker.ui.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fitness.wizard.R
import com.fitness.wizard.core.ReminderNotificationManager
import com.fitness.wizard.core.util.Constants
import com.fitness.wizard.core.util.Constants.KEY_LAST_DAY
import com.fitness.wizard.core.util.Constants.KEY_STREAKS
import com.fitness.wizard.core.util.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.fitness.wizard.runningTracker.data.Run
import com.fitness.wizard.runningTracker.ui.adapter.IDeleteListener
import com.fitness.wizard.runningTracker.ui.adapter.RunAdapter
import com.fitness.wizard.runningTracker.ui.viewmodel.MainViewModel
import com.fitness.wizard.runningTracker.util.SortTypes
import com.fitness.wizard.runningTracker.util.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), IDeleteListener,
    EasyPermissions.PermissionCallbacks {
    @Inject
    lateinit var userName: String

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewmodel: MainViewModel by viewModels()

    private lateinit var runAdapter: RunAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val hiiText = view.findViewById<TextView>(R.id.hiiText)
        val hiiString = "Hii $userName,"
        hiiText.text = hiiString
        checkStreaks()

        val statistics = view.findViewById<CardView>(R.id.statistics)
        val tracker = view.findViewById<CardView>(R.id.runTracking)
        val exercise = view.findViewById<CardView>(R.id.exercise)
        val diet = view.findViewById<CardView>(R.id.diet)

        statistics.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_statisticsFragment)
        }
        tracker.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_trackingFragment)
        }
        exercise.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_exerciseFrag)
        }
        diet.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_recipesFrag)
        }


//////////////////////////////////////////////////////////////////////
        /////RUNS RV //////////
/////////////////////////////////////////////////////////////////////
        requestPermission()

        val spinner = view.findViewById<Spinner>(R.id.spinner)

        when (viewmodel.sortType) {
            SortTypes.DATE -> spinner.setSelection(0)
            SortTypes.RUNNING_TIME -> spinner.setSelection(1)
            SortTypes.DISTANCE -> spinner.setSelection(2)
            SortTypes.AVG_SPEED -> spinner.setSelection(3)
            SortTypes.CALORIES_BURNED -> spinner.setSelection(4)
        }

        lifecycleScope.launch(Dispatchers.IO) {

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        0 -> viewmodel.sortRuns(SortTypes.DATE)
                        1 -> viewmodel.sortRuns(SortTypes.RUNNING_TIME)
                        2 -> viewmodel.sortRuns(SortTypes.DISTANCE)
                        3 -> viewmodel.sortRuns(SortTypes.AVG_SPEED)
                        4 -> viewmodel.sortRuns(SortTypes.CALORIES_BURNED)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}

            }

            lifecycleScope.launch(Dispatchers.Main) {
                viewmodel.sortedRuns.observe(viewLifecycleOwner, {
                    runAdapter.submitList(it)
                })
            }

        }

        //////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvRun)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        runAdapter = RunAdapter(this)
        recyclerView.adapter = runAdapter


    }


    private fun checkStreaks() {
        val today = Calendar.getInstance().timeInMillis
        val todayDate = TimeUnit.MILLISECONDS.toDays(today) // today date
        val yesterdayDate = todayDate - 1L
        val lastDay = sharedPreferences.getLong(KEY_LAST_DAY, 0L)

        if (lastDay == 0L) { // First Day Case
            sharedPreferences.edit()
                .putInt(KEY_STREAKS, 0)
                .putLong(KEY_LAST_DAY, yesterdayDate)
                .apply()
            view?.findViewById<TextView>(R.id.streaksCount)?.text = "0"
        } else if (lastDay != todayDate && lastDay != yesterdayDate) { // If user is inconsistent
            sharedPreferences.edit()
                .putInt(KEY_STREAKS, 0)
                .putLong(KEY_LAST_DAY, yesterdayDate)
                .apply()

            view?.findViewById<TextView>(R.id.streaksCount)?.text = "0"
        } else {
            view?.findViewById<TextView>(R.id.streaksCount)?.text =
                sharedPreferences.getInt(KEY_STREAKS, 0).toString()
        }
    }

    private fun requestPermission() {
        if (TrackingUtility.hasLocationPermission(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You Need To Accept Location Permissions To Use This App",
                REQUEST_CODE_LOCATION_PERMISSION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You Need To Accept Location Permissions To Use This App",
                REQUEST_CODE_LOCATION_PERMISSION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun deleteRunOnClick(run: Run) {
        viewmodel.deleteRun(run)
    }
}