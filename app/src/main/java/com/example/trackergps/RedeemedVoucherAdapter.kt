package com.example.trackergps

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RedeemedVoucherAdapter(
    private val redeemedVoucherList: MutableList<RedeemedVoucher>
) : RecyclerView.Adapter<RedeemedVoucherAdapter.RedeemedVoucherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedeemedVoucherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_redeemed_voucher, parent, false)
        return RedeemedVoucherViewHolder(view)
    }

    override fun onBindViewHolder(holder: RedeemedVoucherViewHolder, position: Int) {
        holder.bind(redeemedVoucherList[position])
    }

    override fun getItemCount(): Int = redeemedVoucherList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateVouchers(newVouchers: List<RedeemedVoucher>) {
        redeemedVoucherList.clear()
        redeemedVoucherList.addAll(newVouchers)
        notifyDataSetChanged()
    }

    class RedeemedVoucherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textViewVoucherTitle)
        private val dateTextView: TextView = itemView.findViewById(R.id.textViewRedeemedDate)
        private val statusTextView: TextView = itemView.findViewById(R.id.textViewRedeemedStatus)

        @SuppressLint("SetTextI18n")
        fun bind(voucher: RedeemedVoucher) {
            titleTextView.text = voucher.voucherTitle
            dateTextView.text = "Ditukar pada ${voucher.redeemedAt.substringBefore(" ")}" // Hanya tampilkan tanggal
            statusTextView.text = voucher.status

            // Atur warna background status sesuai nilainya
            val statusBg = when (voucher.status.lowercase()) {
                "tersedia" -> R.drawable.bg_status_available // Buat drawable ini
                "digunakan" -> R.drawable.bg_status_used
                "hangus" -> R.drawable.bg_status_expired // Buat drawable ini
                else -> R.drawable.bg_status_expired
            }
            statusTextView.background = ContextCompat.getDrawable(itemView.context, statusBg)
        }
    }
}
