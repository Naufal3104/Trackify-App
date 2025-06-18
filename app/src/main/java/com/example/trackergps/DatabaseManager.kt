package com.example.trackergps

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "tracker_gps"
        private const val DATABASE_VERSION = 4 // Versi database dinaikkan menjadi 4
    }

    override fun onCreate(db: SQLiteDatabase){
        // Tabel Users
        val createUsersTable = """
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
        db.execSQL(createUsersTable)

        // Tabel Vouchers
        val createVouchersTable = """
            CREATE TABLE IF NOT EXISTS ${VoucherManager.VOUCHERS_TABLE_NAME} (
                ${VoucherManager.VOUCHERS_ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                ${VoucherManager.VOUCHERS_TITLE} TEXT NOT NULL,
                ${VoucherManager.VOUCHERS_DESCRIPTION} TEXT NOT NULL,
                ${VoucherManager.VOUCHERS_POINTS_REQUIRED} INTEGER NOT NULL,
                ${VoucherManager.VOUCHERS_EXPIRY_DATE} DATE NOT NULL,
                ${VoucherManager.VOUCHERS_STOCK} INTEGER NOT NULL
            );
        """.trimIndent()
        db.execSQL(createVouchersTable)

        // Tabel Activities
        val createActivitiesTable = """
        CREATE TABLE IF NOT EXISTS ${ActivityManager.TABLE_NAME} (
            ${ActivityManager.ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            ${ActivityManager.USER_ID} INTEGER NOT NULL,
            ${ActivityManager.TYPE} TEXT NOT NULL,
            ${ActivityManager.DISTANCE} REAL NOT NULL,
            ${ActivityManager.DURATION} REAL NOT NULL,
            ${ActivityManager.DATE} TEXT NOT NULL,
            ${ActivityManager.POINTS_EARNED} INTEGER NOT NULL,
            ${ActivityManager.VEHICLE} TEXT NOT NULL, -- DITAMBAH: Kolom ini yang hilang
            FOREIGN KEY(${ActivityManager.USER_ID}) REFERENCES ${UserManager.TABLE_NAME}(${UserManager.ID})
        );
    """.trimIndent()
        db.execSQL(createActivitiesTable)

        // --- Tambahan untuk Tabel Redeemed Vouchers ---
        val createRedeemedVouchersTable = """
            CREATE TABLE IF NOT EXISTS ${RedeemedVoucherManager.TABLE_NAME} (
                ${RedeemedVoucherManager.ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                ${RedeemedVoucherManager.USER_ID} INTEGER NOT NULL,
                ${RedeemedVoucherManager.VOUCHER_ID} INTEGER NOT NULL,
                ${RedeemedVoucherManager.REDEEMED_AT} TEXT NOT NULL,
                ${RedeemedVoucherManager.STATUS} TEXT NOT NULL,
                FOREIGN KEY(${RedeemedVoucherManager.USER_ID}) REFERENCES ${UserManager.TABLE_NAME}(${UserManager.ID}),
                FOREIGN KEY(${RedeemedVoucherManager.VOUCHER_ID}) REFERENCES ${VoucherManager.VOUCHERS_TABLE_NAME}(${VoucherManager.VOUCHERS_ID})
            );
        """.trimIndent()
        db.execSQL(createRedeemedVouchersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        // Saat meng-upgrade database, kita akan menghapus semua tabel dan membuat ulang.
        // Penting: Pendekatan ini akan menghapus semua data yang ada.
        // Untuk aplikasi produksi, Anda mungkin ingin melakukan migrasi data yang lebih halus.
        db.execSQL("DROP TABLE IF EXISTS ${UserManager.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${VoucherManager.VOUCHERS_TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${ActivityManager.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${RedeemedVoucherManager.TABLE_NAME}") // Tambahkan ini
        onCreate(db)
    }
}