package com.merchant.go.map

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import com.merchant.go.R


class ProvinceItem(val path: Path, val name: String? = null){

    var isSelected = false
    var centerX = -1
    var centerY = -1
    val region: Region = Region()
    var count: Int = 0
    private val rectF = RectF()

    init {
        path.computeBounds(rectF, true)
        region.setPath(path, Region(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt()))
    }

    fun setCenter(centerX: Int, centerY: Int){
        this.centerX = centerX
        this.centerY = centerY
    }

    fun onTouch(x: Float, y: Float): Boolean{
        if (region.contains(x.toInt(), y.toInt())){
            isSelected = true
            return true
        }
        isSelected = false
        return false
    }

    fun onDraw(canvas: Canvas, paint: Paint){
        paint.reset()
        paint.color = getColor()
        paint.style = Paint.Style.FILL
        canvas.drawPath(path, paint)
        if (isSelected){
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4F
            canvas.drawPath(path, paint)
        }

        if (!name.isNullOrEmpty() && centerX > 0 && centerY > 0){
            paint.color = Color.BLACK
            paint.style = Paint.Style.FILL
            paint.strokeWidth = 2F
            canvas.drawText(name, centerX.toFloat(), centerY.toFloat(),paint)
        }
    }

    private fun getColor(): Int{
        return if (count >= 200 ){
            R.color.teal_700
        }else if (count >= 150){
            R.color.teal_200
        }else if (count >= 100){
            R.color.purple_700
        }else if (count >= 50){
            R.color.purple_500
        }else if (count > 0){
            R.color.purple_200
        }else {
            R.color.white
        }
    }
}