package ru.landyrev.howtodraw.util

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.preview.Frame
import io.fotoapparat.selector.back
import io.fotoapparat.view.CameraView
import ru.landyrev.howtodraw.data.Level
import ru.landyrev.howtodraw.data.LevelsData

/**
 * Created by landyrev on 24.03.2018.
 */

class Camera(context: Context, cameraView: CameraView, private val level: Level) {
    private val fotoapparat: Fotoapparat
    private var firstRecognizeTime = 0L
    private var recognizeCount = 0
    private var isSuccess = false

    private val classify = Classify.create(context.assets,
            "graph.pb",
            "labels.txt",
            224, 128, 128.0f,
            "Placeholder", "final_result"
    )

    var onCapture: (() -> Unit)? = null
    var onSuccess: (() -> Unit)? = null
    var onLost: (() -> Unit)? = null

    init {
        val configuration = CameraConfiguration(
                frameProcessor = { frame -> frameProcessor(frame) }
        )

        fotoapparat = Fotoapparat(
                context,
                cameraConfiguration = configuration,
                view = cameraView,
                scaleType = ScaleType.CenterCrop,
                lensPosition = back()
        )
    }

    fun onCaptureLabelLabel(label: String) {
        Log.w("CAPTURE", label)
        val nowTime = SystemClock.uptimeMillis()
        if (label == level.name) {
            if (onCapture != null) { onCapture!!() }
            recognizeCount++
            if (!isSuccess) {
                firstRecognizeTime = SystemClock.uptimeMillis()
                isSuccess = true
            }
            if (nowTime - firstRecognizeTime > 1000 * 2) {
                if (recognizeCount >= 3) {
                    if (onSuccess != null) { onSuccess!!() }
                }
            }
        } else {
            if (isSuccess) {
                isSuccess = false
                recognizeCount = 0
                if (onLost != null) { onLost!!() }
            }
        }
    }

    fun start() {
        fotoapparat.start()
    }

    fun stop() {
        fotoapparat.stop()

    }

    private fun frameProcessor(frame: Frame) {
        val greyImage = Graphic.frameToGray(frame)
        val realImage = Graphic.bitmapFromGray(greyImage, frame.size.width, frame.size.height)
        val result = classify.recognizeImage(realImage)
        if (result.size > 0) {
            onCaptureLabelLabel(result[0].title.replace(" ", "_"))
        }
    }



}