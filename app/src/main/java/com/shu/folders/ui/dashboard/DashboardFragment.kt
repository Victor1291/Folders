package com.shu.folders.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.shu.folders.databinding.FragmentDashboardBinding
import com.shu.folders.models.Images
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.folders.collect { uiState ->
                    when (uiState) {
                        is UiState.Error -> {
                            Log.e("gallery Fragment", "State.Error")
                        }

                        UiState.Loading -> {
                            Log.e("gallery Fragment", "State.Loading")

                        }

                        is UiState.Success -> {
                            Log.e("gallery Fragment", "State.Success")
                            adapterFolder.submitList(uiState.folders)
                        }
                    }
                }
            }
        }


        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        Log.i("toggleButtonStatus", "Name =  $name ")
        val list = viewModel.getImagesFolder(name)
        findNavController().navigate(
            DashboardFragmentDirections.actionNavigationDashboardToFilesFragment(
                Images(
                    path = name,
                    listImages = list ,
                )
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}