package com.example.testproject.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Region
import android.util.AttributeSet
import android.view.View
import org.xml.sax.Attributes
import java.nio.file.Path

class MapView: View {

    constructor(context: Context): this(context, null)
    constructor(context: Context, attributes: AttributeSet?): this(context, attributes, 0)
    constructor(context: Context, attributes: AttributeSet?, style: Int):super(context, attributes, style){
        init(context)
    }

    private fun init(context: Context) {

    }

    

}