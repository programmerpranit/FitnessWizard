package com.fitness.wizard.runningTracker.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fitness.wizard.R
import com.fitness.wizard.core.util.Constants.ACTION_PAUSE_SERVICE
import com.fitness.wizard.core.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.fitness.wizard.core.util.Constants.ACTION_STOP_SERVICE
import com.fitness.wizard.core.util.Constants.KEY_LAST_DAY
import com.fitness.wizard.core.util.Constants.KEY_STREAKS
import com.fitness.wizard.core.util.Constants.MAP_ZOOM
import com.fitness.wizard.core.util.Constants.POLYLINE_COLOR
import com.fitness.wizard.core.util.Constants.POLYLINE_WIDTH
import com.fitness.wizard.runningTracker.data.Run
import com.fitness.wizard.runningTracker.services.Polyline
import com.fitness.wizard.runningTracker.services.TrackingService
import com.fitness.wizard.runningTracker.ui.viewmodel.MainViewModel
import com.fitness.wizard.runningTracker.util.TrackingUtility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()

    private var map: GoogleMap? = null

    private var isTracking = false

    private var pathPoints = mutableListOf<Polyline>()

    private var currentTimeMillis = 0L

    private var menu: Menu? = null

    @set:Inject
    var weight: Float = 60f

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        addAllPolyline()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapView = view.findViewById<MapView>(R.id.mapView)

        mapView.onCreate(savedInstanceState)


        val runBtn = view.findViewById<Button>(R.id.btnToggleRun)
        runBtn.setOnClickListener {
            toggleRun()
        }
        addAllPolyline()

        val finishBtn = view.findViewById<Button>(R.id.btnFinishRun)
        finishBtn.visibility = View.GONE
        finishBtn.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDB()
            it.visibility = View.GONE
        }

        mapView.getMapAsync {
            map = it
            addAllPolyline()
        }

        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMiles.observe(viewLifecycleOwner, {
            currentTimeMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(currentTimeMillis, true)
            val tvTimer = view?.findViewById<TextView>(R.id.tvTimer)
            tvTimer!!.text = formattedTime
        })
    }

    private fun toggleRun() {
        if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun stopRun() {
        val btnFinishRun = view?.findViewById<Button>(R.id.btnFinishRun)
        btnFinishRun!!.visibility = View.GONE
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_mainFragment)
    }

    private fun updateTracking(isTracking: Boolean) {
        val btnToggleRun = view?.findViewById<Button>(R.id.btnToggleRun)
        val btnFinishRun = view?.findViewById<Button>(R.id.btnFinishRun)

        this.isTracking = isTracking
        if (!isTracking && currentTimeMillis > 0L) {
            btnToggleRun!!.text = "Start"
            btnFinishRun!!.visibility = View.VISIBLE
        } else if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            btnToggleRun!!.text = "Stop"
            btnFinishRun!!.visibility = View.GONE
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolyline() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun zoomToSeeWholeTrack() {
        val mapView = view?.findViewById<MapView>(R.id.mapView)
        val bounds = LatLngBounds.builder()
        for (polyline in pathPoints) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView!!.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun endRunAndSaveToDB() {
        map?.snapshot {
            var distanceInMeters = 0
            for (polyline in pathPoints) {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed =
                ((distanceInMeters / 1000f) / (currentTimeMillis / 1000f / 60 / 60) * 10).roundToInt() / 10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run = Run(
                it,
                dateTimeStamp,
                avgSpeed,
                distanceInMeters,
                currentTimeMillis,
                caloriesBurned
            )

            // streaks logic
            val lastDate = sharedPreferences.getLong(KEY_LAST_DAY, 55L)
            val today = TimeUnit.MILLISECONDS.toDays(dateTimeStamp)
            if (distanceInMeters > 1000) {
                if (lastDate == today - 1L) {
                    val streaks = sharedPreferences.getInt(KEY_STREAKS, 0)
                    sharedPreferences.edit()
                        .putLong(KEY_LAST_DAY, today)
                        .putInt(KEY_STREAKS, streaks + 1)
                        .apply()
                }

            }
            viewModel.insertRun(run)
            stopRun()
        }
    }

    //////////////////////////////////////////////////////////////////////////
    //////////////////////// FOR MAP LIFECYCLE ///////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    override fun onResume() {
        super.onResume()
        val mapView = view?.findViewById<MapView>(R.id.mapView)
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        val mapView = view?.findViewById<MapView>(R.id.mapView)
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        val mapView = view?.findViewById<MapView>(R.id.mapView)
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        val mapView = view?.findViewById<MapView>(R.id.mapView)
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        val mapView = view?.findViewById<MapView>(R.id.mapView)
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        val mapView = view?.findViewById<MapView>(R.id.mapView)
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val mapView = view?.findViewById<MapView>(R.id.mapView)
        mapView?.onSaveInstanceState(outState)
    }

}