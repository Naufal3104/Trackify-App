package com.example.trackergps

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackergps.admin.Manage
//import com.example.trackergps.admin.ProfileActivity
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currentUserId = userManager.UserSession()

        loadDashboardData()
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.home
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

    @SuppressLint("SetTextI18n")
    private fun loadDashboardData() {
        val cursor = userManager.getUserById(currentUserId)
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range")
            val name = cursor.getString(cursor.getColumnIndex(UserManager.NAME))
            @SuppressLint("Range")
            val totalDistance = cursor.getInt(cursor.getColumnIndex(UserManager.TOTAL_DISTANCE))
            @SuppressLint("Range")
            val rewardPoints = cursor.getInt(cursor.getColumnIndex(UserManager.REWARD_POINTS))
            cursor.close()

            binding.textViewGreeting.text = getGreeting()
            binding.textViewUserName.text = name
            binding.textViewTotalDistance.text = "$totalDistance KM"
            binding.textViewRewardPoints.text = "$rewardPoints Poin"

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

}
