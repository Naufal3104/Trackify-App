package com.example.trackergps

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackergps.databinding.ActivityDashboardBinding
import java.util.Calendar

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var userManager: UserManager
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager = UserManager(this)

        // Mengatur padding agar konten tidak tertutup status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mengambil ID pengguna dari Intent
        currentUserId = intent.getIntExtra("USER_ID", -1)
        if (currentUserId == -1) {
            // Jika tidak ada USER_ID, mungkin arahkan kembali ke Login
            // atau tampilkan pesan error dan tutup activity
            finish()
            return
        }

        loadDashboardData()
        setupBottomNavigation()
    }

    @SuppressLint("SetTextI18n")
    private fun loadDashboardData() {
        // PENTING: Pastikan Anda sudah menambahkan fungsi `getUserById` di UserManager.kt
        val cursor = userManager.getUserById(currentUserId)
        if (cursor != null && cursor.moveToFirst()) {
            // Disarankan untuk mengambil index kolom sekali saja untuk efisiensi
            val nameIndex = cursor.getColumnIndex(UserManager.NAME)
            val distanceIndex = cursor.getColumnIndex(UserManager.TOTAL_DISTANCE)
            val pointsIndex = cursor.getColumnIndex(UserManager.REWARD_POINTS)

            if (nameIndex != -1 && distanceIndex != -1 && pointsIndex != -1) {
                val name = cursor.getString(nameIndex)
                val totalDistance = cursor.getInt(distanceIndex)
                val rewardPoints = cursor.getInt(pointsIndex)

                // Menampilkan data ke UI
                binding.textViewGreeting.text = getGreeting()
                binding.textViewUserName.text = name
                binding.textViewTotalDistance.text = "$totalDistance KM"
                binding.textViewRewardPoints.text = "$rewardPoints Poin"
            }
            cursor.close()
        } else {
            binding.textViewUserName.text = "Data tidak ditemukan"
        }
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

    // --- FUNGSI INI TELAH DIPERBAIKI ---
    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.home // Asumsi ID untuk home adalah 'home'

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Sudah di halaman ini
                    true
                }
                R.id.profile -> {
                    // DIUBAH: Mengarah ke halaman profil PENGGUNA yang benar
                    val intent = Intent(this, UserProfileActivity::class.java)
                    // Anda sudah benar dengan mengirim USER_ID
                    intent.putExtra("USER_ID", currentUserId)
                    // DITAMBAHKAN: Flags untuk navigasi yang lebih baik
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    // PENTING: Anda perlu membuat fungsi getUserById di dalam UserManager.kt
    // Contoh implementasinya:
    /*
    // Di dalam kelas UserManager.kt
    fun getUserById(id: Int): Cursor? {
        val db = dbManager.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $ID = ?"
        return db.rawQuery(query, arrayOf(id.toString()))
    }
    */
}