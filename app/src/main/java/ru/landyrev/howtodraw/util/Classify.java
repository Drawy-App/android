package ru.landyrev.howtodraw.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Trace;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;
import org.tensorflow.Operation;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

/** A classifier specialized to label images using TensorFlow. */
public class Classify implements Classifier {
    private static final String TAG = "ImageClassify";

    // Only return this many results with at least this confidence.
    private static final int MAX_RESULTS = 3;
    private static final float THRESHOLD = 0.10f;

    // Config values.
    private String inputName;
    private String outputName;
    private int inputSize;
    private int imageMean;
    private float imageStd;

    // Pre-allocated buffers.
    private final Vector<String> labels = new Vector<>();
    private float[] floatValues;
    private float[] outputs;
    private String[] outputNames;
    private ByteBuffer tmpBuffer;

    private boolean logStats = false;

    private TensorFlowInferenceInterface inferenceInterface;

    private Classify() {}

    /**
     * Initializes a native TensorFlow session for classifying images.
     *
     * @param assetManager The asset manager to be used to load assets.
     * @param modelFilename The filepath of the model GraphDef protocol buffer.
     * @param labelFilename The filepath of label file for classes.
     * @param inputSize The input size. A square image of inputSize x inputSize is assumed.
     * @param imageMean The assumed mean of the image values.
     * @param imageStd The assumed std of the image values.
     * @param inputName The label of the image input node.
     * @param outputName The label of the output node.
     * @throws IOException
     */
    public static Classifier create(
            AssetManager assetManager,
            String modelFilename,
            String labelFilename,
            int inputSize,
            int imageMean,
            float imageStd,
            String inputName,
            String outputName) {
        Classify c = new Classify();
        c.inputName = inputName;
        c.outputName = outputName;

        // Read the label names into memory.
        Log.i(TAG, "Reading labels from: " + labelFilename);
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(assetManager.open(labelFilename)));
//            br = new BufferedReader(assetManager.open(actualFilename))
            String line;
            while ((line = br.readLine()) != null) {
                c.labels.add(line);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException("Problem reading label file!" , e);
        }

        try {
            InputStream modelStream = assetManager.open(modelFilename);
            int available = modelStream.available();
            c.inferenceInterface = new TensorFlowInferenceInterface(modelStream);
        } catch (IOException e) {
            throw new RuntimeException("Something bad happened", e);
        }

        // The shape of the output is [N, NUM_CLASSES], where N is the batch size.
        final Operation operation = c.inferenceInterface.graphOperation(outputName);
        final int numClasses = (int) operation.output(0).shape().size(1);
        Log.i(TAG, "Read " + c.labels.size() + " labels, output layer size is " + numClasses);

        // Ideally, inputSize could have been retrieved from the shape of the input operation.  Alas,
        // the placeholder node for input in the graphdef typically used does not specify a shape, so it
        // must be passed in as a parameter.
        c.inputSize = inputSize;
        c.imageMean = imageMean;
        c.imageStd = imageStd;

        // Pre-allocate buffers.
        c.outputNames = new String[] {outputName};
        c.floatValues = new float[inputSize * inputSize * 3];
        c.outputs = new float[numClasses];
        c.tmpBuffer = ByteBuffer.allocate(inputSize * inputSize);

        return c;
    }

    @Override
    public List<Recognition> recognizeImage(final Bitmap bitmap) {
        // Log this method so that it can be analyzed with systrace.
        Trace.beginSection("recognizeImage");

        Trace.beginSection("preprocessBitmap");

        tmpBuffer.rewind();

        bitmap.copyPixelsToBuffer(tmpBuffer);
        for (int i = 0; i < (inputSize * inputSize); i++) {
            float value = ((tmpBuffer.get(i) & 0xFF) - imageMean) / imageStd;
            floatValues[i * 3] = value;
            floatValues[i * 3 + 1] = value;
            floatValues[i * 3 + 2] = value;
        }
        Trace.endSection();

        // Copy the input data into TensorFlow.
        Trace.beginSection("feed");
        inferenceInterface.feed(inputName, floatValues, 1, inputSize, inputSize, 3);
        Trace.endSection();

        // Run the inference call.
        Trace.beginSection("run");
        inferenceInterface.run(outputNames, logStats);
        Trace.endSection();

        // Copy the output Tensor back into the output array.
        Trace.beginSection("fetch");
        inferenceInterface.fetch(outputName, outputs);
        Trace.endSection();

        // Find the best classifications.
        PriorityQueue<Recognition> pq =
                new PriorityQueue<>(
                        3,
                        new Comparator<Recognition>() {
                            @Override
                            public int compare(Recognition lhs, Recognition rhs) {
                                // Intentionally reversed to put high confidence at the head of the queue.
                                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                            }
                        });
        for (int i = 0; i < outputs.length; ++i) {
            if (outputs[i] > THRESHOLD) {
                Log.w(TAG, "passed!");
                Recognition res = new Recognition(
                        "" + i, labels.size() > i ? labels.get(i) : "unknown", outputs[i], null);
                Log.w(TAG, res.getTitle());
                pq.add(res);

            }
        }
        final ArrayList<Recognition> recognitions = new ArrayList<Recognition>();
        int recognitionsSize = Math.min(pq.size(), MAX_RESULTS);
        for (int i = 0; i < recognitionsSize; ++i) {
            recognitions.add(pq.poll());
        }
        Trace.endSection(); // "recognizeImage"
        return recognitions;
    }

    @Override
    public void enableStatLogging(boolean logStats) {
        this.logStats = logStats;
    }

    @Override
    public String getStatString() {
        return inferenceInterface.getStatString();
    }

    @Override
    public void close() {
        inferenceInterface.close();
    }
}