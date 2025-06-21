package com.example.trackergps

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trackergps.databinding.ItemActivityDashboardBinding // Pastikan import ini benar

// Data class sederhana untuk menampung data yang akan ditampilkan
data class ActivityItem(
    val type: String,
    val distance: Float,
    val duration: Float,
    val points: Int,
    val vehicle: String
)

class ActivityAdapterDashboard : RecyclerView.Adapter<ActivityAdapterDashboard.ActivityViewHolder>() {

    private val activities = mutableListOf<ActivityItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newActivities: List<ActivityItem>) {
        activities.clear()
        activities.addAll(newActivities)
        notifyDataSetChanged() // Memberitahu RecyclerView untuk refresh tampilan
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        // Membuat ViewHolder untuk setiap item
        val binding = ItemActivityDashboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        // Mengirim data ke ViewHolder untuk ditampilkan
        holder.bind(activities[position])
    }

    override fun getItemCount(): Int = activities.size

    // ViewHolder bertugas menampung view dari satu item dan mengisi datanya
    class ActivityViewHolder(private val binding: ItemActivityDashboardBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(activity: ActivityItem) {
            // Mengisi data ke komponen-komponen di item_activity_dashboard.xml
            binding.textActivityType.text = "${activity.type} (${activity.vehicle})"

            val distanceInKm = activity.distance / 1000f
            val durationInMinutes = activity.duration / 60f
            binding.textActivityDetails.text = "%.2f km - %.0f menit".format(distanceInKm, durationInMinutes)

            binding.textActivityPoints.text = "+ ${activity.points} Poin"

            // Logika untuk mengubah ikon (opsional)
            when (activity.vehicle.lowercase()) {
                "jalan kaki" -> binding.iconActivityType.setImageResource(R.drawable.ic_run)
                "sepeda motor" -> binding.iconActivityType.setImageResource(R.drawable.ic_scooter_illustration)
                // Tambahkan case lain jika perlu
                else -> binding.iconActivityType.setImageResource(R.drawable.ic_activity)
            }
        }
    }
}