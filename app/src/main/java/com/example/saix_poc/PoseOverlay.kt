package com.example.saix_poc

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

class PoseOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var pose: Pose? = null
    private val paintLine = Paint().apply {
        color = Color.CYAN
        strokeWidth = 6f
        style = Paint.Style.STROKE
        isAntiAlias = true
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f) // dashed cyber lines
    }
    private val paintPoint = Paint().apply {
        color = Color.MAGENTA
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val glowPaint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = 12f
        maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.OUTER)
    }

    fun setPose(pose: Pose?) {
        this.pose = pose
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val pose = this.pose ?: return

        // Draw all connections
        drawConnection(canvas, pose, PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER)
        drawConnection(canvas, pose, PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW)
        drawConnection(canvas, pose, PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST)
        drawConnection(canvas, pose, PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW)
        drawConnection(canvas, pose, PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST)

        drawConnection(canvas, pose, PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP)
        drawConnection(canvas, pose, PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP)
        drawConnection(canvas, pose, PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP)

        drawConnection(canvas, pose, PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE)
        drawConnection(canvas, pose, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE)
        drawConnection(canvas, pose, PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE)
        drawConnection(canvas, pose, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE)

        // Draw points
        for (landmark in pose.allPoseLandmarks) {
            val cx = translateX(landmark.position.x)
            val cy = translateY(landmark.position.y)
            canvas.drawCircle(cx, cy, 12f, glowPaint) // glowing halo
            canvas.drawCircle(cx, cy, 8f, paintPoint) // solid point
        }
    }

    private fun drawConnection(canvas: Canvas, pose: Pose, startLandmarkType: Int, endLandmarkType: Int) {
        val start = pose.getPoseLandmark(startLandmarkType)
        val end = pose.getPoseLandmark(endLandmarkType)

        if (start != null && end != null) {
            val startX = translateX(start.position.x)
            val startY = translateY(start.position.y)
            val endX = translateX(end.position.x)
            val endY = translateY(end.position.y)

            canvas.drawLine(startX, startY, endX, endY, glowPaint) // glowing outer
            canvas.drawLine(startX, startY, endX, endY, paintLine) // dashed cyber line
        }
    }

    // Adjust coordinates if needed (mirroring/scaling)
    private fun translateX(x: Float): Float = x
    private fun translateY(y: Float): Float = y
}
