package ru.landyrev.howtodraw.util

import android.graphics.*
import io.fotoapparat.preview.Frame
import java.nio.ByteBuffer

/**
 * Created by landyrev on 29.03.2018.
 */

object Graphic {

    fun bitmapFromGray(gray: ByteArray, width: Int, height: Int): Bitmap {
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)

        image.copyPixelsFromBuffer(ByteBuffer.wrap(gray))
        return rotate(square(image, 224))
    }

    fun frameToGray(frame: Frame): ByteArray {
        return frame.image.copyOf(frame.image.size * 8 / 12)
    }

    fun square(srcImage: Bitmap, size: Int): Bitmap {
        val ratio = srcImage.width.toFloat() / srcImage.height.toFloat()
        var x: Int
        var y: Int
        var dstWidth: Int
        var dstHeight: Int

        if (srcImage.width >= srcImage.height) {
            dstHeight = size
            dstWidth = (dstHeight * ratio).toInt()
            x = dstWidth / 2 - dstHeight / 2
            y = 0

        } else {
            dstHeight = (size / ratio).toInt()
            dstWidth = size
            x = 0
            y = dstHeight / 2 - dstWidth / 2
        }
        val dstImage = Bitmap.createScaledBitmap(
                srcImage, dstWidth, dstHeight, true
        )
        return Bitmap.createBitmap(
                dstImage,
                x, y, size, size
        )
    }

    fun rotate(srcImage: Bitmap): Bitmap {
        val m = Matrix()
        m.postRotate(90f)
        return Bitmap.createBitmap(srcImage, 0, 0, srcImage.width, srcImage.height, m, true)
    }

//    fun rotateCW(mat: array) {
//
//    }
}