package com.merchant.go;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;


import java.io.File;
import java.util.List;

import androidx.core.content.FileProvider;

/**
 * Created by sam on 2017/6/1.
 * 调用相机工具类
 */

public class CameraUtils {

    public static Uri takePhotoUri = null;

    public static Uri albumPhotonUri = null;

    public static final int CODE_TAKE_PHOTO = 0;

    public static final int CODE_TAKE_PHOTO_ZOOM = 1;

    public static final int CODE_ALBUM_CHOOSE = 2;

    public static final int CODE_ALBUM_CHOOSE_ZOOM = 3;

    /**
     * 调用相机拍照
     *
     * @param activity 调用的Activity
     */
    public static void takePhoto(Activity activity) {

        String photoName = System.currentTimeMillis() + ".jpg";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), photoName);
        Uri uri = Uri.fromFile(file);
        takePhotoUri = uri;
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, CODE_TAKE_PHOTO);
    }

    /**
     * 相机拍照裁剪
     *
     * @param activity
     */
    public static void takePhotoZoom(Activity activity) {
        if (null != takePhotoUri) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(takePhotoUri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoUri);
            intent.putExtra("return-data", false);
            intent.putExtra("noFaceDetection", true);
            activity.startActivityForResult(intent, CODE_TAKE_PHOTO_ZOOM);
        }
    }

    /**
     * 相册选择图片
     *
     * @param activity
     */
    public static void albumChoose(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, CODE_ALBUM_CHOOSE);
    }

    /**
     * 裁剪相册选择的图片
     *
     * @param activity
     * @param uri
     */
    public static void albumChooseZoom(Activity activity, Uri uri) {
        if (null != uri) {

            /*String photoName = System.currentTimeMillis() + ".jpg";
            File file = new File(Environment.getExternalStorageDirectory(), photoName);
            try {
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            albumPhotonUri = Uri.fromFile(file);*/

            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, albumPhotonUri);
            intent.putExtra("return-data", false);
            intent.putExtra("noFaceDetection", true);
            activity.startActivityForResult(intent, CODE_ALBUM_CHOOSE_ZOOM);
        }
    }

    @TargetApi(19)
    public static void handleImageOnKitKat(Activity activity, Intent data) {
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(activity, uri)) {
            // 如果是document类型的Uri，则通过document id进行处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                albumPhotonUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("com.android.provides.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                albumPhotonUri = contentUri;
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果不是document类型的uri，则使用普通方式处理
            albumPhotonUri = uri;
        }

        albumChooseZoom(activity, uri);
    }

    public static void handleImageBeforeKitKat(Activity activity, Intent data) {
        Uri uri = data.getData();

        albumChooseZoom(activity, uri);
    }



    public static String SaveFullImage(Context context, int requestCode) {
        String path = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Camera";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File path1 = new File(path);//父目录路径
        if (!path1.exists()) {
            path1.mkdirs();
        }
        long curTime = System.currentTimeMillis();

        String currentPicPath = path + "/" + curTime + ".jpg";
        File file = new File(path1, curTime + ".jpg");//file文件指代curTime+.jpg 为图片名图片， 在包名为path1 下的图片文件

        Uri mOutPutFileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mOutPutFileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.grantUriPermission(context.getPackageName(), mOutPutFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            mOutPutFileUri = Uri.fromFile(file); //Uri解析路径 对象包含currentPicPath 整个路径
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);//图片保存为临时文件
        }
        try {
            ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            if (componentName != null) {
                ((Activity) context).startActivityForResult(intent, requestCode);
            }
        } catch (Exception e) {

//            ToastUtil.showToast(context, "请到设置中开启相机权限");
        }
        return currentPicPath;
    }


    public interface onPermissionResponds {
        void responds();
    }


}
