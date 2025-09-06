package com.example.saix_poc

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat

class SitUpTestActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var tvCounter: TextView
    private lateinit var poseOverlay: PoseOverlay
    private lateinit var btnSwitch: Button

    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_situp_test)

        previewView = findViewById(R.id.previewView)
        tvCounter = findViewById(R.id.tvCounter)
        poseOverlay = findViewById(R.id.poseOverlay)
        btnSwitch = findViewById(R.id.btnSwitch)

        // Start camera when activity launches
        startCamera()

        // Switch between front/back cameras
        btnSwitch.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(
                        ContextCompat.getMainExecutor(this),
                        PoseAnalyzer(
                            onRepsDetected = { reps ->
                                runOnUiThread {
                                    tvCounter.text = "Reps: $reps"
                                }
                            },
                            onPoseDetected = { pose ->
                                poseOverlay.setPose(pose)  // 🔹 draw landmarks
                            }
                        )
                    )
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, analyzer)
            } catch (exc: Exception) {
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(this))
    }
}
