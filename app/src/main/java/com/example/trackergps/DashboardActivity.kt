package com.example.trackergps

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trackergps.databinding.ActivityDashboardBinding
import java.util.Calendar

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var userManager: UserManager
    // DITAMBAH: Instance untuk ActivityManager dan Adapter
    private lateinit var activityManager: ActivityManager
    private lateinit var activityAdapter: ActivityAdapterDashboard
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager = UserManager(this)
        activityManager = ActivityManager(this) // DITAMBAH: Inisialisasi ActivityManager

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currentUserId = userManager.UserSession()

        // Jika user tidak ditemukan, kembali ke Login (pengaman)
        if (currentUserId == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupRecyclerView() // DITAMBAH: Panggil fungsi setup RecyclerView
        setupBottomNavigation() // DIUBAH: Panggil fungsi setup Navigasi
    }

    // DITAMBAH: Fungsi untuk mempersiapkan RecyclerView
    private fun setupRecyclerView() {
        activityAdapter = ActivityAdapterDashboard()
        binding.recyclerViewRecentActivities.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = activityAdapter
        }
    }

    // DIUBAH: Fungsi ini sekarang juga memuat data aktivitas
    @SuppressLint("SetTextI18n")
    private fun loadDashboardData() {
        // Muat data pengguna (tidak berubah)
        val userCursor = userManager.getUserById(currentUserId)
        if (userCursor != null && userCursor.moveToFirst()) {
            @SuppressLint("Range")
            val name = userCursor.getString(userCursor.getColumnIndex(UserManager.NAME))
            @SuppressLint("Range")
            val totalDistance = userCursor.getFloat(userCursor.getColumnIndex(UserManager.TOTAL_DISTANCE)) // Ubah ke Float
            @SuppressLint("Range")
            val rewardPoints = userCursor.getInt(userCursor.getColumnIndex(UserManager.REWARD_POINTS))
            userCursor.close()

            binding.textViewGreeting.text = getGreeting()
            binding.textViewUserName.text = name
            // Format jarak ke KM
            binding.textViewTotalDistance.text = "%.2f KM".format(totalDistance / 1000f)
            binding.textViewRewardPoints.text = "$rewardPoints Poin"

        } else {
            binding.textViewUserName.text = "Data tidak ditemukan"
        }

        // DITAMBAH: Muat data aktivitas terbaru
        loadRecentActivities()
    }

    // Di dalam file DashboardActivity.kt

    @SuppressLint("Range")
    private fun loadRecentActivities() {
        // --- DEBUGGING LOGS ---
        Log.d("DashboardDebug", "Memulai loadRecentActivities untuk userId: $currentUserId")

        val activityCursor: Cursor = activityManager.getActivitiesByUserId(currentUserId)

        Log.d("DashboardDebug", "Cursor didapat. Jumlah data: ${activityCursor.count}")

        val recentActivities = mutableListOf<ActivityItem>()
        if (activityCursor.moveToFirst()) {
            Log.d("DashboardDebug", "Cursor tidak kosong. Memulai loop.")
            do {
                val activity = ActivityItem(
                    type = activityCursor.getString(activityCursor.getColumnIndex(ActivityManager.TYPE)),
                    distance = activityCursor.getFloat(activityCursor.getColumnIndex(ActivityManager.DISTANCE)),
                    duration = activityCursor.getFloat(activityCursor.getColumnIndex(ActivityManager.DURATION)),
                    points = activityCursor.getInt(activityCursor.getColumnIndex(ActivityManager.POINTS_EARNED)),
                    vehicle = activityCursor.getString(activityCursor.getColumnIndex(ActivityManager.VEHICLE))
                )
                recentActivities.add(activity)
                Log.d("DashboardDebug", "Menambahkan aktivitas: ${activity.type} dengan kendaraan ${activity.vehicle}")
            } while (activityCursor.moveToNext())
        } else {
            Log.d("DashboardDebug", "Cursor kosong. Tidak ada data untuk ditampilkan.")
        }
        activityCursor.close()

        Log.d("DashboardDebug", "Proses selesai. Total aktivitas yang akan ditampilkan: ${recentActivities.size}")
        activityAdapter.submitList(recentActivities.takeLast(3).reversed())
    }

    private fun getGreeting(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..10 -> "Selamat Pagi,"
            in 11..14 -> "Selamat Siang,"
            in 15..17 -> "Selamat Sore,"
            else -> "Selamat Malam,"
        }
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }

    // DIUBAH: Logika navigasi disatukan dalam satu fungsi
    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    true // Sudah di halaman ini
                }
                R.id.activity -> {
                    val intent = Intent(this, UserActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    startActivity(intent)
                    true
                }
                R.id.profile -> {
                    val intent = Intent(this, UserProfile::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}