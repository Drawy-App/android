package ru.landyrev.howtodraw.util;

import android.graphics.Bitmap;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import io.fotoapparat.preview.Frame;

public class Graphics {

    static public Bitmap bitmapFromGray(byte[] data, int width, int height) {
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        image.copyPixelsFromBuffer(ByteBuffer.wrap(data));
        return image;
    }

    static public byte[] frameToGrey(Frame frame) {
        byte[] srcImage = frame.getImage();
        int pixelsSize = srcImage.length * 8 / 12;
        byte[] dstImage = new byte[pixelsSize];

        System.arraycopy(srcImage, 0, dstImage, 0, pixelsSize);

        return dstImage;
    }

}
