package students.aalto.org.indoormappingapp.sensors;

import android.util.Log;

/**
 * Algorithm to detect steps from accelerometer results.
 * Rewrite of Matlab code by Jiang Dong, Aalto University, 2015/05/25.
 */
public class CustomStepDetector {

    public final static long MIN_STEP_TIME = 1000000000;
    public final static float MIN_STEP_ACC = 3;
    public final static int SMOOTH_SIZE = 25;

    float a1 = 0, a2 = 0, a3 = 0;

    int steps = 0;
    long peakTime = 0, lastPeakTime = 0;
    float valleyA = 0, peakA = 0;

    public boolean detect(long timestamp, float[] accelerometer) {
        a1 = a2;
        a2 = a3;
        a3 = lowpass(timestamp, accelerometer[0] * accelerometer[0] + accelerometer[1] * accelerometer[1] + accelerometer[2] * accelerometer[2]);

        // Detect valleys.
        if (a1 > a2 && a2 < a3) {
            valleyA = a2;
        }

        // Detect peaks.
        if (a1 < a2 && a2 > a3) {
            peakA = a2;
            peakTime = timestamp;

            // Detect steps.
            if (lastPeakTime != 0 && valleyA != 0 && peakTime - lastPeakTime > MIN_STEP_TIME && Math.abs(peakA - valleyA) > MIN_STEP_ACC) {
                Log.d("step", "stepped t=" + (peakTime - lastPeakTime) + " d=" + (peakA - valleyA));
                steps += 1;
                lastPeakTime = peakTime;
                return true;
            }
            if (lastPeakTime == 0) {
                lastPeakTime = peakTime;
            }
        }
        return false;
    }


    float a[] = new float[SMOOTH_SIZE];
    float sum = 0;
    int p = 0;


    private float lowpass(long timestamp, float last) {
        p = (p + 1) % SMOOTH_SIZE;

        sum -= a[p];
        a[p] = last;
        sum += a[p];

        // The reference matlab code used "lowess", fitted 1st degree polynomial model.
        // Greatly more efficient moving average produces adequate results.

        return sum / SMOOTH_SIZE;
    }
}
