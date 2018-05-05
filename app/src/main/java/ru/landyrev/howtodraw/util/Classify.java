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

public class Classify {

    private static final String MODEL_FILE_NAME = "MyNet.lite";
    private static final String LABEL_PATH = "labels.txt";
    private static final String TAG = "HTD_CLASSIFY";
    private ByteBuffer charValues = ByteBuffer.allocate(DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y);

    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    private static final int DIM_BATCH_SIZE = 1;

    private static final int DIM_PIXEL_SIZE = 1;

    static final int DIM_IMG_SIZE_X = 224;
    static final int DIM_IMG_SIZE_Y = 224;

    Interpreter tflite;
    private List<String> labelList;
    private ByteBuffer imgData = null;
    private float[][] labelProbArray = null;

    public Classify(Activity activity) throws IOException {
        tflite = new Interpreter(loadModelFile(activity));
        labelList = loadLabelList(activity);
        imgData = ByteBuffer.allocateDirect(DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE * 4);
        imgData.order(ByteOrder.nativeOrder());
        labelProbArray = new float[1][labelList.size()];
    }

    public void timer(String s) {
        Long secs = (SystemClock.uptimeMillis());
        Log.w(TAG, secs.toString() + ": " + s);
    }

    public void classify(Bitmap image) {
        timer("Start classify");
        convertBitmapToByteBuffer(image);
        timer("Converted to buffer");
        tflite.run(imgData, labelProbArray);
        timer("Labeled");

        int label = 0;
        float maxValue = 0;
        for (int i = 0; i < labelProbArray[0].length; i++) {
            if (labelProbArray[0][i] > maxValue) {
                label = i;
                maxValue = labelProbArray[0][i];
            }
        }


        timer(labelList.get(label));
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        charValues.rewind();
        bitmap.copyPixelsToBuffer(charValues);

        byte[] test = charValues.array();

        byte test1 = (byte) (test[2] & 0xFF);
        int test2 = (test[2] & 0xFF);

        for (int i=0; i < test.length; i++) {
            final float value = ((int) (test[i] & 0xFF) - 128) / 128.0f;
            imgData.putFloat(i * 4, value);
        }
//        bitmap.copyPixelsToBuffer(imgData);
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
