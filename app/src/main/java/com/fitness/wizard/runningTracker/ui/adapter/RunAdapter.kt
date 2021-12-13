package com.fitness.wizard.runningTracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fitness.wizard.R
import com.fitness.wizard.runningTracker.data.Run
import com.fitness.wizard.runningTracker.util.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*


class RunAdapter(private val listener: IDeleteListener): RecyclerView.Adapter<RunViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.run_item, parent, false)
        val rv = RunViewHolder(view)


        view.findViewById<ImageView>(R.id.deleteButton).setOnClickListener{
            val run = differ.currentList[rv.adapterPosition]
            listener.deleteRunOnClick(run)
        }
        return rv
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val currentItem = differ.currentList[position]
        Glide.with(holder.itemView).load(currentItem.img).into(holder.mapImage)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentItem.timeStamp
        }
        val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        holder.date.text = dateFormat.format(calendar.time)

        val avgSpd = "${currentItem.avgSpeedInKM} km/h"
        holder.avgSpeed.text = avgSpd

        val distanceInKm = "${currentItem.distanceInMeter / 1000f} km"
        holder.distance.text = distanceInKm

        holder.time.text = TrackingUtility.getFormattedStopWatchTime(currentItem.timeInMil)

        val caloriesBurned = "${currentItem.caloriesBurned} kcal"
        holder.calories.text = caloriesBurned
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this,diffCallback)

    fun submitList(list: List<Run>) = differ.submitList(list)

}

class RunViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val mapImage: ImageView = itemView.findViewById(R.id.ivRunImage)
    val date: TextView = itemView.findViewById(R.id.tvDate)
    val time: TextView = itemView.findViewById(R.id.tvTime)
    val distance: TextView = itemView.findViewById(R.id.tvDistance)
    val avgSpeed: TextView = itemView.findViewById(R.id.tvAvgSpeed)
    val calories: TextView = itemView.findViewById(R.id.tvCalories)
}

interface IDeleteListener{
    fun deleteRunOnClick(run: Run)

}