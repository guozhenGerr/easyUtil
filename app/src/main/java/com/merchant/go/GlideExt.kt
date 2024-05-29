package com.merchant.go

import android.content.Context
import android.widget.ImageView
import androidx.annotation.IdRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.merchant.go.R

fun ImageView.loadImage(resId: Int){
    Glide.with(this)
        .load(resId)
        .error(R.mipmap.bg_manage_no)
        .into(this)
}
fun ImageView.loadImage(url: String?){
    Glide.with(this)
        .load(url)
        .error(R.mipmap.bg_manage_no)
        .into(this)
}

fun ImageView.loadImageHolder(url: String?){
    Glide.with(this)
        .load(url)
        .error(R.mipmap.bg_manage_no)
        .placeholder(R.mipmap.bg_manage_no)
        .into(this)
}

fun ImageView.loadImageCenterCrop(url: String?, corner: Int){
    Glide.with(this)
        .load(url)
        .error(R.mipmap.bg_manage_no)
        .transform(CenterCrop(),RoundedCorners(dip2px(context, corner.toFloat())))
        .into(this)
}

fun ImageView.loadImageCenterCropHolder(url: String?, corner: Int, holder:Int = R.mipmap.bg_manage_no){
    Glide.with(this)
        .load(url)
        .placeholder(holder)
        .error(holder)
        .transform(CenterCrop(),RoundedCorners(dip2px(context, corner.toFloat())))
        .into(this)
}

fun ImageView.loadImageHolder(url: String?, corner: Int, holder:Int = R.mipmap.bg_manage_no){
    Glide.with(this)
        .load(url)
        .error(holder)
        .placeholder(holder)
        .transform(RoundedCorners(dip2px(context, corner.toFloat())))
        .into(this)
}

fun ImageView.loadImage(url: String?, corner: Int){
    Glide.with(this)
        .load(url)
        .error(R.mipmap.bg_manage_no)
        .placeholder(R.mipmap.bg_manage_no)
        .transform(RoundedCorners(dip2px(context, corner.toFloat())))
        .into(this)
}

fun ImageView.loadImageCircle(url: String?, holder:Int = R.mipmap.ic_portrait){
    Glide.with(this)
        .load(url)
        .error(holder)
        .placeholder(holder)
        .transform(CircleCrop())
        .into(this)
}

fun dip2px(context: Context, dip: Float): Int {
    if (dip <= 0)return 0
    val scale: Float = context.resources.displayMetrics.density
    return (dip * scale + 0.5f).toInt()
}