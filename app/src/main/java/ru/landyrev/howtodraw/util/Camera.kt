package ru.landyrev.howtodraw.util

import android.app.Activity
import android.content.Context
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

    inner class Classifier(activity: Activity): Classify(activity) {
        override fun onRecognize(label: String?) {
            onRecognizeLabel(label!!)
        }

    }
    private val classify = Classifier(context as Activity)


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

    fun onRecognizeLabel(label: String) {
        Log.w("CAPTURE", label)
        val nowTime = SystemClock.uptimeMillis()
        if (label == level.name) {
            recognizeCount++
            if (!isSuccess) {
                firstRecognizeTime = SystemClock.uptimeMillis()
                isSuccess = true
            }
            if (nowTime - firstRecognizeTime > 60 * 3) {
                if (recognizeCount >= 3) {
                    Log.w("URA", "SUCCESS")
                }
            }
        } else {
            if (isSuccess) {
                isSuccess = false
                recognizeCount = 0
                Log.w("LOST", "LOST")
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
        classify.classify(realImage)
    }



}