package com.fitness.wizard.diet.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.fitness.wizard.R
import com.fitness.wizard.core.util.ApiKeys
import com.fitness.wizard.core.MySingleton
import com.fitness.wizard.diet.data.Recipe
import com.fitness.wizard.diet.ui.adapter.DietAdapter
import com.fitness.wizard.diet.ui.adapter.IClicked

class RecipesFrag : Fragment(R.layout.fragment_recipes), IClicked {

    lateinit var mAdapter: DietAdapter

    private val url =
        "https://api.spoonacular.com/recipes/complexSearch?apiKey=${ApiKeys.DietApi}&diet&number=100"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvDietRecipes)
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        fetchData()
        mAdapter = DietAdapter(this)
        recyclerView.adapter = mAdapter

    }

    private fun fetchData() {

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            {
                val dietJsonArray = it.getJSONArray("results")
                val recipes = ArrayList<Recipe>()
                for (i in 0 until dietJsonArray.length()) {
                    val dietJsonObject = dietJsonArray.getJSONObject(i)
                    val model = Recipe(
                        dietJsonObject.getInt("id"),
                        dietJsonObject.getString("title"),
                        dietJsonObject.getString("image")
                    )
                    recipes.add(model)
                }
                mAdapter.updateData(recipes)
            },
            {

            }
        )
        MySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)

    }

    override fun onItemClicked(item: Recipe) {
        val bundle = Bundle()
        bundle.putInt("recipeId", item.recipeID)
        bundle.putString("recipeName", item.recipeName)
        bundle.putString("recipeImage", item.recipeImage)

        val recipeDetail = RecipeDetailFrag()
        recipeDetail.arguments = bundle

        findNavController().navigate(R.id.action_recipesFrag_to_recipeDetailFrag, bundle)
    }

}