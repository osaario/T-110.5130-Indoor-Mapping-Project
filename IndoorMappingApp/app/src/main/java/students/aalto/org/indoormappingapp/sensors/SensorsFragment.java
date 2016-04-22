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

import java.util.HashMap;

import rx.functions.Func1;
import rx.subjects.PublishSubject;
import students.aalto.org.indoormappingapp.R;

/**
 * Registers to sensors and produces position event streams.
 */
public class SensorsFragment extends Fragment implements SensorEventListener {

    public rx.Observable<SensorsSnapshot> orientationObservable;
    public rx.Observable<SensorsSnapshot> stepObservable;

    private PublishSubject<SensorsSnapshot> orientationSubject = PublishSubject.create();
    private PublishSubject<SensorsSnapshot> stepSubject = PublishSubject.create();

    boolean useAndroidStepSensor = true;
    float stepLength = 10;

    SensorManager sensorManager;
    Sensor stepSensor;
    Sensor rotationSensor; // TODO decide if acc+mag is better
    Sensor magneticSensor;
    Sensor accelerationSensor;

    public SensorsFragment() {
    }

    public static SensorsFragment newInstance() {
        SensorsFragment fragment = new SensorsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // TODO Get useAndroidStepSensor and stepLength from settings (singleton)

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        //rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        //sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_GAME);

        if (useAndroidStepSensor && stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        orientationObservable = orientationSubject.replay().refCount();

        stepObservable = stepSubject.filter(new Func1<SensorsSnapshot, Boolean>() {
            @Override
            public Boolean call(SensorsSnapshot sensor) {
                if (sensor == null) {
                    Log.d("sensors", "Sensor readings missing for step");
                    return false;
                }
                return true;
            }
        }).replay().refCount();

        return inflater.inflate(R.layout.fragment_sensors, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sensorManager.unregisterListener(this);
    }

    SensorsCache readings = new SensorsCache(100);
    float[] magnetic = null;

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ROTATION_VECTOR) {

            //TODO replace with acc+mag

        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {

            magnetic = event.values;

        } else if (type == Sensor.TYPE_ACCELEROMETER && magnetic != null) {

            SensorsSnapshot sensors = new SensorsSnapshot(event.timestamp, magnetic, event.values);
            sensors.Orientation = orientationFromAccelerometerAndMagnetic(sensors.Accelerometer, sensors.Magnetic);
            sensors.Azimut = (int) Math.round(Math.toDegrees(sensors.Orientation[0]));
            orientationSubject.onNext(sensors);
            readings.add(sensors);

        } else if (type == Sensor.TYPE_STEP_DETECTOR) {

            stepSubject.onNext(readings.search(event.timestamp));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private float[] orientationFromRotation(float[] rotationValues) {
        float R[] = new float[16];
        SensorManager.getRotationMatrixFromVector(R, rotationValues);
        float orientation[] = new float[3];
        SensorManager.getOrientation(R, orientation);
        return orientation;
    }

    private float[] orientationFromAccelerometerAndMagnetic(float[] accelerometerValues, float[] magneticValues) {
        float R[] = new float[9];
        float I[] = new float[9];
        float orientation[] = {0, 0, 0};
        boolean success = SensorManager.getRotationMatrix(R, I, accelerometerValues, magneticValues);
        if (success) {
            SensorManager.getOrientation(R, orientation);
        }
        return orientation;
    }

}
