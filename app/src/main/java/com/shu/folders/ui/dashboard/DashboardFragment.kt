package com.shu.folders.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.GridLayoutManager
import com.shu.folders.databinding.FragmentDashboardBinding
import com.shu.folders.models.Folder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by viewModels<DashboardViewModel>()

    private lateinit var adapterFolder: FoldersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initAdapter()
        viewModel.folders.observe(
            viewLifecycleOwner,
            Observer<List<Folder>>
            { folders ->
               // Log.v("dash", "in fragment ${folders.size}")
                adapterFolder.setData(folders)
            })
        return root
    }

    private fun initAdapter() {
        adapterFolder = FoldersAdapter(this::toggleButtonStatus)
        binding.recycler.apply {
            adapter = adapterFolder
            //val column = if (checkOrientation()) 2 else 3
            layoutManager = GridLayoutManager(context, 2)
        }
    }

    private fun toggleButtonStatus(
        name: String
    ) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}