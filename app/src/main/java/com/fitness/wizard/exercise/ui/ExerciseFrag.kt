package com.fitness.wizard.exercise.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonArrayRequest
import com.fitness.wizard.R
import com.fitness.wizard.core.MySingleton
import com.fitness.wizard.core.util.ApiKeys
import com.fitness.wizard.exercise.data.Exercise
import com.fitness.wizard.exercise.ui.adapter.ExerciseAdapter

class ExerciseFrag : Fragment(R.layout.fragment_exercise) {

    private val baseUrl = "https://exercisedb.p.rapidapi.com/exercises"

    lateinit var mAdapter:ExerciseAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvExercise)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        mAdapter = ExerciseAdapter()
        recyclerView.adapter = mAdapter
    }


    private fun fetchData(){
        val jsonObjectRequest =object: JsonArrayRequest(
            Method.GET, baseUrl, null,
            { exercisesList ->//list got through api request

                // empty list
                val exercises = ArrayList<Exercise>()

                // getting exercises from the list
                for (i in 0 until exercisesList.length()){
                    val exerciseJsonObject = exercisesList.getJSONObject(i)
                    val model = Exercise(
                        exerciseJsonObject.getString("bodyPart"),
                        exerciseJsonObject.getString("equipment"),
                        exerciseJsonObject.getString("gifUrl"),
                        exerciseJsonObject.getString("name"),
                        exerciseJsonObject.getString("target")
                    )
                    exercises.add(model)
                }
                mAdapter.updateList(exercises)
            },
            {

            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["x-rapidapi-host"] = "exercisedb.p.rapidapi.com"
                headers["x-rapidapi-key"] = ApiKeys.ExerciseApi
                return headers
            }
        }

        MySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)

    }
}