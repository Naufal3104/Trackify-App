package com.example.trackergps.admin

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackergps.ActivityManager
import com.example.trackergps.R
import com.example.trackergps.UserActivity
import com.example.trackergps.databinding.ActivityManageActivityBinding

// 1. PERBARUI DATA CLASS: Tambahkan properti 'vehicle'
data class Activity(
    val id: Int,
    val userId: Int,
    val type: String,
    val distance: Float,
    val duration: Float,
    val date: String,
    val pointsEarned: Int,
    val vehicle: String // DITAMBAH
)

class ManageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageActivityBinding
    private lateinit var activityManager: ActivityManager
    private lateinit var activityAdapter: ActivityAdapter
    private var selectedActivity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        activityManager = ActivityManager(this)
        setupRecyclerView()
        loadActivities()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        activityAdapter = ActivityAdapter { activity ->
            selectedActivity = activity
            // 2. PERBARUI RECYCLER VIEW SETUP: Tampilkan data 'vehicle' di form
            binding.editTextType.setText(activity.type)
            binding.editTextVehicle.setText(activity.vehicle) // DITAMBAH
            binding.editTextDistance.setText(activity.distance.toString())
            binding.editTextDuration.setText(activity.duration.toString())
            binding.editTextDate.setText(activity.date)
            binding.editTextPoints.setText(activity.pointsEarned.toString())
        }
        binding.recyclerViewActivities.apply {
            layoutManager = LinearLayoutManager(this@ManageActivity)
            adapter = activityAdapter
        }
    }

    @SuppressLint("Range")
    private fun loadActivities() {
        val cursor: Cursor = activityManager.getAllActivities()
        val activities = mutableListOf<Activity>()
        if (cursor.moveToFirst()) {
            do {
                // 3. PERBARUI LOAD DATA: Ambil data 'vehicle' dari database
                activities.add(
                    Activity(
                        id = cursor.getInt(cursor.getColumnIndex(ActivityManager.ID)),
                        userId = cursor.getInt(cursor.getColumnIndex(ActivityManager.USER_ID)),
                        type = cursor.getString(cursor.getColumnIndex(ActivityManager.TYPE)),
                        distance = cursor.getFloat(cursor.getColumnIndex(ActivityManager.DISTANCE)),
                        duration = cursor.getFloat(cursor.getColumnIndex(ActivityManager.DURATION)),
                        date = cursor.getString(cursor.getColumnIndex(ActivityManager.DATE)),
                        pointsEarned = cursor.getInt(cursor.getColumnIndex(ActivityManager.POINTS_EARNED)),
                        vehicle = cursor.getString(cursor.getColumnIndex(ActivityManager.VEHICLE)) // DITAMBAH
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        activityAdapter.submitList(activities)
    }

    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            finish()
            val intent = Intent(this, Manage::class.java)
            startActivity(intent)
        }

        // 4. PERBARUI TOMBOL TAMBAH: Sertakan data 'vehicle'
        binding.buttonAdd.setOnClickListener {
            val userId = 1 // Contoh
            val type = binding.editTextType.text.toString().trim()
            val vehicle = binding.editTextVehicle.text.toString().trim() // DITAMBAH
            val distance = binding.editTextDistance.text.toString().toFloatOrNull() ?: 0f
            val duration = binding.editTextDuration.text.toString().toFloatOrNull() ?: 0f
            val date = binding.editTextDate.text.toString().trim()
            val points = binding.editTextPoints.text.toString().toIntOrNull() ?: 0

            if (type.isNotEmpty() && date.isNotEmpty() && vehicle.isNotEmpty()) {
                // Panggil fungsi dengan parameter 'vehicle'
                activityManager.addActivity(userId, type, distance, duration, date, points, vehicle)
                Toast.makeText(this, "Aktivitas berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                clearFormAndRefresh()
            } else {
                Toast.makeText(this, "Tipe, Kendaraan, dan Tanggal tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        // 5. PERBARUI TOMBOL EDIT: Sertakan data 'vehicle'
        binding.buttonEdit.setOnClickListener {
            if (selectedActivity == null) {
                Toast.makeText(this, "Pilih aktivitas yang akan diedit", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val type = binding.editTextType.text.toString().trim()
            val vehicle = binding.editTextVehicle.text.toString().trim() // DITAMBAH
            val distance = binding.editTextDistance.text.toString().toFloatOrNull() ?: 0f
            val duration = binding.editTextDuration.text.toString().toFloatOrNull() ?: 0f
            val date = binding.editTextDate.text.toString().trim()
            val points = binding.editTextPoints.text.toString().toIntOrNull() ?: 0

            if (type.isNotEmpty() && date.isNotEmpty() && vehicle.isNotEmpty()) {
                // Panggil fungsi dengan parameter 'vehicle'
                activityManager.updateActivity(selectedActivity!!.id, type, distance, duration, date, points, vehicle)
                Toast.makeText(this, "Aktivitas berhasil diupdate", Toast.LENGTH_SHORT).show()
                clearFormAndRefresh()
            } else {
                Toast.makeText(this, "Tipe, Kendaraan, dan Tanggal tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonDelete.setOnClickListener {
            if (selectedActivity == null) {
                Toast.makeText(this, "Pilih aktivitas yang akan dihapus", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            activityManager.deleteActivity(selectedActivity!!.id)
            Toast.makeText(this, "Aktivitas berhasil dihapus", Toast.LENGTH_SHORT).show()
            clearFormAndRefresh()
        }
    }

    private fun clearFormAndRefresh() {
        selectedActivity = null
        binding.editTextType.text?.clear()
        binding.editTextVehicle.text?.clear() // DITAMBAH: Kosongkan form vehicle
        binding.editTextDistance.text?.clear()
        binding.editTextDuration.text?.clear()
        binding.editTextDate.text?.clear()
        binding.editTextPoints.text?.clear()
        binding.recyclerViewActivities.requestFocus()
        loadActivities()
    }
}

// --- Adapter untuk RecyclerView Aktivitas ---
class ActivityAdapter(private val onItemClick: (Activity) -> Unit) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    private var activities: List<Activity> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(activityList: List<Activity>) {
        activities = activityList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]
        holder.bind(activity)
        holder.itemView.setOnClickListener {
            onItemClick(activity)
        }
    }

    override fun getItemCount(): Int = activities.size

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val activityInfo: TextView = itemView.findViewById(R.id.textViewActivityInfo)
        private val metrics: TextView = itemView.findViewById(R.id.textViewMetrics)
        private val date: TextView = itemView.findViewById(R.id.textViewDate)
        private val points: TextView = itemView.findViewById(R.id.textViewPoints)

        @SuppressLint("SetTextI18n")
        fun bind(activity: Activity) {
            // PERBARUI TAMPILAN ITEM: Tampilkan juga data vehicle
            activityInfo.text = "User ID: ${activity.userId} - Tipe: ${activity.type} (${activity.vehicle})"
            metrics.text = "Jarak: %.0f m - Durasi: %.0f d".format(activity.distance, activity.duration)
            date.text = "Tanggal: ${activity.date}"
            points.text = "Poin: ${activity.pointsEarned}"
        }
    }
}