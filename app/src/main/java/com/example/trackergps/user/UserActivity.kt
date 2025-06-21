package com.example.trackergps.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.trackergps.LocationService
import com.example.trackergps.databinding.FragmentUserActivityBinding
import com.example.trackergps.user.ActivityManager
import java.text.SimpleDateFormat
import java.util.*

class ActivityUserFragment : Fragment() {

    private var _binding: FragmentUserActivityBinding? = null
    private val binding get() = _binding!!

    private var isCurrentlyTracking = false
    private var selectedVehicle: String? = null
    private var activityStartTime: Long = 0L

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            Toast.makeText(requireContext(), "Izin lokasi & notifikasi dibutuhkan", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestAllPermissions()
        observeServiceData()

        binding.btnStart.setOnClickListener {
            if (isCurrentlyTracking) {
                sendCommandToService(LocationService.ACTION_STOP)
                saveActivityData()
            } else {
                val vehicle = binding.editTextVehicle.text.toString().trim()
                if (vehicle.isEmpty()) {
                    Toast.makeText(requireContext(), "Silakan isi jenis kendaraan", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                selectedVehicle = vehicle
                activityStartTime = System.currentTimeMillis()
                sendCommandToService(LocationService.ACTION_START)
            }
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeServiceData() {
        LocationService.isTracking.observe(viewLifecycleOwner) { isTracking ->
            this.isCurrentlyTracking = isTracking
            updateButtonUI(isTracking)
        }

        LocationService.totalDistance.observe(viewLifecycleOwner) { distanceInMeters ->
            val distanceInKm = distanceInMeters / 1000f
            val distanceText = String.format("%.2f km", distanceInKm)
            // Log saja untuk sekarang
            println("Jarak terbaru: $distanceText")
        }
    }

    private fun updateButtonUI(isTracking: Boolean) {
        binding.btnStart.text = if (isTracking) "Selesai" else "Mulai"
        binding.editTextVehicle.isEnabled = !isTracking
    }

    private fun sendCommandToService(action: String) {
        if (action == LocationService.ACTION_START && !hasLocationPermission()) {
            requestAllPermissions()
            return
        }

        val intent = Intent(requireContext(), LocationService::class.java).apply {
            this.action = action
        }

        if (action == LocationService.ACTION_START) {
            ContextCompat.startForegroundService(requireContext(), intent)
        } else {
            requireContext().stopService(intent)
        }
    }

    private fun saveActivityData() {
        val finalDistance = LocationService.totalDistance.value ?: 0f
        val vehicle = selectedVehicle ?: "Tidak Diketahui"
        val endTime = System.currentTimeMillis()
        val durationInSeconds = (endTime - activityStartTime) / 1000f
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val pointsEarned = (finalDistance / 100).toInt()

        val userId = 1 // Ganti dengan user ID sesungguhnya
        val activityManager = ActivityManager(requireContext())
        activityManager.addActivity(
            userId, "General", finalDistance, durationInSeconds,
            currentDate, pointsEarned, vehicle
        )

        Toast.makeText(requireContext(), "Aktivitas disimpan!", Toast.LENGTH_SHORT).show()
        resetSessionData()
    }

    private fun resetSessionData() {
        selectedVehicle = null
        activityStartTime = 0L
        LocationService.totalDistance.postValue(0f)
        binding.editTextVehicle.text?.clear()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun requestAllPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        if (!hasLocationPermission()) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!hasNotificationPermission() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
