package com.example.trackergps.user

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ActivityManager(context: Context) :
    SQLiteOpenHelper(context, "ActivityDatabase", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE activities (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER,
                type TEXT,
                distance REAL,
                duration REAL,
                date TEXT,
                pointsEarned INTEGER,
                vehicle TEXT
            );
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS activities")
        onCreate(db)
    }

    fun addActivity(
        userId: Int,
        type: String,
        distance: Float,
        duration: Float,
        date: String,
        pointsEarned: Int,
        vehicle: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("userId", userId)
            put("type", type)
            put("distance", distance)
            put("duration", duration)
            put("date", date)
            put("pointsEarned", pointsEarned)
            put("vehicle", vehicle)
        }
        db.insert("activities", null, values)
        db.close()
    }
}
