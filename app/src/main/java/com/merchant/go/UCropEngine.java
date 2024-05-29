package com.merchant.go;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yalantis.ucrop.UCropImageEngine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UCropEngine implements UCropImageEngine {
   @Override
   public void loadImage(Context context, String url, ImageView imageView) {
      Glide.with(context).load(url).into(imageView);
   }

   @Override
   public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
      Glide.with(context).asBitmap().override(maxWidth, maxHeight).load(url).into(new CustomTarget<Bitmap>() {
         @Override
         public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            if (call != null) {
               call.onCall(resource);
            }
         }

         @Override
         public void onLoadFailed(@Nullable Drawable errorDrawable) {
            if (call != null) {
               call.onCall(null);
            }
         }

         @Override
         public void onLoadCleared(@Nullable Drawable placeholder) {
         }
      });
   }
}
