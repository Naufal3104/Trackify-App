package com.example.trackergps

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

/**
 * Data class untuk merepresentasikan voucher yang sudah ditukar.
 * Didefinisikan di sini agar menjadi satu file dengan Managernya.
 */
data class RedeemedVoucher(
    val id: Int,
    val userId: Int,
    val voucherId: Int,
    val redeemedAt: String,
    val status: String,
    val voucherTitle: String
)

class RedeemedVoucherManager(context: Context) {
    private val dbManager = DatabaseManager(context)

    companion object {
        const val TABLE_NAME = "redeemed_vouchers"
        const val ID = "_id"
        const val USER_ID = "user_id"
        const val VOUCHER_ID = "voucher_id"
        const val REDEEMED_AT = "redeemed_at"
        const val STATUS = "status"
    }

    fun addRedeemedVoucher(
        userId: Int,
        voucherId: Int,
        redeemedAt: String,
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

    /**
     * Fungsi JOIN untuk menggabungkan data dari tabel redeemed_vouchers dan vouchers.
     * @return Cursor yang berisi data gabungan.
     */
    fun getJoinedRedeemedVouchersByUserId(userId: Int): Cursor {
        val db = dbManager.readableDatabase
        val query = """
            SELECT
                rv.${ID},
                rv.${USER_ID},
                rv.${VOUCHER_ID},
                rv.${REDEEMED_AT},
                rv.${STATUS},
                v.${VoucherManager.VOUCHERS_TITLE}
            FROM
                ${TABLE_NAME} AS rv
            JOIN
                ${VoucherManager.VOUCHERS_TABLE_NAME} AS v ON rv.${VOUCHER_ID} = v.${VoucherManager.VOUCHERS_ID}
            WHERE
                rv.${USER_ID} = ?
            ORDER BY rv.${REDEEMED_AT} DESC
        """.trimIndent()
        return db.rawQuery(query, arrayOf(userId.toString()))
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

    // Fungsi lain yang mungkin Anda perlukan
    fun getAllRedeemedVouchers(): Cursor {
        val db = dbManager.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun deleteRedeemedVoucher(id: Int) {
        val db = dbManager.writableDatabase
        db.delete(TABLE_NAME, "$ID = ?", arrayOf(id.toString()))
        db.close()
    }
}
