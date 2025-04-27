package com.shu.folders.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Region
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import kotlin.math.atan2
import kotlin.math.sqrt


class CircularIndicator(context: Context?, attributeSet: AttributeSet?) :
    View(context, attributeSet) {
    private val backgroundPaint = Paint()
    private val foregroundPaint: Paint
    private var selectedAngle: Int
    private var clipPath: Path? = null
    private var pressed: Boolean
    private var circleSize = 0
    private val gestureListener: GestureDetector
    private val angleScroller: Scroller
    private val angleAnimator: ValueAnimator? = null

    init {
        backgroundPaint.color = DEFAULT_BG_COLOR
        backgroundPaint.style = Paint.Style.FILL

        foregroundPaint = Paint()
        foregroundPaint.color = DEFAULT_FG_COLOR
        foregroundPaint.style = Paint.Style.FILL

        selectedAngle = 280
        pressed = false

        angleScroller = Scroller(context, null, true)
        angleScroller.finalX = selectedAngle


        //        angleAnimator = ValueAnimator.ofFloat(0, 360);
//        angleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                if(!angleScroller.isFinished()) {
//                    angleScroller.computeScrollOffset();
//                    selectedAngle = angleScroller.getCurrX();
//                    postInvalidate();
//                } else {
//                    angleAnimator.cancel();
//                    selectedAngle = angleScroller.getCurrX();
//                    postInvalidate();
//                }
//            }
//        });
        gestureListener = GestureDetector(context, object : GestureDetector.OnGestureListener {
            var processed: Boolean = false

            override fun onDown(event: MotionEvent): Boolean {
                processed = computeAndSetAngle(event.x, event.y)
                if (processed) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    changePressedState(true)
                    postInvalidate()
                }
                return processed
            }

            override fun onShowPress(e: MotionEvent) {
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                endGesture()
                return false
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                computeAndSetAngle(e2.x, e2.y)
                postInvalidate()
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                endGesture()
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                return false
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        val notFinished = angleScroller.computeScrollOffset()
        selectedAngle = angleScroller.currX

        if (pressed) {
            foregroundPaint.color = PRESSED_FG_COLOR
        } else {
            foregroundPaint.color = DEFAULT_FG_COLOR
        }

        circleSize = width
        if (height < circleSize) circleSize = height

        val horMargin = (width - circleSize) / 2
        val verMargin = (height - circleSize) / 2

        // create a clipPath the first time
        if (clipPath == null) {
            val clipWidth = (circleSize * 0.75).toInt()

            val clipX = (width - clipWidth) / 2
            val clipY = (height - clipWidth) / 2
            clipPath = Path()
            clipPath!!.addArc(
                clipX.toFloat(),
                clipY.toFloat(),
                (clipX + clipWidth).toFloat(),
                (clipY + clipWidth).toFloat(),
                0f, 360f
            )
        }

        canvas.clipRect(0, 0, width, height)
        canvas.clipPath(clipPath!!, Region.Op.DIFFERENCE)

        canvas.save()
        canvas.rotate(-90f, (width / 2).toFloat(), (height / 2).toFloat())

        canvas.drawArc(
            horMargin.toFloat(),
            verMargin.toFloat(),
            (horMargin + circleSize).toFloat(),
            (verMargin + circleSize).toFloat(),
            0f, 360f, true, backgroundPaint
        )

        canvas.drawArc(
            horMargin.toFloat(),
            verMargin.toFloat(),
            (horMargin + circleSize).toFloat(),
            (verMargin + circleSize).toFloat(),
            0f, selectedAngle.toFloat(), true, foregroundPaint
        )

        canvas.restore()

        if (notFinished) invalidate()
    }

    private fun endGesture() {
        parent.requestDisallowInterceptTouchEvent(false)
        changePressedState(false)
        postInvalidate()
    }

    private fun changePressedState(pressed: Boolean) {
        this.pressed = pressed
    }

    private fun computeAndSetAngle(x: Float, y: Float): Boolean {
        var x = x
        var y = y
        x -= (width / 2).toFloat()
        y -= (height / 2).toFloat()

        val radius = sqrt((x * x + y * y).toDouble())
        if (radius > circleSize / 2) return false

        var angle = (180.0 * atan2(y.toDouble(), x.toDouble()) / Math.PI).toInt() + 90
        angle = (if ((angle > 0)) angle else 360 + angle)

        if (angleScroller.computeScrollOffset()) {
            angleScroller.forceFinished(true)
        }

        angleScroller.startScroll(angleScroller.currX, 0, angle - angleScroller.currX, 0)
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureListener.onTouchEvent(event)
    }

    companion object {
        private val TAG: String = CircularIndicator::class.java.name

        private const val DEFAULT_FG_COLOR = -0x10000
        private const val PRESSED_FG_COLOR = -0xffff01
        private const val DEFAULT_BG_COLOR = -0x5f5f60
        private const val FLING_SCALE = 200
    }
}