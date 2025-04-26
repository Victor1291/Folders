package com.shu.folders.ui.custom

import android.R.attr
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewTreeObserver
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.sqrt


private const val BIGGER = 1.07f
private const val SMALL = 0.93f

class ZoomImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyle),
    ViewTreeObserver.OnGlobalLayoutListener,
    ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    private var mOnce = false

    /**
     * The value of the zoom when initialization
     */
    private var mInitScale = 1f

    /**
     * Double-click the value of the magnification arrival
     */
    private var mMidScale = 1.3f

    /**
     * Zoom in maximum
     */
    private var mMaxScale = 1.5f

    private var mScaleMatrix: Matrix? = null

    /**
     * Proportion to capture user more refers to touch
     */
    private var mScaleGestureDetector: ScaleGestureDetector? = null


    // ********* Free mobile variable ************
    /**
     * Record the number of multi-touch
     */
    private var mLastPointerCount = 0

    private var mLastX = 0f
    private var mLastY = 0f

    private var mTouchSlop = 0
    private var isCanDrag = false

    private var isCheckLeftAndRight = false
    private var isCheckTopAndBottom = false


    // ********* Double click to zoom in and shrink *********
    private var mGestureDetector: GestureDetector? = null

    private var isAutoScale = false


    init {
        mScaleMatrix = Matrix()
        scaleType = ScaleType.MATRIX
        setOnTouchListener(this)
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mScaleGestureDetector = ScaleGestureDetector(context, this)
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
                        isAutoScale = true
                    } else {
                        postDelayed(AutoScaleRunnable(mInitScale, x, y), 16)
                        isAutoScale = true
                    }
                    return true
                }

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                  //  onImageSelectListener?.onImageClick()
                    return true
                }
            })


    }

    /**
     * Get images of imageView load completed
     */
    override fun onGlobalLayout() {
        if (!mOnce) {
            // Get the wide and high of the control
            val width = width
            val height = height


            // Get our picture, as well as wide and high
            val drawable = drawable ?: return
            val dh = drawable.intrinsicHeight
            val dw = drawable.intrinsicWidth

            var scale = 1.0f

            // The width of the picture is greater than the width of the control, and the height of the picture is less than the height of the space, we reduced it.
            if (dw > width && dh < height) {
                scale = width * 1.0f / dw
            }

            // The width of the picture is less than the width of the control, and the height of the picture is greater than the height of the space, we will shrink it.
            if (dh > height && dw < width) {
                scale = height * 1.0f / dh
            }

            // Reduce value
            if (dw > width && dh > height) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh)
            }

            // amplifier
            if (dw < width && dh < height) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh)
            }

            /**
             * Proportion of scale when initialization
             */
            mInitScale = scale
            mMaxScale = mInitScale * 4
            mMidScale = mInitScale * 2


            // Move the picture to the middle of the control
            val dx = getWidth() / 2 - dw / 2
            val dy = getHeight() / 2 - dh / 2

            mScaleMatrix?.postTranslate(dx.toFloat(), dy.toFloat())
            mScaleMatrix?.postScale(
                mInitScale, mInitScale, (width / 2).toFloat(),
                (height / 2).toFloat()
            )
            setImageMatrix(mScaleMatrix)

            mOnce = true
        }
    }


    override fun onScale(detector: ScaleGestureDetector): Boolean {
        var scale = getScale()
        var scaleFactor = detector.scaleFactor

        if (drawable == null) {
            return true
        }
        // Control of zoom range
        if ((scale < mMaxScale && scaleFactor > 1.0f)
            || (scale > mInitScale && scaleFactor < 1.0f)
        ) {
            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale;
            }

            if (scale * scaleFactor > mMaxScale) {
                scale = mMaxScale / scale;
            }

            // Zoom
            mScaleMatrix?.postScale(
                scaleFactor, scaleFactor,
                detector.focusX, detector.focusY
            )

            checkBorderAndCenterWhenScale()

            setImageMatrix(mScaleMatrix)
        }

        return true

    }


    /**
     * Register the ONGLOBALLAYOUTLISTENER this interface
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    /**
     * Cancel the interface of ONGLOBALLAYOUTLISTENER
     */
    @Suppress("deprecation")
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeGlobalOnLayoutListener(this)
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
            }
            if (getScale() > mTargetScale) {
                tmpScale = SMALL
            }
        }

        override fun run() {
            // Zoom
            mScaleMatrix?.postScale(tmpScale, tmpScale, x, y)
            checkBorderAndCenterWhenScale()
            setImageMatrix(mScaleMatrix)
            val currentScale: Float = getScale()

            if ((tmpScale > 1.0f && currentScale < mTargetScale) || (tmpScale < 1.0f && currentScale > mTargetScale)) {
                // This method is to re-call the Run () method
                postDelayed(this, 16)
            } else {
                // Set to our target value
                val scale = mTargetScale / currentScale
                mScaleMatrix?.postScale(scale, scale, x, y)
                checkBorderAndCenterWhenScale()
                setImageMatrix(mScaleMatrix)
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
        mScaleMatrix!!.getValues(values)
        return values[Matrix.MSCALE_X]
    }

    /**
     * Get the picture to zoom in and reduced by LEFT, Right, Top, Bottom
     *
     * @return
     */
    private fun getMatrixRectF(): RectF {
        val matrix = mScaleMatrix!!
        val rectF = RectF()
        val d = drawable
        if (d != null) {
            rectF[0f, 0f, d.intrinsicWidth.toFloat()] = d.intrinsicHeight.toFloat()
            matrix.mapRect(rectF)
        }
        return rectF
    }

    /**
     * Perform boundaries and our location when zooming
     */
    private fun checkBorderAndCenterWhenScale() {
        val rectF = getMatrixRectF()
        var deltaX = 0f
        var deltaY = 0f

        val width = width
        val height = height

        // Boundary detection when zooming, prevent the appearance
        if (rectF.width() >= width) {
            if (rectF.left > 0) {
                deltaX = -rectF.left
            }
            if (rectF.right < width) {
                deltaX = width - rectF.right
            }
        }

        if (rectF.height() >= height) {
            if (rectF.top > 0) {
                deltaY = -rectF.top
            }
            if (rectF.bottom < height) {
                deltaY = height - rectF.bottom
            }
        }

        /**
         * If the width or height is less than the width or high of space,
         */
        if (rectF.width() < width) {
            deltaX = width / 2f - rectF.right + rectF.width() / 2f
        }

        if (rectF.height() < height) {
            deltaY = height / 2f - rectF.bottom + rectF.height() / 2f
        }

        mScaleMatrix!!.postTranslate(deltaX, deltaY)
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        if (event != null) {

            if (mGestureDetector?.onTouchEvent(event) == true) {
                return true
            }
            mScaleGestureDetector?.onTouchEvent(event)


            var x = 0f
            var y = 0f
            // Get the number of multi-touch
            val pointerCount = event.pointerCount
            for (i in 0..< pointerCount) {
                x += event.getX(i)
                y += event.getY(i)
            }

            x /= pointerCount
            y /= pointerCount
            if (mLastPointerCount != pointerCount) {
                isCanDrag = false
                mLastX = x
                mLastY = y
            }
            mLastPointerCount = pointerCount
            val rectF = getMatrixRectF()

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (rectF.width() > width + 0.01 || rectF.height() > height + 0.01) {
                        if (parent is ViewPager2)
                            parent.requestDisallowInterceptTouchEvent(true)
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    if (rectF.width() > width + 0.01 || rectF.height() > height + 0.01) {
                        if (parent.parent is ViewPager2) parent.requestDisallowInterceptTouchEvent(true)
                    }
                    var dx = attr.x - mLastX
                    var dy = attr.y - mLastY


                    if (!isCanDrag) {
                        isCanDrag = isMoveAction(dx, dy)
                    }
                    if (isCanDrag) {
                        if (getDrawable() != null) {
                            isCheckLeftAndRight = isCheckTopAndBottom == true
                            // If the width is less than the control width, the lateral movement is not allowed
                            if (rectF.width() < width) {
                                isCheckLeftAndRight = false
                                dx = 0f
                            }
                            // If the height is less than the control height, longitudinal movement is not allowed
                            if (rectF.height() < height) {
                                isCheckTopAndBottom = false
                                dy = 0f
                            }
                            mScaleMatrix?.postTranslate(dx, dy)
                            checkBorderWhenTranslate()
                            setImageMatrix(mScaleMatrix)
                        }
                    }
                    mLastX = x
                    mLastY = y
                }

                MotionEvent.ACTION_UP -> {}
                MotionEvent.ACTION_CANCEL -> {
                    mLastPointerCount = 0
                }

            }

        }
        return true
    }

    /**
     * Boundary check when moving
     */
    private fun checkBorderWhenTranslate() {
        val rectF = getMatrixRectF()
        var deltaX = 0f
        var deltaY = 0f

        val width = width
        val heigth = height

        if (rectF.top > 0 && isCheckTopAndBottom) {
            deltaY = -rectF.top
        }
        if (rectF.bottom < heigth && isCheckTopAndBottom) {
            deltaY = heigth - rectF.bottom
        }
        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltaX = -rectF.left
        }
        if (rectF.right < width && isCheckLeftAndRight) {
            deltaX = width - rectF.right
        }
        mScaleMatrix!!.postTranslate(deltaX, deltaY)
    }


    /**
     * Judging whether it is MOVE
     *
     * @param dx
     * @param dy
     * @return
     */
    private fun isMoveAction(dx: Float, dy: Float): Boolean {
        return sqrt((dx * dx + dy * dy).toDouble()) > mTouchSlop
    }

}

