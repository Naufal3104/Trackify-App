package com.example.trackergps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.trackergps.databinding.ActivityUserBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding

    // DIHAPUS: Kita tidak akan bergantung pada variabel lokal ini lagi
    // private var isCurrentlyTracking = false

    private lateinit var userManager: UserManager
    private var selectedVehicle: String? = null
    private var activityStartTime: Long = 0L

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var allGranted = true
            permissions.entries.forEach {
                if (!it.value) allGranted = false
            }
            if (!allGranted) {
                Toast.makeText(this, "Izin lokasi dan notifikasi sangat penting untuk fitur ini.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userManager = UserManager(this)

        requestAllPermissions()

        binding.btnStart.setOnClickListener {
            // DIUBAH: Cek langsung ke sumber kebenaran (LiveData)
            if (LocationService.isTracking.value == true) {
                sendCommandToService(LocationService.ACTION_STOP)
                saveActivityData()
            } else {
                val vehicle = binding.editTextVehicle.text.toString().trim()
                if (vehicle.isEmpty()) {
                    Toast.makeText(this, "Silakan masukkan jenis kendaraan terlebih dahulu.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                this.selectedVehicle = vehicle
                this.activityStartTime = System.currentTimeMillis()

                sendCommandToService(LocationService.ACTION_START)
            }
        }

        observeServiceData()
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.activity
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    startActivity(intent)
                    true
                }
                R.id.activity -> {
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

    private fun observeServiceData() {
        LocationService.isTracking.observe(this) { isTracking ->
            // DIUBAH: Langsung teruskan nilai dari LiveData ke fungsi UI
            updateButtonUI(isTracking)
        }

        LocationService.totalDistance.observe(this) { distanceInMeters ->
            val distanceInKm = distanceInMeters / 1000f
            val distanceText = String.format("%.2f km", distanceInKm)
            Log.d("UserActivity", "Jarak terbaru: $distanceText")
        }
    }

    private fun updateButtonUI(isTracking: Boolean) {
        if (isTracking) {
            binding.btnStart.text = "Selesai"
            binding.editTextVehicle.isEnabled = false
        } else {
            binding.btnStart.text = "Mulai"
            binding.editTextVehicle.isEnabled = true
        }
    }

    private fun sendCommandToService(action: String) {
        if (action == LocationService.ACTION_START && !hasLocationPermission()) {
            requestAllPermissions()
            return
        }

        val serviceIntent = Intent(this, LocationService::class.java).apply {
            this.action = action
        }

        if (action == LocationService.ACTION_START) {
            ContextCompat.startForegroundService(this, serviceIntent)
        } else {
            stopService(serviceIntent)
        }
    }

    private fun saveActivityData() {
        // DITAMBAH: Log untuk debugging, untuk memastikan fungsi ini terpanggil
        Log.d("UserActivity_SAVE_DEBUG", "Fungsi saveActivityData() mulai dijalankan.")

        val finalDistance = LocationService.totalDistance.value ?: 0f
        val vehicle = this.selectedVehicle ?: "Tidak Diketahui"

        val endTime = System.currentTimeMillis()
        val durationInSeconds = (endTime - activityStartTime) / 1000f

        // Cek apakah durasi valid (lebih dari 0)
        if (activityStartTime == 0L) {
            Log.e("UserActivity_SAVE_DEBUG", "Gagal menyimpan: activityStartTime adalah 0.")
            Toast.makeText(this, "Gagal menyimpan, waktu mulai tidak valid.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = userManager.UserSession()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val pointsEarned = (finalDistance / 0.01).toInt()

        val activityManager = ActivityManager(this)
        activityManager.addActivity(
            userId = userId,
            type = "General",
            distance = finalDistance,
            duration = durationInSeconds,
            date = currentDate,
            pointsEarned = pointsEarned,
            vehicle = vehicle
        )

        Log.d("UserActivity_SAVE_DEBUG", "Data berhasil dikirim ke ActivityManager.")
        Toast.makeText(this, "Aktivitas berhasil disimpan!", Toast.LENGTH_LONG).show()
        resetSessionData()
    }

    private fun resetSessionData() {
        selectedVehicle = null
        activityStartTime = 0L
        LocationService.totalDistance.postValue(0f)
        binding.editTextVehicle.text?.clear()
    }

    // ... sisa kode manajemen izin tetap sama ...
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestAllPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        if (!hasLocationPermission()) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!hasNotificationPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}