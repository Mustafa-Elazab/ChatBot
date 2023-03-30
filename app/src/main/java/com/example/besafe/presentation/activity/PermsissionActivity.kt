package com.example.besafe.presentation.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.besafe.R
import com.example.besafe.utils.PermissionManager
import com.example.besafe.utils.Permissions
import com.example.besafe.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermsissionActivity : AppCompatActivity() {
    private val permissionManager = PermissionManager.from(this)
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_permsission)
        checkPermissionsAndAccessFeature()

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissionsAndAccessFeature() {
        val intentWhenDeniedPermanently = Intent()
        permissionManager
            .request(Permissions.Location)
            .rationale(
                description = "Please approve permission to access this feature",
                title = "Permission required"
            )
            .permissionPermanentlyDeniedIntent(intentWhenDeniedPermanently)
            .permissionPermanentlyDeniedContent(description = "To access this feature we need permission please provide access to app from app settings")
            .checkAndRequestPermission {
                if (it) {

                }
                else
                    toast(getString(R.string.need_this_permission_msg))

            }
    }


}