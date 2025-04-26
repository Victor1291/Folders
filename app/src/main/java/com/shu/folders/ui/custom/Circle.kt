package com.shu.folders.ui.custom

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.random.Random


class Circle(var x: Float, var y: Float, var pointId: Int) {
    var r: Int = 100
    var red: Int
    var green: Int
    var blue: Int

    init {
        red = Random.nextInt(255)
        green = Random.nextInt(255)
        blue = Random.nextInt(255)
    }

    fun drawSelf(canvas: Canvas, paint: Paint) {
        paint.color = Color.rgb(red, green, blue)
        canvas.drawCircle(x, y, r.toFloat(), paint)
    }
}