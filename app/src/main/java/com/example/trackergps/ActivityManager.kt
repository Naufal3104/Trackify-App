package com.example.trackergps

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class ActivityManager(context: Context) {
    private val dbManager = DatabaseManager(context)

    companion object {
        const val TABLE_NAME = "activities"
        const val ID = "_id"
        const val USER_ID = "user_id"
        const val TYPE = "type"
        const val DISTANCE = "distance"
        const val DURATION = "duration"
        const val DATE = "date"
        const val POINTS_EARNED = "points_earned"
    }

    fun addActivity(
        userId: Int,
        type: String,
        distance: Float,
        duration: Float,
        date: String, // Pertimbangkan menggunakan tipe data tanggal yang lebih kuat jika diperlukan
        pointsEarned: Int
    ) {
        val db = dbManager.writableDatabase
        val values = ContentValues().apply {
            put(USER_ID, userId)
            put(TYPE, type)
            put(DISTANCE, distance)
            put(DURATION, duration)
            put(DATE, date)
            put(POINTS_EARNED, pointsEarned)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllActivities(): Cursor {
        val db = dbManager.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun getActivitiesByUserId(userId: Int): Cursor {
        val db = dbManager.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_ID = ?", arrayOf(userId.toString()))
    }

    fun deleteActivity(id: Int) {
        val db = dbManager.writableDatabase
        db.delete(TABLE_NAME, "$ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun updateActivity(
        id: Int,
        type: String,
        distance: Float,
        duration: Float,
        date: String,
        pointsEarned: Int
    ): Int {
        val db = dbManager.writableDatabase
        val values = ContentValues().apply {
            put(TYPE, type)
            put(DISTANCE, distance)
            put(DURATION, duration)
            put(DATE, date)
            put(POINTS_EARNED, pointsEarned)
        }
        val rowsAffected = db.update(TABLE_NAME, values, "$ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected
    }
}