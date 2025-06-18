package com.example.trackergps.admin

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackergps.R
import com.example.trackergps.databinding.ActivityDashboardAdminBinding
import com.example.trackergps.databinding.ActivityManageBinding

class Manage : AppCompatActivity() {
    private lateinit var binding: ActivityManageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inisialisasi View Binding untuk mengakses elemen UI
        binding = ActivityManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Penanganan insets jendela untuk memastikan konten tidak tertutup oleh status/nav bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnUser.setOnClickListener {
            startActivity(Intent(this, ManageUser::class.java))
            finish()
        }
        binding.btnVoucher.setOnClickListener {
            startActivity(Intent(this, ManageVoucher::class.java))
            finish()
        }
        binding.btnActivity.setOnClickListener{
            startActivity(Intent(this, ManageActivity::class.java))
            finish()
        }
        // Menetapkan listener untuk BottomNavigationView
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.manage
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, DashboardAdmin::class.java)
                    startActivity(intent)
                    true
                }
                R.id.manage -> {
                    true
                }
                R.id.profile -> {
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}