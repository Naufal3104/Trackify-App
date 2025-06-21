package com.example.trackergps

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.trackergps.databinding.ActivityEditProfileBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class EditProfile : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userManager: UserManager
    private var currentUserId: Int = -1
    // [DIUBAH] Variabel ini sekarang akan menyimpan path file internal, bukan URI galeri
    private var newProfileImagePath: String? = null

    // Launcher untuk memilih gambar dari galeri
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // Salin gambar yang dipilih ke penyimpanan internal aplikasi
                val filePath = copyImageToInternalStorage(uri)
                if (filePath != null) {
                    newProfileImagePath = filePath
                    // Tampilkan gambar dari file internal yang baru dibuat
                    binding.imageViewProfile.setImageURI(Uri.fromFile(File(filePath)))
                } else {
                    Toast.makeText(this, "Gagal memproses gambar.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager = UserManager(this)
        currentUserId = userManager.UserSession()

        if (currentUserId == -1) {
            Toast.makeText(this, "Sesi tidak ditemukan, silakan login kembali.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupToolbar()
        loadUserData()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbarEditProfile.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    @SuppressLint("Range")
    private fun loadUserData() {
        val cursor = userManager.getUserById(currentUserId)
        cursor?.use {
            if (it.moveToFirst()) {
                val name = it.getString(it.getColumnIndex(UserManager.NAME))
                val email = it.getString(it.getColumnIndex(UserManager.EMAIL))
                val imagePath = it.getString(it.getColumnIndex(UserManager.PROFILE_IMAGE_URI))

                binding.editTextName.setText(name)
                binding.editTextEmail.setText(email)

                // [DIUBAH] Muat gambar dari path file internal
                if (!imagePath.isNullOrEmpty()) {
                    val file = File(imagePath)
                    if (file.exists()) {
                        binding.imageViewProfile.setImageURI(Uri.fromFile(file))
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.textViewChangePhoto.setOnClickListener {
            openGalleryForImage()
        }
        binding.buttonSaveChanges.setOnClickListener {
            saveChanges()
        }
    }

    private fun openGalleryForImage() {
        // Kita bisa kembali menggunakan ACTION_PICK karena kita hanya butuh akses sekali untuk menyalin
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    // [FUNGSI BARU] Untuk menyalin gambar ke penyimpanan internal
    private fun copyImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            // Buat nama file unik berdasarkan waktu
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            // Kembalikan path absolut dari file yang baru dibuat
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveChanges() {
        val name = binding.editTextName.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val newPassword = binding.editTextNewPassword.text.toString()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Nama dan Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        userManager.updateUserProfileData(currentUserId, name, email, newPassword.ifBlank { null })

        // [DIUBAH] Simpan path file internal yang baru
        newProfileImagePath?.let { path ->
            userManager.updateUserProfileImage(currentUserId, path)
        }

        Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
        finish()
    }
}
