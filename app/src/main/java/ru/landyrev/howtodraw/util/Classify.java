package ru.landyrev.howtodraw.util;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.tensorflow.lite.Interpreter;

public abstract class Classify {

    private static final String MODEL_FILE_NAME = "MyNet.lite";
    private static final String LABEL_PATH = "labels.txt";
    private static final String TAG = "HTD_CLASSIFY";
    private ByteBuffer charValues = ByteBuffer.allocate(DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y);

    private static final int IMAGE_MEAN = 256;
    private static final float IMAGE_STD = 256.0f;

    private static final int DIM_PIXEL_SIZE = 1;

    private static final int DIM_IMG_SIZE_X = 224;
    private static final int DIM_IMG_SIZE_Y = 224;

    abstract void onRecognize(String label);

    private Interpreter tflite;
    private List<String> labelList;
    private ByteBuffer imgData;
    private float[][] labelProbArray;

    public Classify(Activity activity) throws IOException {
        tflite = new Interpreter(loadModelFile(activity));
        labelList = loadLabelList(activity);
        imgData = ByteBuffer.allocateDirect(DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE * 4);
        imgData.order(ByteOrder.nativeOrder());
        labelProbArray = new float[1][labelList.size()];

    }


    private void timer(String s) {
        Long secs = (SystemClock.uptimeMillis());
        Log.w(TAG, secs.toString() + ": " + s);
    }

    public void classify(Bitmap image) {
        convertBitmapToByteBuffer(image);
        tflite.run(imgData, labelProbArray);

        int label = 0;
        float maxValue = 0;
        for (int i = 0; i < labelProbArray[0].length; i++) {
            if (labelProbArray[0][i] > maxValue) {
                label = i;
                maxValue = labelProbArray[0][i];
            }
        }
        String textLabel = labelList.get(label).replace(" ", "_");
        onRecognize(textLabel);
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        charValues.rewind();
        bitmap.copyPixelsToBuffer(charValues);

        byte[] pixels = charValues.array();

        for (int i=0; i < pixels.length; i++) {
            final float value = ((pixels[i] & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
            imgData.putFloat(i * 4, value);
        }
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE_NAME);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();

        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadLabelList(Activity activity) throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(activity.getAssets().open(LABEL_PATH)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }
}
