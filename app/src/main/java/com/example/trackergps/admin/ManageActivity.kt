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
import com.example.trackergps.databinding.FragmentAdminManageActivitiesBinding
import com.google.android.material.appbar.MaterialToolbar

// Model data aktivitas
data class ActivityItem(
    val id: Int,
    var name: String,
    var transport: String,
    var distance: Double,
    var points: Int
)

class ManageActivitiesFragment : BaseManageFragment<ActivityItem, FragmentAdminManageActivitiesBinding>() {

    override fun getToolbar(): MaterialToolbar = binding.toolbar
    override fun getListView(): ListView = binding.lvActivities
    override fun getEmptyTextView(): TextView = binding.tvEmptyView
    override fun getToolbarTitle(): String = "Manajemen Aktivitas"

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAdminManageActivitiesBinding {
        return FragmentAdminManageActivitiesBinding.inflate(inflater, container, false)
    }

    override fun createAdapter(): ArrayAdapter<ActivityItem> {
        return ActivityListAdapter(requireContext(), dataList)
    }

    override fun loadInitialData() {
        if (dataList.isEmpty()) {
            dataList.add(ActivityItem(1, "Lari Pagi", "Jalan Kaki", 3.0, 30))
            dataList.add(ActivityItem(2, "Bersepeda", "Sepeda", 10.0, 50))
        }
        refreshAdapter()
    }

    override fun populateForm(item: ActivityItem) {
        binding.etActivityName.setText(item.name)
        binding.etTransportType.setText(item.transport)
        binding.etDistance.setText(item.distance.toString())
        binding.etPointsEarned.setText(item.points.toString())
    }

    override fun clearForm() {
        binding.etActivityName.text?.clear()
        binding.etTransportType.text?.clear()
        binding.etDistance.text?.clear()
        binding.etPointsEarned.text?.clear()
        binding.tilActivityName.requestFocus()
    }

    override fun validateForm(): Boolean {
        val name = binding.etActivityName.text.toString().trim()
        val transport = binding.etTransportType.text.toString().trim()
        val distance = binding.etDistance.text.toString().trim()
        val points = binding.etPointsEarned.text.toString().trim()

        if (name.isEmpty() || transport.isEmpty() || distance.isEmpty() || points.isEmpty()) {
            Toast.makeText(requireContext(), "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun createItemFromForm(): ActivityItem {
        val name = binding.etActivityName.text.toString().trim()
        val transport = binding.etTransportType.text.toString().trim()
        val distance = binding.etDistance.text.toString().toDouble()
        val points = binding.etPointsEarned.text.toString().toInt()
        val newId = (dataList.maxOfOrNull { it.id } ?: 0) + 1
        return ActivityItem(newId, name, transport, distance, points)
    }

    override fun updateItemFromForm(item: ActivityItem) {
        item.name = binding.etActivityName.text.toString().trim()
        item.transport = binding.etTransportType.text.toString().trim()
        item.distance = binding.etDistance.text.toString().toDouble()
        item.points = binding.etPointsEarned.text.toString().toInt()
    }

    override fun getDialogTitleFor(item: ActivityItem): String = "Hapus Aktivitas"
    override fun getAddSuccessMessage(): String = "Aktivitas baru berhasil ditambahkan"
    override fun getUpdateSuccessMessage(): String = "Data aktivitas berhasil diperbarui"
    override fun getDeleteSuccessMessage(item: ActivityItem): String = "'${item.name}' telah dihapus"

    override fun setupClickListeners() {
        binding.btnAdd.setOnClickListener { addItem() }
        binding.btnUpdate.setOnClickListener { updateSelectedItem() }
        binding.btnDelete.setOnClickListener { deleteSelectedItem() }
        binding.btnClear.setOnClickListener { clearFormAndSelection() }
    }

    override fun updateButtonStates() {
        val isItemSelected = selectedItem != null
        binding.btnUpdate.isEnabled = isItemSelected
        binding.btnDelete.isEnabled = isItemSelected
    }
}

class ActivityListAdapter(context: Context, activities: List<ActivityItem>) :
    ArrayAdapter<ActivityItem>(context, 0, activities), SelectableAdapter<ActivityItem> {

    private var selectedPosition = -1

    override fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val activity = getItem(position)!!
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

        holder.nameTextView.text = activity.name
        holder.descTextView.text = "${activity.transport} - ${activity.distance} km - ${activity.points} poin"

        val cardBackground = view as CardView
        if (position == selectedPosition) {
            cardBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.trackify_green_light_selection))
        } else {
            cardBackground.setCardBackgroundColor(Color.WHITE)
        }
        return view
    }

    private class ViewHolder(val nameTextView: TextView, val descTextView: TextView)

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
            setImageResource(R.drawable.ic_flag)
            setColorFilter(Color.parseColor("#11999E"))
        }
        val name = TextView(context).apply {
            id = View.generateViewId()
            setTextColor(Color.parseColor("#212121"))
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
        }
        val desc = TextView(context).apply {
            id = View.generateViewId()
            setTextColor(Color.parseColor("#757575"))
            textSize = 14f
        }
        constraintLayout.addView(icon, (40 * context.resources.displayMetrics.density).toInt(), (40 * context.resources.displayMetrics.density).toInt())
        constraintLayout.addView(name)
        constraintLayout.addView(desc)
        val cs = ConstraintSet()
        cs.clone(constraintLayout)
        cs.connect(icon.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        cs.connect(icon.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        cs.connect(icon.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        cs.connect(name.id, ConstraintSet.START, icon.id, ConstraintSet.END, (16 * context.resources.displayMetrics.density).toInt())
        cs.connect(name.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        cs.connect(name.id, ConstraintSet.TOP, icon.id, ConstraintSet.TOP)
        cs.constrainWidth(name.id, ConstraintSet.MATCH_CONSTRAINT)
        cs.connect(desc.id, ConstraintSet.START, name.id, ConstraintSet.START)
        cs.connect(desc.id, ConstraintSet.TOP, name.id, ConstraintSet.BOTTOM)
        cs.applyTo(constraintLayout)
        cardView.addView(constraintLayout)
        val holder = ViewHolder(name, desc)
        return Pair(cardView, holder)
    }
}
