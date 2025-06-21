package com.example.trackergps

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trackergps.databinding.ActivityUserVoucherBinding

class UserVoucher : AppCompatActivity() {

    private lateinit var binding: ActivityUserVoucherBinding
    private lateinit var userManager: UserManager
    private lateinit var redeemedVoucherManager: RedeemedVoucherManager
    private lateinit var redeemedAdapter: RedeemedVoucherAdapter
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager = UserManager(this)
        redeemedVoucherManager = RedeemedVoucherManager(this)
        currentUserId = userManager.UserSession()

        if (currentUserId == -1) {
            Toast.makeText(this, "Sesi tidak ditemukan.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        loadRedeemedVouchers()
    }

    private fun setupToolbar() {
        binding.toolbarUserVoucher.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        redeemedAdapter = RedeemedVoucherAdapter(mutableListOf())
        binding.recyclerViewRedeemedVouchers.apply {
            layoutManager = LinearLayoutManager(this@UserVoucher)
            adapter = redeemedAdapter
        }
    }

    @SuppressLint("Range")
    private fun loadRedeemedVouchers() {
        // Menggunakan fungsi JOIN yang baru kita buat
        val cursor = redeemedVoucherManager.getJoinedRedeemedVouchersByUserId(currentUserId)
        val redeemedList = mutableListOf<RedeemedVoucher>()

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    redeemedList.add(
                        RedeemedVoucher(
                            id = it.getInt(it.getColumnIndex(RedeemedVoucherManager.ID)),
                            userId = it.getInt(it.getColumnIndex(RedeemedVoucherManager.USER_ID)),
                            voucherId = it.getInt(it.getColumnIndex(RedeemedVoucherManager.VOUCHER_ID)),
                            redeemedAt = it.getString(it.getColumnIndex(RedeemedVoucherManager.REDEEMED_AT)),
                            status = it.getString(it.getColumnIndex(RedeemedVoucherManager.STATUS)),
                            voucherTitle = it.getString(it.getColumnIndex(VoucherManager.VOUCHERS_TITLE))
                        )
                    )
                } while (it.moveToNext())
            }
        }

        if (redeemedList.isEmpty()) {
            binding.recyclerViewRedeemedVouchers.visibility = View.GONE
            binding.textViewNoRedeemedVouchers.visibility = View.VISIBLE
        } else {
            binding.recyclerViewRedeemedVouchers.visibility = View.VISIBLE
            binding.textViewNoRedeemedVouchers.visibility = View.GONE
            redeemedAdapter.updateVouchers(redeemedList)
        }
    }
}
