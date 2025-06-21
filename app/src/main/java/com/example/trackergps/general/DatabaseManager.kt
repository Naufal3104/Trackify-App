package com.example.trackergps.general

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.trackergps.UserManager

class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Menggunakan nama database dan versi dari file yang lebih baru
        private const val DATABASE_NAME = "tracker_gps.db"
        private const val DATABASE_VERSION = 4
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Membuat Tabel Users (menggabungkan definisi terbaik dari kedua file)
        val createUsersTable = """
            CREATE TABLE IF NOT EXISTS ${UserManager.TABLE_NAME} (
                ${UserManager.ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                ${UserManager.NAME} TEXT NOT NULL,
                ${UserManager.EMAIL} TEXT NOT NULL UNIQUE,
                ${UserManager.PASSWORD} TEXT NOT NULL,
                ${UserManager.ROLE} INTEGER NOT NULL,
                ${UserManager.PREFERRED_TRANSPORT} TEXT NOT NULL,
                ${UserManager.TOTAL_DISTANCE} REAL NOT NULL, 
                ${UserManager.REWARD_POINTS} INTEGER NOT NULL
            );
        """.trimIndent()
        db.execSQL(createUsersTable)

        // 5. Mengisi data pengguna default (diambil dari file kedua)
        initializeDefaultUsers(db)
    }

    private fun initializeDefaultUsers(db: SQLiteDatabase) {
        // Fungsi ini untuk menambahkan data awal agar aplikasi tidak crash
        // Data untuk Admin
        val adminValues = ContentValues().apply {
            put(UserManager.NAME, "Admin")
            put(UserManager.EMAIL, "admin@gmail.com")
            put(UserManager.PASSWORD, "admin")
            put(UserManager.ROLE, 0) // 0 untuk admin
            put(UserManager.PREFERRED_TRANSPORT, "")
            put(UserManager.TOTAL_DISTANCE, 0.0)
            put(UserManager.REWARD_POINTS, 0)
        }
        db.insert(UserManager.TABLE_NAME, null, adminValues)

        // Data untuk User
        val userValues = ContentValues().apply {
            put(UserManager.NAME, "User")
            put(UserManager.EMAIL, "user@gmail.com")
            put(UserManager.PASSWORD, "user")
            put(UserManager.ROLE, 1) // 1 untuk user
            put(UserManager.PREFERRED_TRANSPORT, "Sepeda")
            put(UserManager.TOTAL_DISTANCE, 0.0)
            put(UserManager.REWARD_POINTS, 0)
        }
        db.insert(UserManager.TABLE_NAME, null, userValues)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Metode onUpgrade yang lebih lengkap, menghapus semua tabel yang ada
        // Penting: Pendekatan ini akan menghapus semua data yang ada.
        db.execSQL("DROP TABLE IF EXISTS ${UserManager.TABLE_NAME}")
        onCreate(db)
    }
}