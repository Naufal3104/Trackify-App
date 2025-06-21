package com.example.trackergps.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.trackergps.LoginActivity
import com.example.trackergps.R
import com.example.trackergps.databinding.FragmentAdminProfileBinding

class ProfileAdminFragment : Fragment() {

    // Menggunakan ViewBinding untuk akses view yang aman
    private var _binding: FragmentAdminProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menggunakan ViewBinding untuk inflate layout fragment profil admin
        _binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menambahkan listener untuk tombol logout
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar dari akun ini?")
            .setPositiveButton("Logout") { _, _ ->
                logout()
            }
            .setNegativeButton("Batal", null)
            .setIcon(R.drawable.ic_logout)
            .show()
    }

    private fun logout() {
        // Membuat Intent untuk pindah ke LoginActivity
        val intent = Intent(activity, LoginActivity::class.java)

        // Membersihkan semua activity sebelumnya dari tumpukan (stack)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Mencegah memory leak
    }
}
