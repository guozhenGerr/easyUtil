package com.merchant.go

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files

object GalleryUtil {

    @JvmStatic
    fun getMonitorImagePath(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath
        }else {
            Environment.getExternalStorageDirectory().absolutePath + File.separator + context.packageName + Environment.DIRECTORY_PICTURES
        }
    }

    @JvmStatic
    fun getMonitorVideoPath(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)!!.absolutePath
        }else {
            Environment.getExternalStorageDirectory().absolutePath + File.separator + context.packageName + Environment.DIRECTORY_MOVIES
        }
    }

    /**
     * 针对监控器保存的图片和视屏进行刷新或者保存
     */
    @JvmStatic
    fun saveMonitorImage(context: Context, filePath: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            saveImageToGalleryQ(context, filePath.split(File.separator).last(), BitmapFactory.decodeFile(filePath))
        }else {
            val mediaType = "image/*"
            MediaScannerConnection.scanFile(context, arrayOf(filePath), arrayOf(mediaType), null)
        }
    }

    /**
     * 针对监控器保存的图片和视屏进行刷新或者保存
     */
    @JvmStatic
    fun saveMonitorVideo(context: Context, filePath: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            saveVideoToGalleryQ(context, filePath)
        }else {
            val mediaType = "video/*"
            MediaScannerConnection.scanFile(context, arrayOf(filePath), arrayOf(mediaType), null)
        }
    }

    /**
     * android 29以下 保存
     */
    fun saveImageToGallery(context: Context, fileName: String, image: Bitmap): Boolean{
        val dir = getMonitorImagePath(context)
        val fileDir = File(dir)
        if (!fileDir.exists())fileDir.mkdirs()

        val file = File(dir, fileName)
        try {
            FileOutputStream(file).use {outputStream ->
                image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
            }

            val mediaType = "image/*"
            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf(mediaType), null)
            return true
        }catch (e: Exception){
            e.printStackTrace()
        }
        return false
    }

    /**
     * android 29以下
     */
    fun saveVideoToGallery(context: Context, filePath: String, fileName: String): Boolean{
        val dir = getMonitorVideoPath(context)
        val tempFile = File(filePath)
        val file = File(dir, fileName)
        try {
            val buf = ByteArray(1024)
            FileInputStream(tempFile).use { inputStream ->
                BufferedOutputStream(FileOutputStream(file)).use { outputStream ->
                    var len: Int
                    while (inputStream.read(buf).also { len = it } != -1) {
                        outputStream.write(buf, 0, len)
                    }
                    inputStream.close()
                    outputStream.flush()
                    outputStream.close()
                }
            }
            val mediaType = "video/*"
            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf(mediaType), null)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * android 29及以上
     */
    private fun saveImageToGalleryQ(context: Context, fileName: String, image: Bitmap): Boolean{
        val dir = getMonitorImagePath(context)
        val contentValues = getImageContentValues( fileName, dir)
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.apply {
            try {
                val outputStream = resolver.openOutputStream(uri)
                val compress = image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                if (!compress)return false
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentValues.putNull(MediaStore.MediaColumns.DATE_EXPIRES)
                resolver.update(this, contentValues, null, null)
            }catch (e: Exception){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)resolver.delete(this, null)
                return false
            }
            return true
        }
        return false
    }

    /**
     * android 29及以上 复制到相册
     */
    private fun saveVideoToGalleryQ(context: Context, filePath: String){
        val dir = getMonitorVideoPath(context)
        val tempVideo = File(filePath)
        val contentValues = getVideoContentValues( tempVideo, dir)
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            resolver.openOutputStream(uri)?.use {
                Files.copy(tempVideo.toPath(), it)
                it.close()
            }
        }
        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)
    }

    private fun getImageContentValues(imageName: String, relativePath: String): ContentValues {
        return ContentValues().apply {
            val systemTimeTemp = System.currentTimeMillis() / 1000
            put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.IS_PENDING, 1)
            put(MediaStore.Images.Media.RELATIVE_PATH,relativePath)
            put(MediaStore.Video.Media.DATE_ADDED, systemTimeTemp)
            put(MediaStore.Video.Media.DATE_MODIFIED, systemTimeTemp)
        }
    }

    private fun getVideoContentValues(tempVideo: File, relativePath: String): ContentValues {
        return ContentValues().apply {
            val systemTimeTemp = System.currentTimeMillis() / 1000
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
                put(MediaStore.Video.Media.RELATIVE_PATH, relativePath)
            }
            put(MediaStore.Video.Media.TITLE, tempVideo.name)
            put(MediaStore.Video.Media.DISPLAY_NAME, tempVideo.name)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DATE_TAKEN, systemTimeTemp)
            put(MediaStore.Video.Media.DATE_ADDED, systemTimeTemp)
            put(MediaStore.Video.Media.DATE_MODIFIED, systemTimeTemp)
            put(MediaStore.Video.Media.SIZE, tempVideo.length())
        }
    }
}