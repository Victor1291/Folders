package com.shu.folders

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.shu.folders.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.values.all { it }) {
                Toast.makeText(this, "permission is Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "permission is not Granted", Toast.LENGTH_SHORT).show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        checkPermissions()
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun checkPermissions() {

        val isAllGranted = REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

        }
        //ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (isAllGranted) {
            Toast.makeText(this, "permission is Granted", Toast.LENGTH_SHORT).show()
        } else {

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_MEDIA_LOCATION)
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)
            shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VIDEO)
            shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)

            launcher.launch(REQUIRED_PERMISSIONS)
        }
    }

    companion object {

        private val REQUIRED_PERMISSIONS = buildList {
            // For Android Tiramisu (33) and above, use media permissions for scoped storage
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this += android.Manifest.permission.ACCESS_MEDIA_LOCATION
                this += android.Manifest.permission.READ_MEDIA_VIDEO
                this += android.Manifest.permission.READ_MEDIA_IMAGES
            }
            // For Android Upside Down Cake (34) and above, add permission for user-selected visual media
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                this += android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            // For Android versions below Tiramisu 10(29), request WRITE_EXTERNAL_STORAGE for
            // legacy storage access
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
                this += android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                this += android.Manifest.permission.READ_EXTERNAL_STORAGE
            }
        }.toTypedArray()
    }
}