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
import com.example.trackergps.databinding.FragmentAdminManageUsersBinding
import com.google.android.material.appbar.MaterialToolbar

// Model data
data class User(
    val id: Int,
    var name: String,
    var email: String,
    var role: String = "user"
)

// Fragment Anak untuk Pengguna
class ManageUsersFragment : BaseManageFragment<User, FragmentAdminManageUsersBinding>() {

    // --- Implementasi Metode Abstrak dari BaseManageFragment ---

    override fun getToolbar(): MaterialToolbar = binding.toolbar
    override fun getListView(): ListView = binding.lvUsers
    override fun getEmptyTextView(): TextView = binding.tvEmptyView
    override fun getToolbarTitle(): String = "Manajemen Pengguna"

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAdminManageUsersBinding{
        return FragmentAdminManageUsersBinding.inflate(inflater, container, false)
    }

    override fun createAdapter(): ArrayAdapter<User> {
        return UserListAdapter(requireContext(), dataList)
    }

    override fun loadInitialData() {
        if (dataList.isEmpty()) {
            dataList.add(User(1, "Budi Santoso", "budi.s@example.com"))
            dataList.add(User(2, "Citra Lestari", "citra.l@example.com"))
            dataList.add(User(3, "Admin Utama", "admin@trackify.com", "admin"))
        }
        refreshAdapter()
    }

    override fun populateForm(item: User) {
        binding.etUserName.setText(item.name)
        binding.etUserEmail.setText(item.email)
        binding.etUserPassword.setText("") // Selalu kosongkan password saat edit
    }

    override fun clearForm() {
        binding.etUserName.text?.clear()
        binding.etUserEmail.text?.clear()
        binding.etUserPassword.text?.clear()
        binding.tilUserName.requestFocus()
    }

    override fun validateForm(): Boolean {
        // Untuk update, password tidak wajib. Untuk add, wajib.
        val isAdding = selectedItem == null
        val name = binding.etUserName.text.toString().trim()
        val email = binding.etUserEmail.text.toString().trim()
        val password = binding.etUserPassword.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(requireContext(), "Nama dan Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return false
        }
        if (isAdding && password.isEmpty()) {
            Toast.makeText(requireContext(), "Password wajib diisi untuk pengguna baru", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun createItemFromForm(): User {
        val name = binding.etUserName.text.toString().trim()
        val email = binding.etUserEmail.text.toString().trim()
        val newId = (dataList.maxOfOrNull { it.id } ?: 0) + 1
        // Di aplikasi nyata, hash password sebelum menyimpan
        return User(newId, name, email)
    }

    override fun updateItemFromForm(item: User) {
        item.name = binding.etUserName.text.toString().trim()
        item.email = binding.etUserEmail.text.toString().trim()
        // Logika untuk update password jika field tidak kosong bisa ditambahkan di sini
    }

    override fun getDialogTitleFor(item: User): String = "Hapus Pengguna"
    override fun getAddSuccessMessage(): String = "Pengguna baru berhasil ditambahkan"
    override fun getUpdateSuccessMessage(): String = "Data pengguna berhasil diperbarui"
    override fun getDeleteSuccessMessage(item: User): String = "'${item.name}' telah dihapus"

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


// Adapter Kustom untuk ListView Pengguna
class UserListAdapter(context: Context, users: List<User>) :
    ArrayAdapter<User>(context, 0, users), SelectableAdapter<User> {

    private var selectedPosition = -1

    override fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val user = getItem(position)!!
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

        holder.nameTextView.text = user.name
        holder.emailTextView.text = user.email

        val cardBackground = view as CardView
        if (position == selectedPosition) {
            cardBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.trackify_green_light_selection))
        } else {
            cardBackground.setCardBackgroundColor(Color.WHITE)
        }
        return view
    }

    private class ViewHolder(val nameTextView: TextView, val emailTextView: TextView)

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
            setImageResource(R.drawable.ic_manage_accounts)
            setColorFilter(Color.parseColor("#11999E"))
        }
        val name = TextView(context).apply {
            id = View.generateViewId()
            setTextColor(Color.parseColor("#212121"))
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
        }
        val email = TextView(context).apply {
            id = View.generateViewId()
            setTextColor(Color.parseColor("#757575"))
            textSize = 14f
        }
        constraintLayout.addView(icon, (40 * context.resources.displayMetrics.density).toInt(), (40 * context.resources.displayMetrics.density).toInt())
        constraintLayout.addView(name)
        constraintLayout.addView(email)
        val cs = ConstraintSet()
        cs.clone(constraintLayout)
        cs.connect(icon.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        cs.connect(icon.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        cs.connect(icon.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        cs.connect(name.id, ConstraintSet.START, icon.id, ConstraintSet.END, (16 * context.resources.displayMetrics.density).toInt())
        cs.connect(name.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        cs.connect(name.id, ConstraintSet.TOP, icon.id, ConstraintSet.TOP)
        cs.constrainWidth(name.id, ConstraintSet.MATCH_CONSTRAINT)
        cs.connect(email.id, ConstraintSet.START, name.id, ConstraintSet.START)
        cs.connect(email.id, ConstraintSet.TOP, name.id, ConstraintSet.BOTTOM)
        cs.applyTo(constraintLayout)
        cardView.addView(constraintLayout)
        val holder = ViewHolder(name, email)
        return Pair(cardView, holder)
    }
}
