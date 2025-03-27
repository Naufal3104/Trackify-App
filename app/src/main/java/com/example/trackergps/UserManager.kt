package com.example.trackergps

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class UserManager(context: Context) {
    private val dbManager = DatabaseManager(context)
    companion object {
        private const val TABLE_NAME = "users"
        private const val ID = "_id"
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val PASSWORD = "password"
        private const val PREFERRED_TRANSPORT = "preferred_transport"
        private const val TOTAL_DISTANCE = "total_distance"
        private const val REWARD_POINTS = "reward_points"
    }

    fun createTable(){
        val db = dbManager.writableDatabase
        val createTable = "CREATE TABLE IF NOT EXISTS $TABLE_NAME ($ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "$NAME TEXT NOT NULL, " +
                "$EMAIL TEXT NOT NULL, " +
                "$PASSWORD TEXT NOT NULL, " +
                "$PREFERRED_TRANSPORT TEXT NOT NULL, " +
                "$TOTAL_DISTANCE INT NOT NULL, " +
                "$REWARD_POINTS INT NOT NULL)"
        db.execSQL(createTable)
        db.close()
    }

    fun addUser (name: String, email: String, password: String, preferred_transport: String, total_distance: Int, reward_points: Int) {
        val db = dbManager.writableDatabase
        val values = ContentValues()
        values.put(NAME, name)
        values.put(EMAIL, email)
        values.put(PASSWORD, password)
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

    fun login(email: String, password: String): Boolean{
        val db = dbManager.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $EMAIL = ? AND $PASSWORD = ?", arrayOf(email, password))

        val userExists = cursor.count > 0
        if (userExists) {
            println("Login berhasil untuk email: $email") // Debugging
        } else {
            println("Login gagal untuk email: $email") // Debugging
        }
        cursor.close()
        return userExists
    }
}