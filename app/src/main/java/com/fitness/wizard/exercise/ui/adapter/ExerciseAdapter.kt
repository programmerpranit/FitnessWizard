package com.fitness.wizard.exercise.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fitness.wizard.R
import com.fitness.wizard.exercise.data.Exercise

class ExerciseAdapter:RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

        val exerciseName:TextView = itemView.findViewById(R.id.exerciseName)
        val target:TextView = itemView.findViewById(R.id.bodyPart)
        val equipment:TextView = itemView.findViewById(R.id.equipmentRequired)
        val exerciseGif:ImageView = itemView.findViewById(R.id.exerciseImage)
    }

    private val itemList: ArrayList<Exercise> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_view, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val currentItem = itemList[position]

        val targets = "${currentItem.bodyPart.uppercase()} - ${currentItem.target.uppercase()}"
        val equipmentTxt = "Equipment Required: ${currentItem.equipment.uppercase()}"
        val url = currentItem.gifUrl

        holder.exerciseName.text = currentItem.name.uppercase()
        holder.target.text = targets
        holder.equipment.text = equipmentTxt

        val uri = url.toUri().buildUpon().scheme("https").build()

        Glide.with(holder.exerciseGif.context).load(uri).into(holder.exerciseGif)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateList(list:ArrayList<Exercise>){
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }

}