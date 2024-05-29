package com.merchant.go;

import android.app.Activity;
import android.graphics.Color;

import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.engine.CropFileEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.yalantis.ucrop.UCrop;

public class PictureUtil {

    public static PictureUtil getSingleton() {
        return PictureSelectorManagerHolder.SINGLETON;
    }

    private static class PictureSelectorManagerHolder {
        private static final PictureUtil SINGLETON = new PictureUtil();
    }

    /**
     * 选择图片
     *
     * @param activity
     */
    public void openCamera1(Activity activity, OnResultCallbackListener<LocalMedia> listener) {
        PictureSelector.create(activity)
                .openCamera(SelectMimeType.ofImage())
                .setOutputCameraDir(FileExt.INSTANCE.getSDCardFilePath())
                .forResult(listener);
    }

    /**
     * 选择图片
     *
     * @param activity
     */
    public void openCamera2(Activity activity, OnResultCallbackListener<LocalMedia> listener) {
        PictureSelector.create(activity)
                .openCamera(SelectMimeType.ofImage())
                .setOutputCameraDir(FileExt.INSTANCE.getStorageImagePath(activity))
                .forResult(listener);
    }

    /**
     * 选择图片
     *
     * @param activity
     */
    public void pictureSelectorInit(Activity activity,OnResultCallbackListener<LocalMedia> listener) {

        PictureSelector.create(activity)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.getEngine())
                .setSelectionMode(SelectModeConfig.SINGLE)
                .isPreviewImage(false)
                .isDisplayCamera(false)
                .isSelectZoomAnim(true)
                .forResult(listener);
    }

    /**
     * 选择图片裁剪
     *
     * @param activity
     */
    public void pictureSelectorCropInit(Activity activity) {

        PictureSelector.create(activity)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.getEngine())
                .setSelectionMode(SelectModeConfig.SINGLE)
                .isPreviewImage(false)
                .isDisplayCamera(false)
                .setCropEngine(createCropFileEngine())
                .isSelectZoomAnim(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    private CropFileEngine createCropFileEngine(){
        return (fragment, srcUri, destinationUri, dataSource, requestCode) -> {
            UCrop uCrop = UCrop.of(srcUri, destinationUri, dataSource);
            uCrop.setImageEngine(new UCropEngine());
            uCrop.withAspectRatio(1,1);
            uCrop.withOptions(buildOptions());
            uCrop.start(fragment.requireActivity(), fragment, requestCode);
        };
    }

    private UCrop.Options buildOptions(){
        UCrop.Options options = new UCrop.Options();
        options.setMaxScaleMultiplier(4);
//        options.isCropDragSmoothToCenter(true);
        options.setFreeStyleCropEnabled(true);
        options.setHideBottomControls(true);
        options.isDarkStatusBarBlack(true);
        options.setStatusBarColor(Color.WHITE);
        return options;
    }

}
