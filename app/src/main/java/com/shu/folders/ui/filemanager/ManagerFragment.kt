package com.shu.folders.ui.filemanager

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.shu.folders.databinding.FragmentManagerBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ManagerFragment : Fragment() {

    private var _binding: FragmentManagerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var hasPermission = true
    private lateinit var currentDirectory: File
    private lateinit var filesList: List<File>
    private lateinit var adapter: ArrayAdapter<String>

    companion object {
        const val MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 1
        const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 2

        fun newInstance() = ManagerFragment()
    }

    private val viewModel: ManagerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManagerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // binding.toolbar.inflateMenu(R.menu.file_manager_menu)
        setupUi()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        //hasPermission = this.checkStoragePermission(context)
        if (hasPermission) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                if (!Environment.isExternalStorageLegacy()) {
                    binding.rationaleView.visibility = View.GONE
                    binding.legacyStorageView.visibility = View.VISIBLE
                    return
                }
            }

            binding.rationaleView.visibility = View.GONE
            binding.filesTreeView.visibility = View.VISIBLE

            // TODO: Use getStorageDirectory instead https://developer.android.com/reference/android/os/Environment.html#getStorageDirectory()
            open(getExternalStorageDirectory())
        } else {
            binding.rationaleView.visibility = View.VISIBLE
            binding.filesTreeView.visibility = View.GONE
        }
    }

    private fun setupUi() {
        /* binding.toolbar.setOnMenuItemClickListener {
             // startActivity(Intent(this, SettingsActivity::class.java))
             true
         }
 */
        binding.permissionButton.setOnClickListener {
            // requestStoragePermission(this)
        }

        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            mutableListOf<String>()
        )
        binding.filesTreeView.adapter = adapter
        binding.filesTreeView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = filesList[position]
            open(selectedItem)
        }
    }

    private fun open(selectedItem: File) {
        if (selectedItem.isFile) {
            return openFile(requireContext(), selectedItem)
        }

        currentDirectory = selectedItem
        filesList = getFilesList(currentDirectory)

        adapter.clear()
        adapter.addAll(filesList.map {
            if (it.path == selectedItem.parentFile.path) {
                renderParentLink(requireContext())
            } else {
                renderItem(requireContext(), it)
            }
        })

        adapter.notifyDataSetChanged()
    }
}