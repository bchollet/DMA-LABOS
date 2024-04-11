package ch.heigvd.iict.dma.wifirtt.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import ch.heigvd.iict.dma.wifirtt.config.AccessPointLocation
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.properties.Delegates

class MapOverlayView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyle: Int = 0,
                                               defStyleRes : Int = 0) : View(context, attrs, defStyle, defStyleRes)  {

    private var initialised = false
    private var heightMm by Delegates.notNull<Int>()
    private var widthMm by Delegates.notNull<Int>()

    private var clickedPosition: Pair<Float, Float>? = null

    fun setMapDimension(heightMm : Int, widthMm: Int) {
        this.heightMm = heightMm
        this.widthMm = widthMm
        initialised = true
        invalidate()
    }

    private val accessPointsLocations = mutableMapOf<String, AccessPointLocation>()
    var estimatedPosition : DoubleArray? = null
        set(value) {
            field = value
            invalidate()
        }

    var estimatedDistances : Map<String, Double>? = null
        set(value) {
            field = value
            invalidate()
        }

    var debug = false
        set(value) {
            field = value
            invalidate()
        }

    var circleSize = 15f
        set(value) {
            field = value
            invalidate()
        }

    fun setAccessPoints(accessPointsLocations : Map<String, AccessPointLocation>) {
        this.accessPointsLocations.clear()
        this.accessPointsLocations.putAll(accessPointsLocations)
        invalidate()
    }

    val apPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 5f
    }

    val debugCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    val debugPositionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 1f
        textSize = 24f
    }

    val posPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 5f
    }

    val posHeightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 1f
        textSize = 24f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(debug && event.action == MotionEvent.ACTION_DOWN) {
            clickedPosition = Pair(event.x, event.y)
            Log.d(TAG, "clicked: $clickedPosition")
            invalidate()
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(!initialised) return

        val heightRatio = heightMm.toDouble() / height
        val widthRatio = widthMm.toDouble() / width

        val pixelPerMm: Double
        val xOffsetPixels: Double
        val yOffsetPixels: Double

        if(heightRatio > widthRatio) {
            // the image fill all available height
            pixelPerMm = height.toDouble() / heightMm
            xOffsetPixels = (width - (widthMm.toDouble() * pixelPerMm)) / 2.0
            yOffsetPixels = 0.0
        }
        else {
            // the image fill all available width
            pixelPerMm = width.toDouble() / widthMm
            xOffsetPixels = 0.0
            yOffsetPixels = (height - (heightMm.toDouble() * pixelPerMm)) / 2.0
        }

        //display access points
        for(ap in accessPointsLocations.values) {
            val x = xOffsetPixels + (ap.xMm * pixelPerMm)
            val y = height - (yOffsetPixels + (ap.yMm * pixelPerMm)) // y axis is inverted...
            canvas.drawCircle(x.toFloat(), y.toFloat(),circleSize, apPaint)
        }

        // display distances circles
        if(debug) {
            estimatedDistances?.let { estimatedDistances ->
                for(apAddress in estimatedDistances.keys) {
                    accessPointsLocations[apAddress]?.let {
                        val x = xOffsetPixels + (it.xMm * pixelPerMm)
                        val y = height - (yOffsetPixels + (it.yMm * pixelPerMm)) // y axis is inverted...
                        val dist = pixelPerMm * estimatedDistances[it.macAddress]!!
                        canvas.drawCircle(x.toFloat(), y.toFloat(),dist.toFloat(), debugCirclePaint)
                    }
                }
            }
        }

        // display real position
        if(debug) {
            clickedPosition?.let { clickedPosition ->
                canvas.drawCircle(clickedPosition.first, clickedPosition.second, circleSize, debugPositionPaint)

                // compute distance
                val xRealPosition = (clickedPosition.first  - xOffsetPixels) / pixelPerMm
                val yRealPosition =  ((height - clickedPosition.second) - yOffsetPixels) / pixelPerMm // y axis is inverted...

                Log.d(TAG, "Clicked position: ($xRealPosition, $yRealPosition)")

                estimatedPosition?.let { position ->
                    val distance = round(sqrt((xRealPosition-position[0]).pow(2) + (yRealPosition-position[1]).pow(2)) / 10.0) / 100.0
                    canvas.drawText("$distance m.", clickedPosition.first + (circleSize/2f) + 25f, clickedPosition.second + (circleSize/2f), debugPositionPaint) // on the right
                }

            }

        }

        // display location
        estimatedPosition?.let { position ->
            val x = xOffsetPixels + (position[0] * pixelPerMm)
            val y = height - (yOffsetPixels + (position[1] * pixelPerMm)) // y axis is inverted...
            canvas.drawCircle(x.toFloat(), y.toFloat(),circleSize, posPaint)

            if(debug) {
                //display estimated height
                val heightM = round(position[2] / 10.0)
                canvas.drawText("$heightM cm.", x.toFloat() + (circleSize/2f) + 25f, y.toFloat() + (circleSize/2f), posHeightPaint) // on the right
            }

        }

    }

    companion object {
        private val TAG = MapOverlayView::class.java.simpleName
    }

}