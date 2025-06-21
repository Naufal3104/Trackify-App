package com.example.trackergps.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.trackergps.R
import com.example.trackergps.fragment.DataVoucher
import com.example.trackergps.fragment.ManageUsersFragment

/**
 * A simple [Fragment] subclass.
 * Use the [AdminFragmentManage.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminFragmentManage : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_manage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menggunakan ID baru: btnManageUsers
        val btnManageUsers: View = view.findViewById(R.id.btnManageUsers)
        btnManageUsers.setOnClickListener {
            replaceFragment(ManageUsersFragment())
        // Aksi untuk kelola pengguna
        }

        // Menggunakan ID baru: btnManageActivities
        val btnManageActivities: View = view.findViewById(R.id.btnManageActivities)
        btnManageActivities.setOnClickListener {
            replaceFragment(ManageUsersFragment())
            // Aksi untuk kelola aktivitas
        }

        // Menggunakan ID baru: btnManageVouchers
        val btnManageVouchers: View = view.findViewById(R.id.btnManageVouchers)
        btnManageVouchers.setOnClickListener {
            replaceFragment(DataVoucher())
        }

        // Menggunakan ID baru: btnManageReports
        val btnManageReports: View = view.findViewById(R.id.btnManageReports)
        btnManageReports.setOnClickListener {
            // Aksi untuk laporan
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    }