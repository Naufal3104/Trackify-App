package com.example.trackergps

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    private lateinit var textSignIn: TextView
    private lateinit var userManager: UserManager
    private lateinit var editTextEmailLogin : EditText
    private lateinit var editTextPasswordLogin : EditText
    private lateinit var btnLogin: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        userManager = UserManager(this)
        textSignIn = findViewById(R.id.textSignIn)
        editTextEmailLogin = findViewById(R.id.editTextEmailLogin)
        editTextPasswordLogin = findViewById(R.id.editTextPasswordLogin)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = editTextEmailLogin.text.toString()
            val password = editTextPasswordLogin.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                userManager.createTable()  // Pastikan tabel sudah ada sebelum login
                val cursor = userManager.getAllUsers()

                if (cursor.moveToFirst()) {
                    do {
                        val dbEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                        val dbPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"))
                        println("User di DB: $dbEmail - $dbPassword")
                    } while (cursor.moveToNext())
                }

                if (userManager.login(email, password)) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish() // Selesaikan LoginActivity agar tidak bisa kembali dengan back
                } else {
                    Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }


        textSignIn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun clearFields() {
        editTextEmailLogin.text.clear()
        editTextPasswordLogin.text.clear()
    }
}