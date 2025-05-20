package com.example.trackergps

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "tracker_gps"
        private const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase){
        val createTable = """
            CREATE TABLE IF NOT EXISTS ${UserManager.TABLE_NAME} (
                ${UserManager.ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                ${UserManager.NAME} TEXT NOT NULL,
                ${UserManager.EMAIL} TEXT NOT NULL,
                ${UserManager.PASSWORD} TEXT NOT NULL,
                ${UserManager.ROLE} INTEGER NOT NULL,
                ${UserManager.PREFERRED_TRANSPORT} TEXT NOT NULL,
                ${UserManager.TOTAL_DISTANCE} INTEGER NOT NULL,
                ${UserManager.REWARD_POINTS} INTEGER NOT NULL
            );
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }
}