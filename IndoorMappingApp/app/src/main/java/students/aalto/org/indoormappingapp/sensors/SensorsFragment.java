package students.aalto.org.indoormappingapp.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;
import students.aalto.org.indoormappingapp.R;

/**
 * Registers to sensors and produces position event streams.
 */
public class SensorsFragment extends Fragment implements SensorEventListener {

    public Observable<SensorsSnapshot> orientationObservable;
    public SensorsCache cache = new SensorsCache(100);
    public List<SensorsSnapshot> path = new ArrayList<>(50);

    private PublishSubject<SensorsSnapshot> orientationSubject = PublishSubject.create();

    boolean publishOrientation = false;
    boolean useAndroidStepSensor = true;
    float stepLength = 1.0f;

    SensorManager sensorManager;
    Sensor stepSensor;
    Sensor rotationSensor;
    Sensor gyroSensor;
    Sensor magneticSensor;
    Sensor accelerationSensor;

    CustomStepDetector customStepDetector;

    public SensorsFragment() {
    }

    public static SensorsFragment newInstance() {
        return new SensorsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orientationObservable = orientationSubject.replay().refCount();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensors, container, false);
    }

    @Override
    public void onDestroyView() {
        stopRecording();
        super.onDestroyView();
    }

    public void configureCache(int size, boolean noCycling) {
        cache = new SensorsCache(size, noCycling);
    }

    public void startRecording(boolean publishOrientation) {
        startRecording(publishOrientation, new float[] {0,0,0});
    }

    public void startRecording(boolean publishOrientation, float[] currentCoordinates) {

        // TODO get useAndroidStepSensor and stepLengh from application settings
        this.publishOrientation = publishOrientation;

        cache.clear();
        path.clear();
        path.add(SensorsSnapshot.initial(currentCoordinates));

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_GAME);

        if (useAndroidStepSensor && stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            customStepDetector = new CustomStepDetector();
        }
    }

    public void stopRecording() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    float[] gyroscope = null;
    float[] magnetic = null;
    float[] accelerometer = null;

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ROTATION_VECTOR) {

            SensorsSnapshot readings = new SensorsSnapshot(event.timestamp, orientationFromRotation(event.values));
            readings.Gyroscope = gyroscope;
            readings.Magnetic = magnetic;
            readings.Accelerometer = accelerometer;
            if (publishOrientation) {
                orientationSubject.onNext(readings);
            }
            cache.add(readings);

        } else if (type == Sensor.TYPE_GYROSCOPE) {
            gyroscope = event.values;
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetic = event.values;
        } else if (type == Sensor.TYPE_ACCELEROMETER) {
            accelerometer = event.values;

            if ((!useAndroidStepSensor || stepSensor == null) && customStepDetector.detect(event.timestamp, event.values)) {
                step(cache.search(event.timestamp - customStepDetector.MIN_STEP_TIME / 2));
            }

        } else if (type == Sensor.TYPE_STEP_DETECTOR) {

            step(cache.search(event.timestamp));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private float[] orientationFromRotation(float[] rotationValues) {
        float R[] = new float[16];
        SensorManager.getRotationMatrixFromVector(R, rotationValues);
        float orientation[] = {0, 0, 0};
        SensorManager.getOrientation(R, orientation);
        return orientation;
    }

    private float[] orientationFromAccelerometerAndMagnetic(float[] accelerometerValues, float[] magneticValues) {
        float R[] = new float[16];
        float I[] = new float[16];
        float orientation[] = {0, 0, 0};
        boolean success = SensorManager.getRotationMatrix(R, I, accelerometerValues, magneticValues);
        if (success) {
            SensorManager.getOrientation(R, orientation);
        }
        return orientation;
    }

    private void step(SensorsSnapshot readings) {
        /*
        if (readings == null) {
            Log.e("sensors", "Sensor readings missing for step");
            return;
        }
        if (path.size() == 0) {
            Log.e("sensors", "No path start set");
        }
        SensorsSnapshot before = path.get(path.size() - 1);
        readings.Coordinate = new float[]{
                before.Coordinate[0] + stepLength * (float) Math.cos(readings.azimuth()),
                before.Coordinate[1] + stepLength * (float) Math.sin(readings.azimuth()),
                before.Coordinate[2]
        };
        path.add(readings);
        */
    }

}
