package com.fitness.wizard.runningTracker.ui.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitness.wizard.runningTracker.data.Run
import com.fitness.wizard.runningTracker.repositories.MainRepo
import com.fitness.wizard.runningTracker.util.SortTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepo: MainRepo
) : ViewModel() {

    fun insertRun(run: Run) {
        viewModelScope.launch {
            mainRepo.insertRun(run)
        }
    }

    fun deleteRun(run: Run) {
        viewModelScope.launch {
            mainRepo.deleteRun(run)
        }
    }

    val totalAvgSpeed = mainRepo.getTotalAvgSpeed()

    val totalDistance = mainRepo.getTotalDistance()

    val totalCaloriesBurned = mainRepo.getTotalCaloriesBurned()

    val totalTime = mainRepo.getTotalTimeInMillis()

    private val runsSortedByDate = mainRepo.getAllRunsSortedByDate()
    private val runsSortedByDistance = mainRepo.getAllRunsSortedByDistance()
    private val runsSortedByCalories = mainRepo.getAllRunsSortedByCaloriesBurned()
    private val runsSortedByAvgSpeed = mainRepo.getAllRunsSortedByAvgSpeed()
    private val runsSortedByTime = mainRepo.getAllRunsSortedByTimeInMillis()

    val sortedRuns = MediatorLiveData<List<Run>>()

    var sortType = SortTypes.DATE

    init {
        sortedRuns.addSource(runsSortedByDate) { result ->
            if (sortType == SortTypes.DATE) {
                result?.let { sortedRuns.value = it }
            }
        }
        sortedRuns.addSource(runsSortedByDistance) { result ->
            if (sortType == SortTypes.DISTANCE) {
                result?.let { sortedRuns.value = it }
            }
        }
        sortedRuns.addSource(runsSortedByCalories) { result ->
            if (sortType == SortTypes.CALORIES_BURNED) {
                result?.let { sortedRuns.value = it }
            }
        }
        sortedRuns.addSource(runsSortedByAvgSpeed) { result ->
            if (sortType == SortTypes.AVG_SPEED) {
                result?.let { sortedRuns.value = it }
            }
        }
        sortedRuns.addSource(runsSortedByTime) { result ->
            if (sortType == SortTypes.RUNNING_TIME) {
                result?.let { sortedRuns.value = it }
            }
        }
    }

    fun sortRuns(sortTypes: SortTypes) = when (sortTypes) {
        SortTypes.DATE -> runsSortedByDate.value?.let { sortedRuns.value = it }
        SortTypes.DISTANCE -> runsSortedByDistance.value?.let { sortedRuns.value = it }
        SortTypes.CALORIES_BURNED -> runsSortedByCalories.value?.let { sortedRuns.value = it }
        SortTypes.AVG_SPEED -> runsSortedByAvgSpeed.value?.let { sortedRuns.value = it }
        SortTypes.RUNNING_TIME -> runsSortedByTime.value?.let { sortedRuns.value = it }
    }.also {
        this.sortType = sortType
    }
}