package com.example.trackergps

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var notificationManager: NotificationManager

    companion object {
        // DITAMBAH: Aksi untuk mengontrol service dari luar
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        const val NOTIFICATION_CHANNEL_ID = "location_service_channel"
        const val NOTIFICATION_ID = 1

        val totalDistance = MutableLiveData<Float>()
        val isTracking = MutableLiveData<Boolean>()
    }

    private var lastLocation: Location? = null
    private var accumulatedDistance = 0f

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (isTracking.value == true) {
                        if (lastLocation != null) {
                            val distance = lastLocation!!.distanceTo(location)
                            accumulatedDistance += distance
                            totalDistance.postValue(accumulatedDistance)
                            // DITAMBAH: Update jarak di notifikasi secara real-time
                            updateNotification(accumulatedDistance)
                        }
                        lastLocation = location
                    }
                }
            }
        }
    }

    // DIUBAH: Logika onStartCommand untuk menangani aksi START dan STOP
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTracking()
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startTracking() {
        if (isTracking.value == true) return // Jika sudah berjalan, jangan mulai lagi

        Log.d("LocationService", "Pelacakan dimulai...")
        initValues()
        isTracking.postValue(true)

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000
        ).setMinUpdateIntervalMillis(3000).build()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        startForeground(NOTIFICATION_ID, getNotification("Jarak: 0.00 km"))
    }

    private fun stopTracking() {
        Log.d("LocationService", "Pelacakan dihentikan.")
        stopForeground(true)
        stopSelf()
    }

    // DIUBAH: Fungsi untuk membuat notifikasi sekarang lebih fleksibel
    private fun getNotification(contentText: String): Notification {
        // DITAMBAH: Intent dan PendingIntent untuk tombol "Hentikan"
        val stopIntent = Intent(this, LocationService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingStopIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Buat channel notifikasi jika belum ada
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, "Layanan Lokasi", NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Aktivitas sedang berjalan")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_scooter_illustration)
            .setOngoing(true)
            // DITAMBAH: Menambahkan tombol aksi "Hentikan"
            .addAction(R.drawable.ic_stop, "Hentikan", pendingStopIntent)
            .build()
    }

    // DITAMBAH: Fungsi untuk memperbarui notifikasi yang sudah ada
    private fun updateNotification(distanceInMeters: Float) {
        val distanceInKm = distanceInMeters / 1000f
        val notificationText = "Jarak: %.2f km".format(distanceInKm)
        val notification = getNotification(notificationText)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun initValues() {
        accumulatedDistance = 0f
        lastLocation = null
        totalDistance.postValue(0f)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Pastikan update lokasi berhenti saat service dihancurkan
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isTracking.postValue(false)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}