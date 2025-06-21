package com.example.trackergps

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trackergps.databinding.ActivityReedeemVoucherBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReedeemVoucher : AppCompatActivity(), VoucherAdapter.OnVoucherClickListener {

    private lateinit var binding: ActivityReedeemVoucherBinding
    private lateinit var userManager: UserManager
    private lateinit var voucherManager: VoucherManager
    private lateinit var redeemedVoucherManager: RedeemedVoucherManager
    private lateinit var voucherAdapter: VoucherAdapter
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pastikan nama file layout XML Anda adalah activity_redeem_voucher.xml
        // Jika nama kelas ini ReedeemVoucher, maka layoutnya R.layout.activity_reedeem_voucher
        binding = ActivityReedeemVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi semua manager yang dibutuhkan
        userManager = UserManager(this)
        voucherManager = VoucherManager(this)
        redeemedVoucherManager = RedeemedVoucherManager(this)
        currentUserId = userManager.UserSession()

        // Pengaman jika user tidak login
        if (currentUserId == -1) {
            Toast.makeText(this, "Sesi tidak ditemukan.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        loadAvailableVouchers()
    }

    /**
     * Mengatur fungsi tombol kembali pada Toolbar.
     */
    private fun setupToolbar() {
        binding.toolbarRedeemVoucher.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Mempersiapkan RecyclerView dan Adapternya.
     */
    private fun setupRecyclerView() {
        voucherAdapter = VoucherAdapter(mutableListOf(), this)
        binding.recyclerViewVouchers.apply {
            layoutManager = LinearLayoutManager(this@ReedeemVoucher)
            adapter = voucherAdapter
        }
    }

    /**
     * Memuat semua voucher yang tersedia (stok > 0) dari database
     * dan menampilkannya di RecyclerView.
     */
    @SuppressLint("Range")
    private fun loadAvailableVouchers() {
        val voucherCursor = voucherManager.getAllVouchers()
        val voucherList = mutableListOf<Voucher>()

        voucherCursor.use {
            if (it.moveToFirst()) {
                do {
                    val stock = it.getInt(it.getColumnIndex(VoucherManager.VOUCHERS_STOCK))
                    // Filter: Hanya tampilkan voucher yang stoknya masih ada
                    if (stock > 0) {
                        voucherList.add(
                            Voucher(
                                id = it.getInt(it.getColumnIndex(VoucherManager.VOUCHERS_ID)),
                                title = it.getString(it.getColumnIndex(VoucherManager.VOUCHERS_TITLE)),
                                description = it.getString(it.getColumnIndex(VoucherManager.VOUCHERS_DESCRIPTION)),
                                pointsRequired = it.getInt(it.getColumnIndex(VoucherManager.VOUCHERS_POINTS_REQUIRED)),
                                expiryDate = it.getString(it.getColumnIndex(VoucherManager.VOUCHERS_EXPIRY_DATE)),
                                stock = stock
                            )
                        )
                    }
                } while (it.moveToNext())
            }
        }

        // Mengatur tampilan jika tidak ada voucher yang tersedia
        if (voucherList.isEmpty()) {
            binding.recyclerViewVouchers.visibility = View.GONE
            binding.textViewNoVouchers.visibility = View.VISIBLE
        } else {
            binding.recyclerViewVouchers.visibility = View.VISIBLE
            binding.textViewNoVouchers.visibility = View.GONE
            voucherAdapter.updateVouchers(voucherList)
        }
    }

    /**
     * Fungsi ini dipanggil dari Adapter saat sebuah item voucher di-klik.
     */
    override fun onVoucherClick(voucher: Voucher) {
        showRedemptionConfirmationDialog(voucher)
    }

    /**
     * Menampilkan dialog untuk meminta konfirmasi dari pengguna.
     */
    private fun showRedemptionConfirmationDialog(voucher: Voucher) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Penukaran")
            .setMessage("Anda akan menukarkan ${voucher.pointsRequired} poin untuk mendapatkan '${voucher.title}'. Lanjutkan?")
            .setPositiveButton("Ya, Tukar") { _, _ ->
                processVoucherRedemption(voucher)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    /**
     * Memproses logika penukaran voucher:
     * 1. Cek dan kurangi poin pengguna.
     * 2. Jika berhasil, kurangi stok voucher.
     * 3. Tambahkan data ke tabel voucher yang sudah ditukar.
     */
    private fun processVoucherRedemption(voucher: Voucher) {
        // 1. Cek & kurangi poin user
        val pointDeducted = userManager.deductPoints(currentUserId, voucher.pointsRequired)

        if (pointDeducted) {
            // 2. Kurangi stok voucher
            voucherManager.decrementVoucherStock(voucher.id)

            // 3. Tambahkan ke tabel redeemed_vouchers
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentDate = sdf.format(Date())
            redeemedVoucherManager.addRedeemedVoucher(
                userId = currentUserId,
                voucherId = voucher.id,
                redeemedAt = currentDate,
                status = "Tersedia" // Status awal saat voucher berhasil didapat
            )
            Toast.makeText(this, "Voucher berhasil ditukarkan!", Toast.LENGTH_SHORT).show()

            // Muat ulang daftar voucher untuk memperbarui tampilan (stok, dll)
            loadAvailableVouchers()
        } else {
            Toast.makeText(this, "Poin Anda tidak cukup untuk menukarkan voucher ini.", Toast.LENGTH_LONG).show()
        }
    }
}
