package com.example.trackergps.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.example.trackergps.R
import com.example.trackergps.databinding.FragmentAdminManageDataVoucherBinding
import com.google.android.material.appbar.MaterialToolbar

// Model data
data class Voucher(
    val id: Int,
    var name: String,
    var points: Int
)

// Fragment Anak untuk Voucher
class DataVoucher : BaseManageFragment<Voucher, FragmentAdminManageDataVoucherBinding>() {

    // --- Implementasi Metode Abstrak dari BaseManageFragment ---

    override fun getToolbar(): MaterialToolbar = binding.toolbar
    override fun getListView(): ListView = binding.lvVouchers
    override fun getEmptyTextView(): TextView = binding.tvEmptyView
    override fun getToolbarTitle(): String = "Manajemen Voucher"

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAdminManageDataVoucherBinding {
        return FragmentAdminManageDataVoucherBinding.inflate(inflater, container, false)
    }

    override fun createAdapter(): ArrayAdapter<Voucher> {
        return VoucherListAdapter(requireContext(), dataList)
    }

    override fun loadInitialData() {
        // Data dummy, gantilah dengan data dari VoucherManager Anda
        if (dataList.isEmpty()) {
            dataList.add(Voucher(1, "Diskon 20% GrabBike", 500))
            dataList.add(Voucher(2, "Potongan Rp10.000 GoFood", 800))
            dataList.add(Voucher(3, "Cashback 15% OVO", 1200))
        }
        refreshAdapter()
    }

    override fun populateForm(item: Voucher) {
        binding.etVoucherName.setText(item.name)
        binding.etVoucherPoints.setText(item.points.toString())
    }

    override fun clearForm() {
        binding.etVoucherName.text?.clear()
        binding.etVoucherPoints.text?.clear()
        binding.tilVoucherName.requestFocus()
    }

    override fun validateForm(): Boolean {
        if (binding.etVoucherName.text.isNullOrEmpty() || binding.etVoucherPoints.text.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Nama dan Poin tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun createItemFromForm(): Voucher {
        val newId = (dataList.maxOfOrNull { it.id } ?: 0) + 1
        return Voucher(
            id = newId,
            name = binding.etVoucherName.text.toString().trim(),
            points = binding.etVoucherPoints.text.toString().toIntOrNull() ?: 0
        )
    }

    override fun updateItemFromForm(item: Voucher) {
        item.name = binding.etVoucherName.text.toString().trim()
        item.points = binding.etVoucherPoints.text.toString().toIntOrNull() ?: 0
    }

    override fun getDialogTitleFor(item: Voucher): String = "Hapus Voucher"
    override fun getAddSuccessMessage(): String = "Voucher berhasil ditambahkan"
    override fun getUpdateSuccessMessage(): String = "Voucher berhasil diperbarui"
    override fun getDeleteSuccessMessage(item: Voucher): String = "'${item.name}' telah dihapus"

    override fun setupClickListeners() {
        binding.btnAdd.setOnClickListener { addItem() }
        binding.btnUpdate.setOnClickListener { updateSelectedItem() }
        binding.btnDelete.setOnClickListener { deleteSelectedItem() }
        binding.btnClear.setOnClickListener { clearFormAndSelection() }
    }

    override fun updateButtonStates() {
        binding.btnUpdate.isEnabled = selectedItem != null
        binding.btnDelete.isEnabled = selectedItem != null
    }
}


// Adapter Kustom untuk ListView Voucher
class VoucherListAdapter(context: Context, vouchers: List<Voucher>) :
    ArrayAdapter<Voucher>(context, 0, vouchers), SelectableAdapter<Voucher> {

    private var selectedPosition = -1

    override fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val voucher = getItem(position)!!
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            val createdViews = createItemView(context)
            view = createdViews.first
            holder = createdViews.second
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        holder.nameTextView.text = voucher.name
        holder.pointsTextView.text = "${voucher.points} Poin"

        val cardBackground = view as CardView
        if (position == selectedPosition) {
            cardBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.trackify_green_light_selection))
        } else {
            cardBackground.setCardBackgroundColor(Color.WHITE)
        }
        return view
    }

    private class ViewHolder(val nameTextView: TextView, val pointsTextView: TextView)

    private fun createItemView(context: Context): Pair<View, ViewHolder> {
        val cardView = CardView(context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            radius = (8 * context.resources.displayMetrics.density)
            cardElevation = (2 * context.resources.displayMetrics.density)
            useCompatPadding = true
        }
        val constraintLayout = ConstraintLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val padding = (16 * context.resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
        }
        val icon = ImageView(context).apply {
            id = View.generateViewId()
            setImageResource(R.drawable.ic_voucher)
            setColorFilter(Color.parseColor("#11999E"))
        }
        val name = TextView(context).apply {
            id = View.generateViewId()
            setTextColor(Color.parseColor("#212121"))
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
        }
        val points = TextView(context).apply {
            id = View.generateViewId()
            setTextColor(Color.parseColor("#16C79A"))
            setTypeface(null, Typeface.BOLD)
        }
        constraintLayout.addView(icon, (40 * context.resources.displayMetrics.density).toInt(), (40 * context.resources.displayMetrics.density).toInt())
        constraintLayout.addView(name)
        constraintLayout.addView(points)
        val cs = ConstraintSet()
        cs.clone(constraintLayout)
        cs.connect(icon.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        cs.connect(icon.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        cs.connect(icon.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        cs.connect(name.id, ConstraintSet.START, icon.id, ConstraintSet.END, (16 * context.resources.displayMetrics.density).toInt())
        cs.connect(name.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        cs.connect(name.id, ConstraintSet.TOP, icon.id, ConstraintSet.TOP)
        cs.constrainWidth(name.id, ConstraintSet.MATCH_CONSTRAINT)
        cs.connect(points.id, ConstraintSet.START, name.id, ConstraintSet.START)
        cs.connect(points.id, ConstraintSet.TOP, name.id, ConstraintSet.BOTTOM)
        cs.applyTo(constraintLayout)
        cardView.addView(constraintLayout)
        val holder = ViewHolder(name, points)
        return Pair(cardView, holder)
    }
}
