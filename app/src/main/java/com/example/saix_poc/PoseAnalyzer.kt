package com.example.saix_poc

import android.annotation.SuppressLint
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions   // ✅ correct import

class PoseAnalyzer(
    private val onRepsDetected: (Int) -> Unit,
    private val onPoseDetected: (Pose) -> Unit   // 🔹 callback for overlay
) : ImageAnalysis.Analyzer {

    // ✅ ML Kit Pose Detector (streaming mode)
    private val poseDetector = PoseDetection.getClient(
        PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
    )

    private var repCount = 0
    private var lastState = false

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage: Image? = imageProxy.image
        if (mediaImage != null) {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            poseDetector.process(inputImage)
                .addOnSuccessListener { pose: Pose ->
                    detectSitUp(pose)    // count sit-ups
                    onPoseDetected(pose) // send pose to overlay
                }
                .addOnFailureListener { e ->
                    Log.e("PoseAnalyzer", "Pose detection failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun detectSitUp(pose: Pose) {
        val nose = pose.getPoseLandmark(PoseLandmark.NOSE)

        if (nose != null) {
            val currentState = nose.position.y < 400  // tweak threshold
            if (currentState && !lastState) {
                repCount++
                onRepsDetected(repCount)
            }
            lastState = currentState
        }
    }
}
