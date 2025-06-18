package com.example.trackergps.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog // Import yang dibutuhkan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackergps.R
import com.example.trackergps.databinding.ActivityProfileBinding

// --- PERUBAHAN ---
// Pastikan Anda sudah membuat Activity untuk login, contohnya LoginActivity.kt
// yang menggunakan layout login.xml
// import com.example.trackergps.auth.LoginActivity

class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
        setupBottomNavigation()

        // TODO: Muat data pengguna (nama, email, foto) dari database atau SharedPreferences
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(this, "Membuka halaman Edit Profil...", Toast.LENGTH_SHORT).show()
            // val intent = Intent(this, EditProfileActivity::class.java)
            // startActivity(intent)
        }

        // --- PERUBAHAN DIMULAI DI SINI ---
        // Listener untuk tombol Keluar (Logout)
        binding.btnLogout.setOnClickListener {
            // Tampilkan dialog konfirmasi untuk pengalaman pengguna yang lebih baik
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Keluar")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya") { _, _ ->
                    // Jika pengguna setuju, jalankan fungsi logout
                    performLogout()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }
        // --- AKHIR PERUBAHAN ---
    }

    // --- FUNGSI BARU DITAMBAHKAN ---
    private fun performLogout() {
        // 1. Hapus Sesi Pengguna dari SharedPreferences
        // Ganti "MyAppPrefs" dengan nama file preferensi yang Anda gunakan saat login
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Menghapus semua data sesi (token, status login, dll)
        editor.apply()

        // 2. Arahkan ke Halaman Login dan Bersihkan Riwayat Activity
        Toast.makeText(this, "Anda telah keluar.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
    }
    // --- AKHIR FUNGSI BARU ---

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.selectedItemId = R.id.profile
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, DashboardAdmin::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.manage -> {
                    val intent = Intent(this, Manage::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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

    // --- PERUBAHAN ---
    // Buat kelas LoginActivity palsu di sini jika Anda belum membuatnya,
    // agar kode tidak error saat dikompilasi.
    // Hapus kelas ini jika Anda sudah punya LoginActivity sendiri.
    class LoginActivity : AppCompatActivity() {}
}