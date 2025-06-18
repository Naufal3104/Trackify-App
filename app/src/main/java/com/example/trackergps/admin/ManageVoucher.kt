package com.example.trackergps.admin

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackergps.R
import com.example.trackergps.VoucherManager
import com.example.trackergps.databinding.ActivityManageVoucherBinding

// Data class untuk menampung data voucher
data class Voucher(
    val id: Int,
    val title: String,
    val description: String,
    val pointsRequired: Int,
    val expiryDate: String,
    val stock: Int
)

class ManageVoucher : AppCompatActivity() {

    private lateinit var binding: ActivityManageVoucherBinding
    private lateinit var voucherManager: VoucherManager
    private lateinit var voucherAdapter: VoucherAdapter
    private var selectedVoucher: Voucher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Penanganan insets jendela
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        voucherManager = VoucherManager(this)
        setupRecyclerView()
        loadVouchers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        voucherAdapter = VoucherAdapter { voucher ->
            // Aksi saat item voucher diklik
            selectedVoucher = voucher
            binding.editTextTitle.setText(voucher.title)
            binding.editTextDescription.setText(voucher.description)
            binding.editTextPoints.setText(voucher.pointsRequired.toString())
            binding.editTextExpiryDate.setText(voucher.expiryDate)
            binding.editTextStock.setText(voucher.stock.toString())
        }
        binding.recyclerViewVouchers.apply {
            layoutManager = LinearLayoutManager(this@ManageVoucher)
            adapter = voucherAdapter
        }
    }

    @SuppressLint("Range")
    private fun loadVouchers() {
        val cursor: Cursor = voucherManager.getAllVouchers()
        val vouchers = mutableListOf<Voucher>()
        if (cursor.moveToFirst()) {
            do {
                vouchers.add(
                    Voucher(
                        id = cursor.getInt(cursor.getColumnIndex(VoucherManager.VOUCHERS_ID)),
                        title = cursor.getString(cursor.getColumnIndex(VoucherManager.VOUCHERS_TITLE)),
                        description = cursor.getString(cursor.getColumnIndex(VoucherManager.VOUCHERS_DESCRIPTION)),
                        pointsRequired = cursor.getInt(cursor.getColumnIndex(VoucherManager.VOUCHERS_POINTS_REQUIRED)),
                        expiryDate = cursor.getString(cursor.getColumnIndex(VoucherManager.VOUCHERS_EXPIRY_DATE)),
                        stock = cursor.getInt(cursor.getColumnIndex(VoucherManager.VOUCHERS_STOCK))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        voucherAdapter.submitList(vouchers)
    }

    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonAdd.setOnClickListener {
            val title = binding.editTextTitle.text.toString().trim()
            val description = binding.editTextDescription.text.toString().trim()
            val points = binding.editTextPoints.text.toString().toIntOrNull() ?: 0
            val expiryDate = binding.editTextExpiryDate.text.toString().trim()
            val stock = binding.editTextStock.text.toString().toIntOrNull() ?: 0

            if (title.isNotEmpty() && description.isNotEmpty() && expiryDate.isNotEmpty()) {
                voucherManager.addVoucher(title, description, points, expiryDate, stock)
                Toast.makeText(this, "Voucher berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                clearFormAndRefresh()
            } else {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonEdit.setOnClickListener {
            if (selectedVoucher == null) {
                Toast.makeText(this, "Pilih voucher yang akan diedit", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val title = binding.editTextTitle.text.toString().trim()
            val description = binding.editTextDescription.text.toString().trim()
            val points = binding.editTextPoints.text.toString().toIntOrNull() ?: 0
            val expiryDate = binding.editTextExpiryDate.text.toString().trim()
            val stock = binding.editTextStock.text.toString().toIntOrNull() ?: 0

            if (title.isNotEmpty() && description.isNotEmpty() && expiryDate.isNotEmpty()) {
                voucherManager.updateVoucher(selectedVoucher!!.id, title, description, points, expiryDate, stock)
                Toast.makeText(this, "Voucher berhasil diupdate", Toast.LENGTH_SHORT).show()
                clearFormAndRefresh()
            } else {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonDelete.setOnClickListener {
            if (selectedVoucher == null) {
                Toast.makeText(this, "Pilih voucher yang akan dihapus", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            voucherManager.deleteVoucher(selectedVoucher!!.id)
            Toast.makeText(this, "Voucher berhasil dihapus", Toast.LENGTH_SHORT).show()
            clearFormAndRefresh()
        }
    }

    private fun clearFormAndRefresh() {
        selectedVoucher = null
        binding.editTextTitle.text?.clear()
        binding.editTextDescription.text?.clear()
        binding.editTextPoints.text?.clear()
        binding.editTextExpiryDate.text?.clear()
        binding.editTextStock.text?.clear()
        binding.recyclerViewVouchers.requestFocus()
        loadVouchers()
    }
}

// --- Adapter untuk RecyclerView Voucher ---
class VoucherAdapter(private val onItemClick: (Voucher) -> Unit) :
    RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder>() {

    private var vouchers: List<Voucher> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(voucherList: List<Voucher>) {
        vouchers = voucherList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_voucher, parent, false)
        return VoucherViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        val voucher = vouchers[position]
        holder.bind(voucher)
        holder.itemView.setOnClickListener {
            onItemClick(voucher)
        }
    }

    override fun getItemCount(): Int = vouchers.size

    class VoucherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.textViewVoucherTitle)
        private val description: TextView = itemView.findViewById(R.id.textViewVoucherDescription)
        private val pointsStock: TextView = itemView.findViewById(R.id.textViewPointsStock)
        private val expiryDate: TextView = itemView.findViewById(R.id.textViewExpiryDate)

        @SuppressLint("SetTextI18n")
        fun bind(voucher: Voucher) {
            title.text = voucher.title
            description.text = voucher.description
            pointsStock.text = "Poin: ${voucher.pointsRequired} - Stok: ${voucher.stock}"
            expiryDate.text = "Berlaku hingga: ${voucher.expiryDate}"
        }
    }
}
