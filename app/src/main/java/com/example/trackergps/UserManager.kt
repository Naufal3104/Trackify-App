package com.example.trackergps

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log

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
    }

    fun addUser (name: String, email: String, password: String, role: Int, preferred_transport: String, total_distance: Int, reward_points: Int) {
        val db = dbManager.writableDatabase
        val values = ContentValues()
        values.put(NAME, name)
        values.put(EMAIL, email)
        values.put(PASSWORD, password)
        values.put(ROLE, role)
        values.put(PREFERRED_TRANSPORT, preferred_transport)
        values.put(TOTAL_DISTANCE, total_distance)
        values.put(REWARD_POINTS, reward_points)
        db.insert(TABLE_NAME, null, values)
        db.close()
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

    fun updateUser(id: Int, name: String, email: String, password: String, role: Int, preferred_transport: String, total_distance: Int, reward_points: Int) {
        val db = dbManager.writableDatabase
        val values = ContentValues()
        values.put(NAME, name)
        values.put(EMAIL, email)
        values.put(PASSWORD, password)
        values.put(ROLE, role)
        values.put(PREFERRED_TRANSPORT, preferred_transport)
        values.put(TOTAL_DISTANCE, total_distance)
        values.put(REWARD_POINTS, reward_points)
        db.update(TABLE_NAME, values, "$ID = ?", arrayOf(id.toString()))
        db.close()
    }


    fun login(email: String, password: String): Int? {
        val db = dbManager.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $EMAIL = ? AND $PASSWORD = ?", arrayOf(email, password))
        if (cursor.moveToFirst()) {
            val role = cursor.getInt(cursor.getColumnIndexOrThrow("role")) // Ambil role dari database
            cursor.close()
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