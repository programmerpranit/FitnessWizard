package com.fitness.wizard.diet.ui.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.fitness.wizard.R
import com.fitness.wizard.core.util.ApiKeys
import com.fitness.wizard.core.MySingleton


class RecipeDetailFrag : Fragment(R.layout.fragment_recipe_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundleArguments = this.arguments
        val recipeId = bundleArguments?.getInt("recipeId", 21)
        val recipeName = bundleArguments?.getString("recipeName", "No recipe found")
        val recipeImage = bundleArguments?.getString(
            "recipeImage",
            "https://spoonacular.com/recipeImages/716429-556x370.jpg"
        )

        val url =
            "https://api.spoonacular.com/recipes/$recipeId/information?apiKey=${ApiKeys.DietApi}"

        fetchData(url, view)

        val image = view.findViewById<ImageView>(R.id.recipeImage)
        val name = view.findViewById<TextView>(R.id.recipeName)

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        progressBar.visibility = View.VISIBLE  //show Progress Bar

        name.text = recipeName
        Glide.with(this).load(recipeImage).into(image)
        progressBar.visibility = View.INVISIBLE  //hide progressBar
    }

    private fun fetchData(url: String, view: View) {

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, {

                val sourceName = "Recipe By ${it.getString("sourceName")}" //0
                val healthScore = "Health Score ${it.getDouble("healthScore")}" //1
                val sourceUrl = it.getString("sourceUrl").toString() //2
                val vegetarian = it.getBoolean("vegetarian")
                val veryHealthy = it.getBoolean("veryHealthy")
                val dairyFree = it.getBoolean("dairyFree")
                val glutenFree = it.getBoolean("glutenFree")

                val recipeByTV = view.findViewById<TextView>(R.id.recipeBy)
                val healthScoreTV = view.findViewById<TextView>(R.id.healthScore)
                val tvVegetarian = view.findViewById<TextView>(R.id.vegetarian)
                val tvVeryHealthy = view.findViewById<TextView>(R.id.veryHealthy)
                val tvDairyFree = view.findViewById<TextView>(R.id.dairyFree)
                val tvGlutenFree = view.findViewById<TextView>(R.id.glutenFree)

                recipeByTV.text = sourceName
                healthScoreTV.text = healthScore

                if (vegetarian) {
                    tvVegetarian.text = "Vegetarian: YES"
                }
                if (veryHealthy) {
                    tvVeryHealthy.text = "Very Healthy: YES"
                }
                if (dairyFree) {
                    tvDairyFree.text = "Dairy Free: YES"
                }
                if (glutenFree) {
                    tvGlutenFree.text = "Gluten Free: YES"
                }

                view.findViewById<Button>(R.id.fullRecipeBtn).setOnClickListener {
                    openUrl(sourceUrl)
                }
            },
            {

            }
        )
        MySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)
    }

    private fun openUrl(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
    }

}