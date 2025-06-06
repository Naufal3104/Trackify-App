package com.example.trackergps

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class VoucherManager(context: Context) {
    private val dbManager = DatabaseManager(context)
    companion object{
        const val VOUCHERS_TABLE_NAME = "vouchers"
        const val VOUCHERS_ID = "id"
        const val VOUCHERS_TITLE = "title"
        const val VOUCHERS_DESCRIPTION = "description"
        const val VOUCHERS_POINTS_REQUIRED = "points_required"
        const val VOUCHERS_EXPIRY_DATE = "expiry_date"
        const val VOUCHERS_STOCK = "stock"
    }

    fun addVoucher(title: String, description: String, pointsRequired: Int, expiryDate: String, stock: Int) {
        val db = dbManager.writableDatabase
        val values = ContentValues()
        values.put(VOUCHERS_TITLE, title)
        values.put(VOUCHERS_DESCRIPTION, description)
        values.put(VOUCHERS_POINTS_REQUIRED, pointsRequired)
        values.put(VOUCHERS_EXPIRY_DATE, expiryDate)
        values.put(VOUCHERS_STOCK, stock)
        db.insert(VOUCHERS_TABLE_NAME, null, values)
        db.close()
    }

    fun getAllVouchers(): Cursor {
        val db = dbManager.readableDatabase
        return db.rawQuery("SELECT * FROM $VOUCHERS_TABLE_NAME", null)
    }

    fun updateVoucher(id: Int, title: String, description: String, pointsRequired: Int, expiryDate: String, stock: Int) {
        val db = dbManager.writableDatabase
        val values = ContentValues()
        values.put(VOUCHERS_TITLE, title)
        values.put(VOUCHERS_DESCRIPTION, description)
        values.put(VOUCHERS_POINTS_REQUIRED, pointsRequired)
        values.put(VOUCHERS_EXPIRY_DATE, expiryDate)
        values.put(VOUCHERS_STOCK, stock)
        db.update(VOUCHERS_TABLE_NAME, values, "$VOUCHERS_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteVoucher(id: Int) {
        val db = dbManager.writableDatabase
        db.delete(VOUCHERS_TABLE_NAME, "$VOUCHERS_ID = ?", arrayOf(id.toString()))
        db.close()
    }
}