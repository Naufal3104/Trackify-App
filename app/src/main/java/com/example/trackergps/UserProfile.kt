package com.example.trackergps

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackergps.databinding.ActivityUserProfileBinding
import java.io.File

class UserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var userManager: UserManager
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userManager = UserManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currentUserId = userManager.UserSession()

        if (currentUserId == -1) {
            navigateToLogin()
            return
        }

        setupBottomNavigation()
        setupClickListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun loadProfileData() {
        val userCursor = userManager.getUserById(currentUserId)

        userCursor?.use {
            if (it.moveToFirst()) {
                // [FIX] Mengambil indeks kolom terlebih dahulu untuk keamanan
                val nameIndex = it.getColumnIndex(UserManager.NAME)
                val distanceIndex = it.getColumnIndex(UserManager.TOTAL_DISTANCE)
                val pointsIndex = it.getColumnIndex(UserManager.REWARD_POINTS)
                val imagePathIndex = it.getColumnIndex(UserManager.PROFILE_IMAGE_URI)

                // [FIX] Mengambil data dengan memeriksa apakah indeks valid (-1 berarti tidak ditemukan)
                val name = if (nameIndex != -1) it.getString(nameIndex) else "Nama Tidak Tersedia"
                val totalDistance = if (distanceIndex != -1) it.getFloat(distanceIndex) else 0f
                val rewardPoints = if (pointsIndex != -1) it.getInt(pointsIndex) else 0
                val imagePath = if (imagePathIndex != -1) it.getString(imagePathIndex) else null

                binding.textViewName.text = name
                binding.textViewDistanceValue.text = "%.2f Km".format(totalDistance / 1000f)
                binding.textViewPointsValue.text = rewardPoints.toString()

                if (!imagePath.isNullOrEmpty()) {
                    val file = File(imagePath)
                    if (file.exists()) {
                        binding.imageViewProfile.setImageURI(Uri.fromFile(file))
                    } else {
                        binding.imageViewProfile.setImageResource(R.drawable.ic_edit_profile)
                    }
                } else {
                    binding.imageViewProfile.setImageResource(R.drawable.ic_edit_profile)
                }
            } else {
                binding.textViewName.text = "User tidak ditemukan"
                binding.textViewDistanceValue.text = "0 Km"
                binding.textViewPointsValue.text = "0"
            }
        }
    }

    private fun setupClickListeners() {
        binding.buttonLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
        binding.textViewEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfile::class.java))
        }
        binding.textViewRedeemPoints.setOnClickListener {
             startActivity(Intent(this, ReedeemVoucher::class.java))
        }
        binding.textViewMyVouchers.setOnClickListener {
             startActivity(Intent(this, UserVoucher::class.java))
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Keluar")
            .setMessage("Apakah Anda yakin ingin keluar dari akun Anda?")
            .setPositiveButton("Ya") { _, _ ->
                userManager.logout()
                navigateToLogin()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.profile
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, DashboardActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    true
                }
                R.id.activity -> {
                    startActivity(Intent(this, UserActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    })
                    true
                }
                R.id.profile -> true
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
    }
}
