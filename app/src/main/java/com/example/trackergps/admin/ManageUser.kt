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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackergps.R
import com.example.trackergps.UserManager
import com.example.trackergps.databinding.ActivityManageUserBinding

// Data class untuk menampung data user agar lebih mudah dikelola di Adapter
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val role: Int,
    val preferredTransport: String,
    val totalDistance: Int,
    val rewardPoints: Int
)

class ManageUser : AppCompatActivity() {

    private lateinit var binding: ActivityManageUserBinding
    private lateinit var userManager: UserManager
    private lateinit var userAdapter: UserAdapter
    private var selectedUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager = UserManager(this)

        setupRecyclerView()
        loadUsers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter { user ->
            // Aksi ketika item di RecyclerView di-klik
            selectedUser = user
            binding.editTextName.setText(user.name)
            binding.editTextEmail.setText(user.email)
            binding.editTextPassword.setText(user.password)
            // Anda bisa tambahkan field lain jika ada di layout
        }
        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(this@ManageUser)
            adapter = userAdapter
        }
    }

    @SuppressLint("Range")
    private fun loadUsers() {
        val cursor: Cursor = userManager.getAllUsers()
        val users = mutableListOf<User>()
        if (cursor.moveToFirst()) {
            do {
                users.add(
                    User(
                        id = cursor.getInt(cursor.getColumnIndex(UserManager.ID)),
                        name = cursor.getString(cursor.getColumnIndex(UserManager.NAME)),
                        email = cursor.getString(cursor.getColumnIndex(UserManager.EMAIL)),
                        password = cursor.getString(cursor.getColumnIndex(UserManager.PASSWORD)),
                        role = cursor.getInt(cursor.getColumnIndex(UserManager.ROLE)),
                        preferredTransport = cursor.getString(cursor.getColumnIndex(UserManager.PREFERRED_TRANSPORT)),
                        totalDistance = cursor.getInt(cursor.getColumnIndex(UserManager.TOTAL_DISTANCE)),
                        rewardPoints = cursor.getInt(cursor.getColumnIndex(UserManager.REWARD_POINTS))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        userAdapter.submitList(users)
    }

    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            finish() // Kembali ke activity sebelumnya
        }

        binding.buttonAdd.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                // Role default untuk user baru adalah 1 (user biasa)
                // Field lain bisa di-set ke nilai default
                userManager.addUser(name, email, password, 1, "car", 0, 0)
                Toast.makeText(this, "Pengguna berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                clearFormAndRefresh()
            } else {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonEdit.setOnClickListener {
            if (selectedUser == null) {
                Toast.makeText(this, "Pilih pengguna yang akan diedit", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val name = binding.editTextName.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                // Panggil fungsi updateUser dari UserManager
                // Anda perlu menambahkan fungsi ini di UserManager.kt
                userManager.updateUser(
                    selectedUser!!.id, name, email, password,
                    selectedUser!!.role, // Role tidak diubah dari form ini
                    selectedUser!!.preferredTransport,
                    selectedUser!!.totalDistance,
                    selectedUser!!.rewardPoints
                )
                Toast.makeText(this, "Data pengguna berhasil diupdate", Toast.LENGTH_SHORT).show()
                clearFormAndRefresh()
            } else {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonDelete.setOnClickListener {
            if (selectedUser == null) {
                Toast.makeText(this, "Pilih pengguna yang akan dihapus", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userManager.deleteUser(selectedUser!!.id)
            Toast.makeText(this, "Pengguna berhasil dihapus", Toast.LENGTH_SHORT).show()
            clearFormAndRefresh()
        }
    }

    private fun clearFormAndRefresh() {
        binding.editTextName.text?.clear()
        binding.editTextEmail.text?.clear()
        binding.editTextPassword.text?.clear()
        selectedUser = null
        binding.recyclerViewUsers.requestFocus() // Pindahkan fokus
        loadUsers()
    }
}

// --- Adapter untuk RecyclerView ---
class UserAdapter(private val onItemClick: (User) -> Unit) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var users: List<User> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(userList: List<User>) {
        users = userList
        notifyDataSetChanged() // Sederhana, untuk performa lebih baik gunakan DiffUtil
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
    }

    override fun getItemCount(): Int = users.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewUserName)
        private val emailTextView: TextView = itemView.findViewById(R.id.textViewUserEmail)
        private val roleTextView: TextView = itemView.findViewById(R.id.textViewUserRole)

        fun bind(user: User) {
            nameTextView.text = user.name
            emailTextView.text = user.email
            roleTextView.text = "Role: ${if (user.role == 0) "Admin" else "Pengguna"}"
        }
    }
}
