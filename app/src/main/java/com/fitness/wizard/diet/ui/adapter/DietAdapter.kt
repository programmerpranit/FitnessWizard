package com.fitness.wizard.diet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fitness.wizard.R
import com.fitness.wizard.diet.data.Recipe
import java.util.ArrayList

class DietAdapter(private val clickInterface:IClicked) : RecyclerView.Adapter<ViewHolder>() {

    private val items: ArrayList<Recipe> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.diet_recipe_view, parent, false)
        val view = ViewHolder(itemView)
        itemView.setOnClickListener{
            clickInterface.onItemClicked(items[view.adapterPosition])
        }
        return view
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = items[position]

        Glide.with(holder.itemView.context).load(currentItem.recipeImage).into(holder.itemImage)

        holder.itemText.text = currentItem.recipeName


    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(data: ArrayList<Recipe>){
        items.clear()
        items.addAll(data)

        notifyDataSetChanged()
    }

}

class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val itemImage: ImageView = itemView.findViewById(R.id.ivSingleItem)
    val itemText: TextView = itemView.findViewById(R.id.tvSingleItem)
}

interface IClicked {
    fun onItemClicked(item: Recipe)
}
