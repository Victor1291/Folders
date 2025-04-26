package com.shu.folders.ui.custom

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.sqrt

private const val BIGGER = 1.07f
private const val SMALL = 0.93f

class ImageViewZoom2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyle), View.OnTouchListener {


    // These matrices will be used to scale points of the image
    private var matrix: Matrix = Matrix()
    private var savedMatrix: Matrix = Matrix()
    private var mode = NONE

    // these PointF objects are used to record the point(s) the user is touching
    private var start = PointF()
    private var mid = PointF()
    private var oldDist = 1f


    // ********* Double click to zoom in and shrink *********
    /**
     * The value of the zoom when initialization
     */
    private var mInitScale = 1f

    /**
     * Double-click the value of the magnification arrival
     */
    private var mMidScale = 2.0f


    private var mGestureDetector: GestureDetector? = null
    private var isAutoScale = false

    /**
     * onDrag only on Big
     */
    private var isSmall = true

    init {
        mGestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                // Double-click event
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    if (isAutoScale) {
                        return true
                    }
                    val x = e.x
                    val y = e.y

                    if (getScale() < mMidScale) {
                        postDelayed(AutoScaleRunnable(mMidScale, x, y), 16)
                        isSmall = false
                        mode = DRAG
                        isAutoScale = true
                    } else {
                        postDelayed(AutoScaleRunnable(mInitScale, x, y), 16)
                        isSmall = true
                        mode = NONE
                        isAutoScale = true
                    }
                    return true
                }

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    //  onImageSelectListener?.onImageClick()
                    return true
                }
            })

        setOnTouchListener(this)
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        if (event != null) {

            if (mGestureDetector?.onTouchEvent(event) == true) {
                Log.d(TAG, "onTouch return")
                return true
            }
            val view = v as ImageView
            view.scaleType = ImageView.ScaleType.MATRIX
            val scale: Float



            dumpEvent(event)

            when (event.action.and(MotionEvent.ACTION_MASK)) {

                MotionEvent.ACTION_DOWN -> {
                    if (!isSmall) {
                        savedMatrix.set(matrix)
                        start[event.x] = event.y
                        Log.d(TAG, "mode=DRAG  $parent ,**** ${parent.parent}") // write to LogCat
                        mode = DRAG
                        if (parent is ViewPager2)
                            parent.requestDisallowInterceptTouchEvent(true)
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    if (isSmall) {
                        mode = NONE
                        Log.d(TAG, "mode=NONE")
                    }
                }

               /* MotionEvent.ACTION_POINTER_DOWN -> {
                    oldDist = spacing(event)
                    Log.d(TAG, "oldDist=$oldDist")
                    if (oldDist > 5f) {
                        savedMatrix.set(matrix)
                        midPoint(mid, event)
                        mode = ZOOM
                        Log.d(TAG, "mode=ZOOM")
                    }
                }*/

                MotionEvent.ACTION_MOVE -> if (mode == DRAG && !isSmall) {
                    matrix.set(savedMatrix)
                    matrix.postTranslate(
                        event.x - start.x,
                        event.y - start.y
                    ) // create the transformation in the matrix  of points
                    // parent.requestDisallowInterceptTouchEvent(true)
                    parent.parent.requestDisallowInterceptTouchEvent(true)
                } /*else if (mode == ZOOM) {
                    // pinch zooming
                    val newDist = spacing(event)
                    Log.d(TAG, "newDist=$newDist")
                    if (newDist > 5f) {
                        matrix.set(savedMatrix)
                        scale = newDist / oldDist // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y)
                    }
                }*/
            }

            view.imageMatrix = matrix // display the transformation on screen
        }
        return true // indicate event was handled

    }

    /**
     * @param mTargetScale
     * @param x
     * @param y
     */
    inner class AutoScaleRunnable(
        private val mTargetScale: Float = 0f,
        private val x: Float = 0f,
        private val y: Float = 0f
    ) : Runnable {


        private var tmpScale = 0f


        init {
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER
                isSmall = false
            }
            if (getScale() > mTargetScale) {
                tmpScale = SMALL
                isSmall = true

            }
        }

        override fun run() {
            // Zoom
            matrix.postScale(tmpScale, tmpScale, x, y)
            //  checkBorderAndCenterWhenScale()
            setImageMatrix(matrix)
            val currentScale: Float = getScale()

            if ((tmpScale > 1.0f && currentScale < mTargetScale) || (tmpScale < 1.0f && currentScale > mTargetScale)) {
                // This method is to re-call the Run () method
                postDelayed(this, 16)
            } else {
                // Set to our target value
                val scale = mTargetScale / currentScale
                matrix.postScale(scale, scale, x, y)
                // checkBorderAndCenterWhenScale()
                setImageMatrix(matrix)
                isAutoScale = false
            }
        }
    }

    /**
     * Get the zoom value of the current picture
     *
     * @return
     */
    fun getScale(): Float {
        val values = FloatArray(9)
        matrix.getValues(values)
        return values[Matrix.MSCALE_X]
    }

    /*
        * --------------------------------------------------------------------------
        * Method: spacing Parameters: MotionEvent Returns: float Description:
        * checks the spacing between the two fingers on touch
        * ----------------------------------------------------
        */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */
    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    /** Show an event in the LogCat view, for debugging  */
    private fun dumpEvent(event: MotionEvent) {
        val names = arrayOf(
            "DOWN",
            "UP",
            "MOVE",
            "CANCEL",
            "OUTSIDE",
            "POINTER_DOWN",
            "POINTER_UP",
            "7?",
            "8?",
            "9?"
        )
        val sb = StringBuilder()
        val action = event.action
        val actionCode = action and MotionEvent.ACTION_MASK
        sb.append("event ACTION_").append(names[actionCode])
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action shr MotionEvent.ACTION_POINTER_ID_SHIFT)
            sb.append(")")
        }
        sb.append("[")
        for (i in 0 until event.pointerCount) {
            sb.append("#").append(i)
            sb.append("(pid ").append(event.getPointerId(i))
            sb.append(")=").append(event.getX(i).toInt())
            sb.append(",").append(event.getY(i).toInt())
            if (i + 1 < event.pointerCount) sb.append(";")
        }
        sb.append("]")
        Log.d("Touch Events ---------", sb.toString())
    }

    companion object {
        private const val TAG = "Touch"
        private const val MIN_ZOOM = 1f
        private const val MAX_ZOOM = 1f

        // The 3 states (events) which the user is trying to perform
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
    }

}
