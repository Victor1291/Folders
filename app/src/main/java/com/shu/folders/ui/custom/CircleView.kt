package com.shu.folders.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View


class MyView : View {
    private var circles: MutableList<Circle?> = mutableListOf<Circle?>()
    private var p: Paint = Paint()

    constructor(context: Context?) : super(context)

    constructor(context: Context?,  attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?,  attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onDraw(canvas: Canvas) {

        for (circle in circles) {
            circle?.drawSelf(canvas, p)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //Get finger behavior
        val action = event.action
        var action_code = action and 0xff
        Log.i("test", "action=$action")
        Log.i("test", "action_code=$action_code")
        //The index of the finger
        val pointIndex = action shr 8
        Log.i("test", "pointIndex=$pointIndex")


        //Get the coordinates of the finger
        val x = event.getX(pointIndex)
        val y = event.getY(pointIndex)


        //Get the name ID of the finger
        val pointId = event.getPointerId(pointIndex)
        Log.i("test", "pointId=$pointId")

        if (action_code >= 5) {
            action_code -= 5
        }
        when (action_code) {
            MotionEvent.ACTION_DOWN -> {
                //Instantiate the circle
                val circle: Circle = Circle(x, y, pointId)
                //Add the circle to the collection
                circles.add(circle)
            }

            MotionEvent.ACTION_UP -> circles.remove(getCircleId(pointId))
            MotionEvent.ACTION_MOVE -> {
                var i = 0
                while (i < event.pointerCount) {
                    //Get the name ID of the finger again
                    val id = event.getPointerId(i)
                    getCircleId(id)?.x = event.getX(i)
                    getCircleId(id)?.y = event.getY(i)
                    i++
                }
            }
        }
        //Redraw, call onDraw() again
        invalidate()
        return true
    }

    fun getCircleId(pointId: Int): Circle? {
        for (circle in circles) {
            if (circle?.pointId == pointId) {
                return circle
            }
        }
        return null
    }
}