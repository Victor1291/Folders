package com.shu.folders.fotoZoom

import android.graphics.Matrix
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.shu.folders.databinding.FragmentZoomBinding
import kotlin.math.sqrt


class ZoomFragment : Fragment(), View.OnTouchListener {

    private var _binding: FragmentZoomBinding? = null
    private val binding get() = _binding!!

    private val glide by lazy { Glide.with(this) }

    //  private val args: ZoomFragmentArgs by navArgs()

    // These matrices will be used to scale points of the image
    var matrix: Matrix = Matrix()
    var savedMatrix: Matrix = Matrix()
    var mode = NONE

    // these PointF objects are used to record the point(s) the user is touching
    var start = PointF()
    var mid = PointF()
    var oldDist = 1f


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentZoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*  val url = args.url
          val radius = 10f
          val screenSize = getScreenSize(requireContext())
          glide.load(url)
              *//*.override(screenSize.x, screenSize.y - 40)*//*
            .transform(GranularRoundedCorners(0f, 0f, radius, radius))
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.bg_placeholder)
            .into(binding.imageView)

        binding.imageView.setOnTouchListener(this)*/

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val view = v as ImageView
        view.scaleType = ImageView.ScaleType.MATRIX
        val scale: Float
        if (event != null) {
            dumpEvent(event)

            when (event.action.and(MotionEvent.ACTION_MASK)) {
                MotionEvent.ACTION_DOWN -> {
                    savedMatrix.set(matrix)
                    start[event.x] = event.y
                    Log.d(TAG, "mode=DRAG") // write to LogCat
                    mode = DRAG
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    mode = NONE
                    Log.d(TAG, "mode=NONE")
                }

                MotionEvent.ACTION_POINTER_DOWN -> {
                    oldDist = spacing(event)
                    Log.d(TAG, "oldDist=$oldDist")
                    if (oldDist > 5f) {
                        savedMatrix.set(matrix)
                        midPoint(mid, event)
                        mode = ZOOM
                        Log.d(TAG, "mode=ZOOM")
                    }
                }

                MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                    matrix.set(savedMatrix)
                    matrix.postTranslate(
                        event.x - start.x,
                        event.y - start.y
                    ) // create the transformation in the matrix  of points
                } else if (mode == ZOOM) {
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
                }
            }
        }
        view.imageMatrix = matrix // display the transformation on screen
        return true // indicate event was handled
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