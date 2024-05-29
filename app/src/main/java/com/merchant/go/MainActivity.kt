package com.merchant.go

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.easyutil.linear
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.merchant.go.databinding.ActivityMainBinding
import com.permissionx.guolindev.PermissionX


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.refresher.setEnableRefresh(false).setEnableLoadMore(false)
            .setEnableOverScrollDrag(true)
//        setOnClick()

        binding.apply {
            takePhoto.setOnClickListener {

                PictureUtil.getSingleton().openCamera1(this@MainActivity,
                    object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: ArrayList<LocalMedia>?) {
                            result?.first()?.apply {
                                binding.imageView.loadImage(this.realPath)
                            }
                        }

                        override fun onCancel() {}

                    })
            }

            selectPhoto.setOnClickListener {

                PictureUtil.getSingleton().openCamera2(this@MainActivity,
                    object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: ArrayList<LocalMedia>?) {
                            result?.first()?.apply {
                                binding.imageView.loadImage(this.realPath)
                            }
                        }

                        override fun onCancel() {}

                    })
            }
        }


    }

    private fun setOnClick() {
        binding.apply {
            takePhoto.setOnClickListener {
                PermissionX.init(this@MainActivity)
                    .permissions(FileExt.getStoragePermission())
                    .onForwardToSettings { scope, deniedList ->
                        scope.showForwardToSettingsDialog(
                            deniedList,
                            "部分权限被取消，请到设置中开启",
                            "去开启",
                            "取消"
                        )
                    }
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            takePhoto()
                        } else {
                            goManagerFileAccess()
                        }
                    }
            }

            selectPhoto.setOnClickListener {
                PermissionX.init(this@MainActivity)
                    .permissions(FileExt.getStoragePermission())
                    .onForwardToSettings { scope, deniedList ->
                        scope.showForwardToSettingsDialog(
                            deniedList,
                            "部分权限被取消，请到设置中开启",
                            "去开启",
                            "取消"
                        )
                    }
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            select()
                        } else {
                            goManagerFileAccess()
                        }
                    }
            }
        }
    }

    var takePhotoPath: String = ""

    private fun takePhoto(){


//        PictureUtil.getSingleton().openCamera(this@MainActivity,
//            object : OnResultCallbackListener<LocalMedia> {
//                override fun onResult(result: ArrayList<LocalMedia>?) {
//                    result?.first()?.apply {
//                        binding.imageView.loadImage(this.realPath)
//                    }
//                }
//
//                override fun onCancel() {}
//
//            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK){
            binding.imageView.loadImage(takePhotoPath)
        }

    }

    private fun select(){
        PictureUtil.getSingleton().pictureSelectorInit(
            this@MainActivity,
            object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    result?.first()?.apply {
                        binding.imageView.loadImage(this.realPath)
                    }
                }

                override fun onCancel() {

                }

            })
    }

    /**
     * 进入Android 11或更高版本的文件访问权限页面
     */
    private fun goManagerFileAccess() {
        // Android 11 (Api 30)或更高版本的写文件权限需要特殊申请，需要动态申请管理所有文件的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val appIntent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            appIntent.data = Uri.parse("package:$packageName")
            //appIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
            try {
                startActivity(appIntent)
            } catch (ex: ActivityNotFoundException) {
                ex.printStackTrace()
                val allFileIntent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(allFileIntent)
            }
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", packageName, null)
            startActivity(intent)
        }
    }


}