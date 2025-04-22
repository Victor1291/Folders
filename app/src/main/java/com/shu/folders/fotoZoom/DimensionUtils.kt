package com.shu.folders.fotoZoom

import android.content.Context
import android.graphics.Point
import android.view.WindowManager


//TODO fix deprecation in API 30
fun getScreenSize(context: Context): Point {
    val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}