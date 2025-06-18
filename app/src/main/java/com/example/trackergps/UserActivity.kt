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
    private var isCurrentlyTracking = false

    // DITAMBAH: Variabel untuk menyimpan data selama sesi aktivitas berlangsung
    private var selectedVehicle: String? = null
    private var activityStartTime: Long = 0L

    // DITAMBAH: Cara modern untuk meminta izin
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

        // Minta semua izin yang diperlukan saat aplikasi dibuka
        requestAllPermissions()

        // DIUBAH: Logika tombol yang lebih lengkap
        binding.btnStart.setOnClickListener {
            if (isCurrentlyTracking) {
                // Jika sedang berjalan, maka hentikan service dan simpan data
                sendCommandToService(LocationService.ACTION_STOP)
                saveActivityData()
            } else {
                // Jika akan memulai, ambil data dari UI
                val vehicle = binding.editTextVehicle.text.toString().trim()
                if (vehicle.isEmpty()) {
                    Toast.makeText(this, "Silakan masukkan jenis kendaraan terlebih dahulu.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Simpan data sesi sebelum memulai service
                this.selectedVehicle = vehicle
                this.activityStartTime = System.currentTimeMillis()

                // Mulai service
                sendCommandToService(LocationService.ACTION_START)
            }
        }

        observeServiceData()
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.activity
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    true
                }
                R.id.activity -> {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.profile -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun observeServiceData() {
        LocationService.isTracking.observe(this) { isTracking ->
            this.isCurrentlyTracking = isTracking
            updateButtonUI(isTracking)
        }

        LocationService.totalDistance.observe(this) { distanceInMeters ->
            val distanceInKm = distanceInMeters / 1000f
            val distanceText = String.format("%.2f km", distanceInKm)

            // CATATAN: Pastikan Anda punya TextView dengan id 'tvJarak' di XML Anda
            // Contoh: binding.tvJarak.text = distanceText
            Log.d("UserActivity", "Jarak terbaru: $distanceText")
        }
    }

    private fun updateButtonUI(isTracking: Boolean) {
        if (isTracking) {
            binding.btnStart.text = "Selesai"
            binding.editTextVehicle.isEnabled = false // Non-aktifkan input saat berjalan
        } else {
            binding.btnStart.text = "Mulai"
            binding.editTextVehicle.isEnabled = true // Aktifkan kembali input
        }
    }

    // DIUBAH: Fungsi ini sekarang mengirim aksi yang jelas ke service
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

    // DITAMBAH: Fungsi untuk mengumpulkan dan menyimpan data aktivitas
    private fun saveActivityData() {
        val finalDistance = LocationService.totalDistance.value ?: 0f
        val vehicle = this.selectedVehicle ?: "Tidak Diketahui"

        val endTime = System.currentTimeMillis()
        val durationInSeconds = (endTime - activityStartTime) / 1000f

        val userId = 1 // Ganti dengan ID pengguna yang login
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val pointsEarned = (finalDistance / 100).toInt() // Contoh: 1 poin tiap 100m

        val activityManager = ActivityManager(this)
        activityManager.addActivity(
            userId = userId,
            type = "General", // Tipe bisa disesuaikan
            distance = finalDistance,
            duration = durationInSeconds,
            date = currentDate,
            pointsEarned = pointsEarned,
            vehicle = vehicle
        )

        Toast.makeText(this, "Aktivitas berhasil disimpan!", Toast.LENGTH_LONG).show()
        resetSessionData()
    }

    // DITAMBAH: Fungsi untuk mereset state setelah aktivitas selesai
    private fun resetSessionData() {
        selectedVehicle = null
        activityStartTime = 0L
        LocationService.totalDistance.postValue(0f)
        binding.editTextVehicle.text?.clear()
    }

    // --- Manajemen Izin yang Diperbarui ---

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasNotificationPermission(): Boolean {
        // Izin notifikasi hanya diperlukan untuk Android 13 (API 33) ke atas
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Dianggap granted untuk versi di bawahnya
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