package com.shu.folders.ui.notifications

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.shu.folders.databinding.FragmentNotificationsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private val viewModel by viewModels<AppViewModel>()

    private var _binding: FragmentNotificationsBinding? = null

    private val takePicture = registerForActivityResult(TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            viewModel.saveImageFromCamera(bitmap)
        }
    }

    private val selectPicture = registerForActivityResult(GetContentWithMimeTypes()) { uri ->
        uri?.let {
            viewModel.copyImageFromUri(uri)
        }
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.takePicture.setOnClickListener {
            takePicture.launch(null)
        }

        binding.selectPicture.setOnClickListener {
            selectPicture.launch(ACCEPTED_MIMETYPES)
        }

        binding.addRandomImage.setOnClickListener {
            viewModel.saveRandomImageFromInternet()
        }

        binding.clearFiles.setOnClickListener {
            viewModel.clearFiles()
        }

        viewModel.notification.observe(viewLifecycleOwner, Observer {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class GetContentWithMimeTypes : ActivityResultContract<Array<String>, Uri?>() {
    override fun createIntent(
        context: Context,
        input: Array<String>
    ): Intent {
        return Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("*/*")
            .putExtra(Intent.EXTRA_MIME_TYPES, input);

    }

    override fun getSynchronousResult(
        context: Context,
        input: Array<String>
    ): SynchronousResult<Uri?>? {
        return null
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
    }
}