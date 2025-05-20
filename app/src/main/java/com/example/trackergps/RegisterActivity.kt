package com.example.trackergps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.TextView
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    private lateinit var register: UserManager
    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPreferredTransport: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var textLogin: TextView
    private lateinit var chkValidation: CheckBox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        register = UserManager(this)
        editTextName = findViewById(R.id.editTextName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextPreferredTransport = findViewById(R.id.editTextPreferredTransport)
        btnRegister = findViewById(R.id.btnRegister)
        textLogin = findViewById(R.id.textLogin)
        chkValidation = findViewById(R.id.chkValidation)

        textLogin.setOnClickListener(){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnRegister.setOnClickListener {
            registerUser()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun registerUser () {
        // Get input from EditText fields
        val name = editTextName.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()
        val preferred_transport = editTextPreferredTransport.text.toString()

        // Check if fields are not empty
        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            if (chkValidation.isChecked){
                register.addUser (name, email, password, 1, preferred_transport, 0, 0)
                Toast.makeText(this, "User  registered successfully", Toast.LENGTH_SHORT).show()
                clearFields()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                clearFields()
                finish()
            }else{
                Toast.makeText(this, "Please accept the Privacy Policy and Terms of Service!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
        editTextName.text.clear()
        editTextEmail.text.clear()
        editTextPassword.text.clear()
    }
}