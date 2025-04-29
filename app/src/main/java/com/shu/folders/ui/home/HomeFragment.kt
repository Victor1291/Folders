package com.shu.folders.ui.home

import android.Manifest
import android.content.DialogInterface
import android.content.IntentSender
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shu.folders.R
import com.shu.folders.databinding.FragmentHomeBinding
import com.shu.folders.models.MediaStoreImage
import com.shu.folders.models.ViewPagerItem
import com.shu.folders.ui.home.ItemTypes.CARD
import com.shu.folders.ui.home.ItemTypes.HEADER
import com.shu.folders.ui.home.viewHolders.CardViewHolder
import com.shu.folders.ui.home.viewHolders.HeaderViewHolder
import com.shu.folders.ui.home.viewHolders.OneLine2ViewHolder
import com.shu.folders.ui.home.viewHolders.TwoStringsViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/** The request code for requesting [Manifest.permission.READ_EXTERNAL_STORAGE] permission. */
private const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045

/**
 * Code used with [IntentSender] to request user permission to delete an image with scoped storage.
 */
private const val DELETE_PERMISSION_REQUEST = 0x1033

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val menuHost: MenuHost get() = requireActivity()

    private val viewModel by viewModels<HomeViewModel>()

    private lateinit var viewHoldersManager: ViewHoldersManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewHoldersManager = ViewHoldersManagerImpl().apply {
            registerViewHolder(ItemTypes.HEADER, HeaderViewHolder())
            registerViewHolder(ItemTypes.ONE_LINE_STRINGS, OneLine2ViewHolder())
            registerViewHolder(ItemTypes.TWO_STRINGS, TwoStringsViewHolder())
            registerViewHolder(ItemTypes.CARD, CardViewHolder())
        }

        val root: View = binding.root


        /* viewModel.permissionNeededForDelete.observe(viewLifecycleOwner, Observer { intentSender ->
             intentSender?.let {
                 // On Android 10+, if the app doesn't have permission to modify
                 // or delete an item, it returns an `IntentSender` that we can
                 // use here to prompt the user to grant permission to delete (or modify)
                 // the image.
                 startIntentSenderForResult(
                     intentSender,
                     DELETE_PERMISSION_REQUEST,
                     null,
                     0,
                     0,
                     0,
                     null
                 )
             }
         })*/

        menuHost.addMenuProvider(object : MenuProvider { // Добавляем MenuProvider
            override fun onPrepareMenu(menu: Menu) // Вызывается перед отрисовкой меню
            {
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Надуваем fragment_menu и мержим с прошлым menu
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Пользователь кликнул на элемент меню
                // return true — не нужно передавать нажатие другому провайдеру
                // return false — передаем нажатие следующему провайдеру

                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        Toast.makeText(context, "Click on the Settings", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.action_share -> {

                        Toast.makeText(context, "Mode Macros", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.action_trash -> {

                        Toast.makeText(context, "Mode Edit", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.action_favorite -> {

                        Toast.makeText(context, "Mode Preview", Toast.LENGTH_SHORT).show()
                        true
                    }


                    else -> true
                }
            }

            override fun onMenuClosed(menu: Menu) // Меню закрыто
            {
            }

        }, viewLifecycleOwner)

        //TODO clicklistener
        val galleryAdapter =
            GalleryAdapter(viewHoldersManager, AdapterClickListenerById { clickState ->

                // Log.d("click Fragment", "click  ${clickState.id} , ${clickState.position} ")
                if (clickState.itemTypes == CARD) {
                    Log.d(
                        "click Fragment",
                        "click in adapter ${clickState.id} , ${clickState.position} "
                    )
                    clickState.extras?.let { extras ->
                        findNavController().navigate(
                            HomeFragmentDirections.actionNavigationHomeToOnBoardingFragment(
                                ViewPagerItem(
                                    listImages = viewModel.getImages(),
                                    position = clickState.position,
                                )
                            ),
                            extras
                        )
                    }
                }
                if (clickState.itemTypes == HEADER) {
                    Log.d(
                        "click Fragment",
                        "click header in adapter ${clickState.id} , ${clickState.position} "
                    )
                }
            })

        binding.gallery.also { view ->
            val gridLayoutManager = GridLayoutManager(
                context, 3
            )
            gridLayoutManager.spanSizeLookup =
                object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return viewModel.spanList[position]
                    }
                }
            view.layoutManager = gridLayoutManager
            view.adapter = galleryAdapter
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.images.collect { uiState ->
                    when (uiState) {

                        is UiStateHome.Error -> {
                            Log.e("frHome", "State.Error")
                        }

                        UiStateHome.Loading -> {
                            Log.e("frHome", "State.Loading")

                        }

                        is UiStateHome.Success -> {
                            Log.e("frHome", "State.Success")
                            galleryAdapter.submitList(uiState.images)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showImages() {
        viewModel.loadImages()
        binding.welcomeView.visibility = View.GONE
        binding.permissionRationaleView.visibility = View.GONE
    }

    private fun showNoAccess() {
        binding.welcomeView.visibility = View.GONE
        binding.permissionRationaleView.visibility = View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun openMediaStore() {
        showImages()
        /*  if (haveStoragePermission()) {
              Log.d("permission", " true Permission")

          } else {
              Log.d("permission", " false Permission")
              requestPermission()
          }*/
    }

    private fun goToSettings() {
        /*Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }*/
    }

    /**
     * Convenience method to check if [Manifest.permission.READ_EXTERNAL_STORAGE] permission
     * has been granted to the app.
     */
    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PERMISSION_GRANTED

    /**
     * Convenience method to request [Manifest.permission.READ_EXTERNAL_STORAGE] permission.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
            )
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissions,
                READ_EXTERNAL_STORAGE_REQUEST
            )
        }
    }

    private fun deleteImage(image: MediaStoreImage) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_dialog_title)
            .setMessage(getString(R.string.delete_dialog_message, image.displayName))
            .setPositiveButton(R.string.delete_dialog_positive) { _: DialogInterface, _: Int ->
                viewModel.deleteImage(image)
            }
            .setNegativeButton(R.string.delete_dialog_negative) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .show()
    }

}