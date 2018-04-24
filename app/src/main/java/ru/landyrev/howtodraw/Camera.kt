package ru.landyrev.howtodraw

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.widget.ImageView
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.preview.Frame
import io.fotoapparat.selector.back
import io.fotoapparat.view.CameraView
import ru.landyrev.howtodraw.util.Classify
import ru.landyrev.howtodraw.util.Graphic

/**
 * Created by landyrev on 24.03.2018.
 */

class Camera(context: Context, cameraView: CameraView, previewImage: ImageView) {
    val mainHandler = Handler(context.mainLooper)
    val fotoapparat: Fotoapparat
    val imageView = previewImage
    val classify = Classify(context as Activity)


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

    fun start() {
        fotoapparat.start()
    }

    fun stop() {
        fotoapparat.stop()
    }

    private fun frameProcessor(frame: Frame) {
        classify.timer("frame captured")
        val greyImage = Graphic.frameToGray(frame)
        classify.timer("frame grayed")
        val realImage = Graphic.bitmapFromGray(greyImage, frame.size.width, frame.size.height)
        classify.timer("gray bitmapped")
        classify.classify(realImage)

//        mainHandler.post {
//            imageView.setImageBitmap(realImage)
//        }
    }



}