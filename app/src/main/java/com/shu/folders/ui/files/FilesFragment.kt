package com.shu.folders.ui.files

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.shu.folders.databinding.FragmentFilesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilesFragment : Fragment() {

    private var _binding: FragmentFilesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private val viewModel: FilesViewModel by viewModels()

    private var adapter: FilesAdapter? = null

    private val args: FilesFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFilesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewModel.setList(args.images.listImages)
        initAdapter()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.images.collect { list ->
                    Log.i("filesFragment", " List ${list.size}")
                    if (adapter == null) {
                        Log.i("filesFragment", "adapter = null")
                    }
                    adapter?.submitList(list)
                }
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun initAdapter() {
        adapter = FilesAdapter {
            toggleButtonStatus(it)
        }
        binding.recycler.layoutManager = GridLayoutManager(context, 6)
        binding.recycler.adapter = adapter
    }

    private fun toggleButtonStatus(
        name: Long
    ) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null
    }
}