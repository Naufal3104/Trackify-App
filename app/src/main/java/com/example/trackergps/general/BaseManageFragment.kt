package com.example.trackergps.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.trackergps.R
import com.google.android.material.appbar.MaterialToolbar

// T: Tipe data (User atau Activity), VB: Tipe ViewBinding
abstract class BaseManageFragment<T, VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected lateinit var listAdapter: ArrayAdapter<T>
    protected var dataList = mutableListOf<T>()
    protected var selectedItem: T? = null
    protected var selectedPosition = -1

    // --- METODE ABSTRAK (WAJIB DIISI OLEH ANAK) ---
    abstract fun getToolbar(): MaterialToolbar
    abstract fun getListView(): ListView
    abstract fun getEmptyTextView(): TextView
    abstract fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    abstract fun createAdapter(): ArrayAdapter<T>
    abstract fun loadInitialData()
    abstract fun getToolbarTitle(): String
    abstract fun populateForm(item: T)
    abstract fun clearForm()
    abstract fun validateForm(): Boolean
    abstract fun createItemFromForm(): T
    abstract fun updateItemFromForm(item: T)
    abstract fun getDialogTitleFor(item: T): String
    abstract fun getAddSuccessMessage(): String
    abstract fun getUpdateSuccessMessage(): String
    abstract fun getDeleteSuccessMessage(item: T): String
    abstract fun setupClickListeners() // Sekarang menjadi abstrak
    abstract fun updateButtonStates() // Sekarang menjadi abstrak


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = createViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getToolbar().title = getToolbarTitle()
        getToolbar().setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        listAdapter = createAdapter()

        setupListView()
        setupClickListeners() // Sekarang didefinisikan oleh anak
        loadInitialData()
        updateButtonStates()
    }

    private fun setupListView() {
        getListView().adapter = listAdapter
        getListView().setOnItemClickListener { _, _, position, _ ->
            selectedItem = listAdapter.getItem(position)
            selectedPosition = position
            populateForm(selectedItem!!)
            updateButtonStates()
            (listAdapter as? SelectableAdapter<*>)?.setSelectedPosition(position)
        }
    }

    // --- Logika CRUD yang Umum dipindahkan ke sini ---
    protected fun addItem() {
        if (!validateForm()) return

        val newItem = createItemFromForm()
        dataList.add(newItem)
        refreshAdapter()
        Toast.makeText(requireContext(), getAddSuccessMessage(), Toast.LENGTH_SHORT).show()
        clearForm()
        getListView().smoothScrollToPosition(dataList.size - 1)
    }

    protected fun updateSelectedItem() {
        if (selectedItem == null) return
        if (!validateForm()) return

        updateItemFromForm(selectedItem!!)
        refreshAdapter()
        Toast.makeText(requireContext(), getUpdateSuccessMessage(), Toast.LENGTH_SHORT).show()
        clearForm()
    }

    protected fun deleteSelectedItem() {
        selectedItem?.let { itemToDelete ->
            AlertDialog.Builder(requireContext())
                .setTitle(getDialogTitleFor(itemToDelete))
                .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                .setPositiveButton("Hapus") { _, _ ->
                    dataList.remove(itemToDelete)
                    refreshAdapter()
                    Toast.makeText(requireContext(), getDeleteSuccessMessage(itemToDelete), Toast.LENGTH_SHORT).show()
                    clearForm()
                }
                .setNegativeButton("Batal", null)
                .setIcon(R.drawable.ic_delete)
                .show()
        }
    }

    protected fun clearFormAndSelection() {
        selectedItem = null
        selectedPosition = -1
        clearForm()
        updateButtonStates()
        (listAdapter as? SelectableAdapter<*>)?.setSelectedPosition(-1)
        Toast.makeText(requireContext(), "Pilihan dibersihkan", Toast.LENGTH_SHORT).show()
    }

    protected fun refreshAdapter() {
        listAdapter.notifyDataSetChanged()
        updateEmptyView()
    }

    private fun updateEmptyView() {
        getEmptyTextView().visibility = if (dataList.isEmpty()) View.VISIBLE else View.GONE
        getListView().visibility = if (dataList.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Interface kecil untuk adapter agar bisa di-select
interface SelectableAdapter<T> {
    fun setSelectedPosition(position: Int)
}

