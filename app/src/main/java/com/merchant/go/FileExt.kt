package com.merchant.go

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object FileExt {

    private val appName = "kuaiJinGo"

    fun getStoragePermission(): List<String> {
        mutableListOf<String>().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                //targetSdk >= 33 android 13
                add(Manifest.permission.READ_MEDIA_IMAGES)
                add(Manifest.permission.READ_MEDIA_VIDEO)
                add(Manifest.permission.READ_MEDIA_AUDIO)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //targetSdk >= 30 android 11
                add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //targetSdk >= 29 android 10
                //此时需要 清单文件添加 android:requestLegacyExternalStorage="true"
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //targetSdk >= 23 android 6.0
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            return this
        }
    }

    fun getSDCardImagePath(): String {
        return getSDCardPath(Environment.DIRECTORY_PICTURES)
    }

    fun getSDCardVideoPath(): String {
        return getSDCardPath(Environment.DIRECTORY_MOVIES)
    }

    fun getSDCardFilePath(): String {
        return getSDCardPath(Environment.DIRECTORY_DOCUMENTS)
    }

    private fun getSDCardPath(type: String): String {
        val storageState = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == storageState) {
            val path =
                Environment.getExternalStorageDirectory().absolutePath + File.separator + appName + File.separator + type + File.separator
            val filesDir = File(path)
            filesDir.apply {
                if (!this.exists()) this.mkdirs()
            }
            return path
        } else {
            throw Exception("外部储存无法使用")
        }
    }

    fun getCashPath(context: Context): String {
        return context.filesDir.absolutePath
    }

    fun getStorageImagePath(context: Context): String {
        return getStoragePath(context, Environment.DIRECTORY_PICTURES)
    }

    fun getStorageVideoPath(context: Context): String {
        return getStoragePath(context, Environment.DIRECTORY_MOVIES)
    }

    fun getStorageFilePath(context: Context): String {
        return getStoragePath(context, Environment.DIRECTORY_DOCUMENTS)
    }

    private fun getStoragePath(context: Context, type: String): String {
        val storageState = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == storageState) {
            val filesDir = context.getExternalFilesDir(type)!!
            filesDir.apply {
                if (!this.exists()) this.mkdirs()
            }
            return filesDir.absolutePath
        } else {
            throw Exception("外部储存无法使用")
        }
    }

    enum class MEDIA(val value: String) {
        IMAGE("image/*"), VIDEO("video/*")
    }

    /**
     * 创建图片文件路径
     * 返回 /storage/emulated/0/Android/data/包名/Pictures/xxx.jpg(fileName)
     *
     * sdk29以后 返回私有路径需要插入到图片库才能显示图片
     * sdk29之前 返回公共路径 保存后扫描媒体文件即可
     */
    fun createImagePath(context: Context, fileName: String): String {
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        }
        return File(dir, fileName).absolutePath
    }

    /**
     * 创建视频文件路径
     * 返回 /storage/emulated/0/Android/data/包名/Movies/xxx.mp4(fileName)
     *
     * sdk29以后 返回私有路径需要插入到图片库才能显示视频
     * sdk29之前 返回公共路径 保存后扫描媒体文件即可
     */
    fun createVideoPath(context: Context, fileName: String): String {
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        }
        return File(dir, fileName).absolutePath
    }


    fun saveImage(context: Context, imagePath: String) {

    }

    /**
     * 从其他地方保存到本地
     */
    fun saveVideo(context: Context, videoPath: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val dirParent = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
            val tempVideo = File(videoPath)
            val targetVideo = File(dirParent, context.packageName + File.separator + tempVideo.name)

            try {
                val buf = ByteArray(1024)
                FileInputStream(tempVideo).use { inputStream ->
                    BufferedOutputStream(FileOutputStream(targetVideo)).use { outputStream ->
                        var len: Int
                        while (inputStream.read(buf).also { len = it } != -1) {
                            outputStream.write(buf, 0, len)
                        }
                        inputStream.close()
                        outputStream.flush()
                        outputStream.close()
                    }
                }
                scanMedia(context, targetVideo.absolutePath, MEDIA.VIDEO)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            copyFileToGallery(context, File(videoPath), MEDIA.VIDEO)
        }
    }


    // sdk 小于29 保存成功后扫描
    private fun scanMedia(context: Context, filePath: String, type: MEDIA = MEDIA.IMAGE) {
        MediaScannerConnection.scanFile(
            context,
            arrayOf(filePath),
            arrayOf(type.value)
        ) { path, uri -> /*扫描结束*/ }
    }


    private fun copyFileToGallery(context: Context, file: File, type: MEDIA = MEDIA.IMAGE) {
        val contentValues = if (type == MEDIA.IMAGE)getImageContentValues(context, file) else getVideoContentValues(context, file)
        val contentResolver = context.contentResolver

        val collectionUri = if (type == MEDIA.IMAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val imageUri = contentResolver.insert(collectionUri, contentValues)

        imageUri?.let { uri ->
            try {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val inputStream = FileInputStream(file)
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)
                }
            } catch (e: IOException) {
            }
        }
    }


    private fun getImageContentValues(context: Context, tempImage: File): ContentValues {
        return ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, tempImage.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + File.separator + context.packageName
                )
            }
        }
    }

    private fun getVideoContentValues(context: Context, tempVideo: File): ContentValues {
        return ContentValues().apply {
            val systemTimeTemp = System.currentTimeMillis() / 1000
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
                put(
                    MediaStore.Video.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_MOVIES + File.separator + context.packageName
                )
            }
            put(MediaStore.Video.Media.TITLE, tempVideo.name)
            put(MediaStore.Video.Media.DISPLAY_NAME, tempVideo.name)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DATE_TAKEN, systemTimeTemp)
            put(MediaStore.Video.Media.DATE_MODIFIED, systemTimeTemp)
            put(MediaStore.Video.Media.DATE_ADDED, systemTimeTemp)
            put(MediaStore.Video.Media.SIZE, tempVideo.length())
        }
    }
}