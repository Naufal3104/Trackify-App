package com.example.trackergps

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackergps.databinding.ActivitySplashscreenBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashscreenBinding
    private val SPLASH_DELAY: Long = 2500 // Durasi splash screen 2.5 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handler untuk menunda perpindahan ke activity berikutnya
        Handler(Looper.getMainLooper()).postDelayed({
            // Buat Intent untuk pindah ke LoginActivity
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)

            // Tutup activity ini agar tidak bisa kembali ke splash screen
            finish()
        }, SPLASH_DELAY)
    }
}
