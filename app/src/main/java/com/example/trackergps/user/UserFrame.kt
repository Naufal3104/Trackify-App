package com.example.trackergps.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.trackergps.R
import com.example.trackergps.databinding.FrameUserBinding
import com.example.trackergps.fragment.HomeUserFragment
import com.example.trackergps.fragment.ProfileUserFragment
import com.example.trackergps.fragment.ActivityUserFragment

class UserFrame : AppCompatActivity() {

    private lateinit var binding: FrameUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FrameUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tampilkan fragment default
        replaceFragment(HomeUserFragment())

        // Inset sistem (status bar dsb)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Bottom navigation handler
        binding.bottomNavigationViewUser.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home_user -> replaceFragment(HomeUserFragment())
                R.id.nav_activity_user -> replaceFragment(ActivityUserFragment())
                R.id.nav_profile_user -> replaceFragment(ProfileUserFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}
