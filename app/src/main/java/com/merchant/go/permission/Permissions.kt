package com.merchant.go.permission

import android.Manifest
import android.os.Build

object Permissions {

    const val CAMERA = Manifest.permission.CAMERA

    const val CALL_PHONE = Manifest.permission.CALL_PHONE

    val EXTERNAL_STORAGE = getExternalStoragePer()

    val LOCATION = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val BLE = listOf(
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private fun getExternalStoragePer(): MutableList<String> {
        val permissions: MutableList<String> = ArrayList()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
//            permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
//            permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
//            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        return permissions
    }

}