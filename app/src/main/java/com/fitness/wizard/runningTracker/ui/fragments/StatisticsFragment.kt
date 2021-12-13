package com.fitness.wizard.runningTracker.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.fitness.wizard.R
import com.fitness.wizard.runningTracker.ui.viewmodel.MainViewModel
import com.fitness.wizard.runningTracker.util.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val mViewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        subscribeToObservers()

    }

    private fun subscribeToObservers() {
        mViewModel.totalTime.observe(viewLifecycleOwner, {
            it?.let {
                val totalTime = TrackingUtility.getFormattedStopWatchTime(it)
                val tvTime = view?.findViewById<TextView>(R.id.tvTotalTime)
                tvTime?.text = totalTime
            }
        })
        mViewModel.totalAvgSpeed.observe(viewLifecycleOwner, {
            it?.let {
                val tvAvgSpeed = view?.findViewById<TextView>(R.id.tvAvgSpeed)
                tvAvgSpeed?.text = it.toString()
            }
        })
        mViewModel.totalDistance.observe(viewLifecycleOwner, {
            it?.let {
                val tvDistance = view?.findViewById<TextView>(R.id.tvTotalDistance)
                tvDistance?.text = (it / 1000f).toString()
            }
        })
        mViewModel.totalCaloriesBurned.observe(viewLifecycleOwner, {
            it?.let {
                val tvCalories = view?.findViewById<TextView>(R.id.tvTotalCalories)
                tvCalories?.text = it.toString()
            }
        })
    }

}