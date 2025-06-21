package com.example.trackergps

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class UserManager(context: Context) {
    private val dbManager = DatabaseManager(context)
    companion object {
        const val TABLE_NAME = "users"
        const val ID = "_id"
        const val NAME = "name"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val ROLE = "role"
        const val PREFERRED_TRANSPORT = "preferred_transport"
        const val TOTAL_DISTANCE = "total_distance"
        const val REWARD_POINTS = "reward_points"
        const val PROFILE_IMAGE_URI = "profile_image_uri"
        private var loggedInUserId: Int = -1
    }

    fun addUser (name: String, email: String, password: String, role: Int, preferred_transport: String, total_distance: Int, reward_points: Int) {
        val db = dbManager.writableDatabase
        val values = ContentValues().apply {
            put(NAME, name)
            put(EMAIL, email)
            put(PASSWORD, password)
            put(ROLE, role)
            put(PREFERRED_TRANSPORT, preferred_transport)
            put(TOTAL_DISTANCE, total_distance)
            put(REWARD_POINTS, reward_points)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updateUserProfileData(userId: Int, name: String, email: String, newPassword: String?) {
        val db = dbManager.writableDatabase
        val values = ContentValues().apply {
            put(NAME, name)
            put(EMAIL, email)
            if (!newPassword.isNullOrBlank()) {
                put(PASSWORD, newPassword)
            }
        }
        db.update(TABLE_NAME, values, "$ID = ?", arrayOf(userId.toString()))
        db.close()
    }

    fun updateUserProfileImage(userId: Int, imageUri: String) {
        val db = dbManager.writableDatabase
        val values = ContentValues().apply {
            put(PROFILE_IMAGE_URI, imageUri)
        }
        db.update(TABLE_NAME, values, "$ID = ?", arrayOf(userId.toString()))
        db.close()
    }

    /**
     * Mengurangi poin pengguna.
     * @return true jika poin berhasil dikurangi, false jika poin tidak cukup.
     */
    fun deductPoints(userId: Int, pointsToDeduct: Int): Boolean {
        val db = dbManager.writableDatabase
        val userCursor = getUserById(userId)
        var success = false

        userCursor?.use {
            if (it.moveToFirst()) {
                val currentPointsIndex = it.getColumnIndex(REWARD_POINTS)
                if (currentPointsIndex != -1) {
                    val currentPoints = it.getInt(currentPointsIndex)
                    if (currentPoints >= pointsToDeduct) {
                        val newPoints = currentPoints - pointsToDeduct
                        val values = ContentValues().apply {
                            put(REWARD_POINTS, newPoints)
                        }
                        db.update(TABLE_NAME, values, "$ID = ?", arrayOf(userId.toString()))
                        success = true
                    }
                }
            }
        }
        // Jangan tutup koneksi db di sini jika Anda akan memanggil fungsi lain setelahnya dalam satu transaksi
        return success
    }

    fun logout() {
        loggedInUserId = -1
    }

    fun getAllUsers(): Cursor {
        val db = dbManager.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun deleteUser (id: Int) {
        val db = dbManager.writableDatabase
        db.delete(TABLE_NAME, "$ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun UserSession(): Int{
        return loggedInUserId
    }

    fun login(email: String, password: String): Int? {
        val db = dbManager.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $EMAIL = ? AND $PASSWORD = ?", arrayOf(email, password))
        if (cursor.moveToFirst()) {
            val role = cursor.getInt(cursor.getColumnIndexOrThrow("role"))
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
            cursor.close()
            loggedInUserId = id
            return role
        } else {
            cursor.close()
            return null
        }
    }

    fun getUserById(id: Int): Cursor? {
        val db = dbManager.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $ID = ?", arrayOf(id.toString()))
    }

    fun initializeAdminUser() {
        val adminEmail = "admin@gmail.com"
        val db = dbManager.readableDatabase
        var cursor: Cursor? = null

        try {
            val query = "SELECT * FROM $TABLE_NAME WHERE $EMAIL = ?"
            cursor = db.rawQuery(query, arrayOf(adminEmail))
            if (cursor.count == 0) {
                addUser(
                    name = "Admin",
                    email = adminEmail,
                    password = "admin",
                    role = 0,
                    preferred_transport = "0",
                    total_distance = 0,
                    reward_points = 0
                )
            }
        } finally {
            cursor?.close()
        }
    }
}
