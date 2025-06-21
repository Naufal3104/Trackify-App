package com.example.trackergps

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VoucherAdapter(
    private val voucherList: MutableList<Voucher>,
    private val listener: OnVoucherClickListener
) : RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder>() {

    interface OnVoucherClickListener {
        fun onVoucherClick(voucher: Voucher)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_voucher_user, parent, false)
        return VoucherViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        val voucher = voucherList[position]
        holder.bind(voucher, listener)
    }

    override fun getItemCount(): Int = voucherList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateVouchers(newVouchers: List<Voucher>) {
        voucherList.clear()
        voucherList.addAll(newVouchers)
        notifyDataSetChanged()
    }

    class VoucherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Mengambil referensi ke semua TextView dari layout
        private val titleTextView: TextView = itemView.findViewById(R.id.textViewVoucherTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textViewVoucherDescription)
        private val stockTextView: TextView = itemView.findViewById(R.id.textViewPointsStock)
        // [FIX] Menggunakan ID yang benar sesuai file XML: textViewExpiryDate
        private val expiryDateTextView: TextView = itemView.findViewById(R.id.textViewExpiryDate)

        /**
         * Mengisi data voucher ke dalam komponen view yang sesuai.
         */
        @SuppressLint("SetTextI18n")
        fun bind(voucher: Voucher, listener: OnVoucherClickListener) {
            // Mengisi data ke setiap TextView
            titleTextView.text = "${voucher.title} (${voucher.pointsRequired} Poin)"
            descriptionTextView.text = voucher.description
            stockTextView.text = "Stok: ${voucher.stock}"
            expiryDateTextView.text = "Berlaku hingga: ${voucher.expiryDate}"

            itemView.setOnClickListener {
                listener.onVoucherClick(voucher)
            }
        }
    }
}
