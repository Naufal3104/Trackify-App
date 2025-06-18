package com.example.trackergps

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class RedeemedVoucherManager(context: Context) {
    private val dbManager = DatabaseManager(context)

    companion object {
        const val TABLE_NAME = "redeemed_vouchers"
        const val ID = "_id"
        const val USER_ID = "user_id"
        const val VOUCHER_ID = "voucher_id"
        const val REDEEMED_AT = "redeemed_at"
        const val STATUS = "status" // e.g., "used", "pending", "cancelled"
    }

    fun addRedeemedVoucher(
        userId: Int,
        voucherId: Int,
        redeemedAt: String, // Format "YYYY-MM-DD HH:MM:SS"
        status: String
    ) {
        val db = dbManager.writableDatabase
        val values = ContentValues().apply {
            put(USER_ID, userId)
            put(VOUCHER_ID, voucherId)
            put(REDEEMED_AT, redeemedAt)
            put(STATUS, status)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllRedeemedVouchers(): Cursor {
        val db = dbManager.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun getRedeemedVouchersByUserId(userId: Int): Cursor {
        val db = dbManager.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_ID = ?", arrayOf(userId.toString()))
    }

    fun getRedeemedVouchersByVoucherId(voucherId: Int): Cursor {
        val db = dbManager.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $VOUCHER_ID = ?", arrayOf(voucherId.toString()))
    }

    fun updateRedeemedVoucherStatus(id: Int, newStatus: String): Int {
        val db = dbManager.writableDatabase
        val values = ContentValues().apply {
            put(STATUS, newStatus)
        }
        val rowsAffected = db.update(TABLE_NAME, values, "$ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected
    }

    fun deleteRedeemedVoucher(id: Int) {
        val db = dbManager.writableDatabase
        db.delete(TABLE_NAME, "$ID = ?", arrayOf(id.toString()))
        db.close()
    }
}