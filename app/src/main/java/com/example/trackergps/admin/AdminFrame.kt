package com.example.trackergps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.trackergps.databinding.FrameAdminBinding
import com.example.trackergps.admin.AdminFragmentManage
import com.example.trackergps.fragment.AdminHome
import com.example.trackergps.fragment.ProfileAdminFragment

class AdminFrame : AppCompatActivity() {

    private lateinit var binding: FrameAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FrameAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(AdminHome())
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(AdminHome())
                R.id.manage -> replaceFragment(AdminFragmentManage())
                R.id.profile -> replaceFragment(ProfileAdminFragment())
                else -> {
                }
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}