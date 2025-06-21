package com.example.trackergps

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "tracker_gps"
        // PENTING: Naikkan versi database agar onUpgrade() terpanggil
        private const val DATABASE_VERSION = 7 // NAIKKAN DARI 6 MENJADI 7
    }

    override fun onCreate(db: SQLiteDatabase){
        // --- 1. MEMBUAT SEMUA TABEL ---
        // Tabel Users - (DIUBAH)
        val createUsersTable = """
            CREATE TABLE IF NOT EXISTS ${UserManager.TABLE_NAME} (
                ${UserManager.ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                ${UserManager.NAME} TEXT NOT NULL,
                ${UserManager.EMAIL} TEXT NOT NULL,
                ${UserManager.PASSWORD} TEXT NOT NULL,
                ${UserManager.ROLE} INTEGER NOT NULL,
                ${UserManager.PREFERRED_TRANSPORT} TEXT NOT NULL,
                ${UserManager.TOTAL_DISTANCE} REAL NOT NULL DEFAULT 0,
                ${UserManager.REWARD_POINTS} INTEGER NOT NULL DEFAULT 0,
                ${UserManager.PROFILE_IMAGE_URI} TEXT 
            );
        """.trimIndent() // TAMBAHKAN KOLOM BARU 'PROFILE_IMAGE_URI'
        db.execSQL(createUsersTable)

        // ... (Sisa kode onCreate tetap sama) ...

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
                ${ActivityManager.VEHICLE} TEXT NOT NULL,
                FOREIGN KEY(${ActivityManager.USER_ID}) REFERENCES ${UserManager.TABLE_NAME}(${UserManager.ID}) ON DELETE CASCADE
            );
        """.trimIndent()
        db.execSQL(createActivitiesTable)

        // Tabel Redeemed Vouchers
        val createRedeemedVouchersTable = """
            CREATE TABLE IF NOT EXISTS ${RedeemedVoucherManager.TABLE_NAME} (
                ${RedeemedVoucherManager.ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                ${RedeemedVoucherManager.USER_ID} INTEGER NOT NULL,
                ${RedeemedVoucherManager.VOUCHER_ID} INTEGER NOT NULL,
                ${RedeemedVoucherManager.REDEEMED_AT} TEXT NOT NULL,
                ${RedeemedVoucherManager.STATUS} TEXT NOT NULL,
                FOREIGN KEY(${RedeemedVoucherManager.USER_ID}) REFERENCES ${UserManager.TABLE_NAME}(${UserManager.ID}) ON DELETE CASCADE,
                FOREIGN KEY(${RedeemedVoucherManager.VOUCHER_ID}) REFERENCES ${VoucherManager.VOUCHERS_TABLE_NAME}(${VoucherManager.VOUCHERS_ID}) ON DELETE CASCADE
            );
        """.trimIndent()
        db.execSQL(createRedeemedVouchersTable)

        // --- 2. MEMBUAT SEMUA TRIGGER ---

        // Trigger setelah data baru dimasukkan ke tabel 'activities'
        val triggerInsert = """
            CREATE TRIGGER update_user_stats_after_insert
            AFTER INSERT ON ${ActivityManager.TABLE_NAME}
            FOR EACH ROW
            BEGIN
                UPDATE ${UserManager.TABLE_NAME}
                SET
                    ${UserManager.TOTAL_DISTANCE} = ${UserManager.TOTAL_DISTANCE} + NEW.${ActivityManager.DISTANCE},
                    ${UserManager.REWARD_POINTS} = ${UserManager.REWARD_POINTS} + NEW.${ActivityManager.POINTS_EARNED}
                WHERE ${UserManager.ID} = NEW.${ActivityManager.USER_ID};
            END;
        """.trimIndent()
        db.execSQL(triggerInsert)

        // Trigger setelah data dihapus dari tabel 'activities'
        val triggerDelete = """
            CREATE TRIGGER update_user_stats_after_delete
            AFTER DELETE ON ${ActivityManager.TABLE_NAME}
            FOR EACH ROW
            BEGIN
                UPDATE ${UserManager.TABLE_NAME}
                SET
                    ${UserManager.TOTAL_DISTANCE} = ${UserManager.TOTAL_DISTANCE} - OLD.${ActivityManager.DISTANCE},
                    ${UserManager.REWARD_POINTS} = ${UserManager.REWARD_POINTS} - OLD.${ActivityManager.POINTS_EARNED}
                WHERE ${UserManager.ID} = OLD.${ActivityManager.USER_ID};
            END;
        """.trimIndent()
        db.execSQL(triggerDelete)

        // Di dalam file DatabaseManager.kt -> fungsi onCreate()

// ... (kode triggerInsert dan triggerDelete tetap sama) ...

// --- TRIGGER UPDATE YANG DIPERBAIKI DAN DISEDERHANKAN ---
        val triggerUpdate = """
            CREATE TRIGGER update_user_stats_after_update
            AFTER UPDATE ON ${ActivityManager.TABLE_NAME}
            FOR EACH ROW
            WHEN OLD.${ActivityManager.USER_ID} = NEW.${ActivityManager.USER_ID} -- Trigger hanya berjalan jika User ID tidak berubah
            BEGIN
                UPDATE ${UserManager.TABLE_NAME}
                SET
                    ${UserManager.TOTAL_DISTANCE} = ${UserManager.TOTAL_DISTANCE} - OLD.${ActivityManager.DISTANCE} + NEW.${ActivityManager.DISTANCE},
                    ${UserManager.REWARD_POINTS} = ${UserManager.REWARD_POINTS} - OLD.${ActivityManager.POINTS_EARNED} + NEW.${ActivityManager.POINTS_EARNED}
                WHERE ${UserManager.ID} = NEW.${ActivityManager.USER_ID};
            END;
        """.trimIndent()
        db.execSQL(triggerUpdate)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        // Pendekatan ini akan menghapus semua tabel dan membuat ulang,
        // yang akan menerapkan skema baru beserta trigger-nya.
        // Untuk pengembangan ini adalah cara yang cepat. Untuk produksi, Anda perlu ALTER TABLE.
        db.execSQL("DROP TRIGGER IF EXISTS update_user_stats_after_insert")
        db.execSQL("DROP TRIGGER IF EXISTS update_user_stats_after_delete")
        db.execSQL("DROP TRIGGER IF EXISTS update_user_stats_after_update")
        db.execSQL("DROP TABLE IF EXISTS ${RedeemedVoucherManager.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${ActivityManager.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${VoucherManager.VOUCHERS_TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${UserManager.TABLE_NAME}")
        onCreate(db)
    }
}
