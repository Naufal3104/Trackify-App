package com.example.trackergps

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.trackergps.general.DatabaseManager

class UserManager(private val context: Context) {

    companion object {
        const val TABLE_NAME = "users"
        const val ID = "id"
        const val NAME = "name"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val ROLE = "role"
        const val PREFERRED_TRANSPORT = "preferred_transport"
        const val TOTAL_DISTANCE = "total_distance"
        const val REWARD_POINTS = "reward_points"
    }

    fun login(email: String, password: String): Int? {
        val db: SQLiteDatabase = DatabaseManager(context).readableDatabase
        val cursor = db.rawQuery(
            "SELECT $ROLE FROM $TABLE_NAME WHERE $EMAIL = ? AND $PASSWORD = ?",
            arrayOf(email, password)
        )
        var role: Int? = null
        if (cursor.moveToFirst()) {
            role = cursor.getInt(cursor.getColumnIndexOrThrow(ROLE))
        }
        cursor.close()
        db.close()
        return role
    }
    fun addUser(
        name: String,
        email: String,
        password: String,
        role: Int,
        preferredTransport: String,
        totalDistance: Double,
        rewardPoints: Int
    ) {
        val db = DatabaseManager(context).writableDatabase
        val values = android.content.ContentValues().apply {
            put(NAME, name)
            put(EMAIL, email)
            put(PASSWORD, password)
            put(ROLE, role)
            put(PREFERRED_TRANSPORT, preferredTransport)
            put(TOTAL_DISTANCE, totalDistance)
            put(REWARD_POINTS, rewardPoints)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

}
