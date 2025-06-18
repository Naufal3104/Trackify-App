package com.example.trackergps // Ganti dengan package aplikasi Anda

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.trackergps.databinding.ActivityProfileUserBinding

// Nama class tetap PascalCase (praktik terbaik), meskipun nama filenya user_profile.kt
class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserData()
        setupClickListeners()
        setupBottomNavigation()
    }

    private fun loadUserData() {
        // TODO: Ganti data statis ini dengan data dari database atau SharedPreferences
        binding.textUserName.text = "Naufal"
        binding.textUserLocation.text = "Surabaya, Indonesia"
        binding.textTotalJarak.text = "24.8Km"
        binding.textTotalPoin.text = "320"
    }

    private fun setupClickListeners() {
        binding.buttonTukarPoin.setOnClickListener {
            Toast.makeText(this, "Fitur Tukar Poin belum tersedia", Toast.LENGTH_SHORT).show()
        }
        binding.buttonVoucherSaya.setOnClickListener {
            Toast.makeText(this, "Fitur Voucher Saya belum tersedia", Toast.LENGTH_SHORT).show()
        }
        binding.buttonKeluar.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Keluar")
            .setMessage("Apakah Anda yakin ingin keluar dari akun Anda?")
            .setPositiveButton("Ya") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun performLogout() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        Toast.makeText(this, "Anda telah keluar.", Toast.LENGTH_SHORT).show()

        // GANTI LoginActivity::class.java dengan Activity Login Anda yang sebenarnya
        // val intent = Intent(this, LoginActivity::class.java)
        // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // startActivity(intent)
        // finish()
    }

    private fun setupBottomNavigation() {
        // Logika untuk BottomNavigationView tetap sama
        // Pastikan Anda memiliki menu dengan id yang sesuai
        // binding.bottomNavigationView.selectedItemId = R.id.nav_profil

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // ... (tambahkan logika navigasi Anda di sini)
                else -> false
            }
        }
    }
}