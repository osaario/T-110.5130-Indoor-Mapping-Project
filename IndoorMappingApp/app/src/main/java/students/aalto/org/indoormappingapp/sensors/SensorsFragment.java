package students.aalto.org.indoormappingapp.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;
import students.aalto.org.indoormappingapp.R;

/**
 * Registers to sensors and produces event streams.
 */
public class SensorsFragment extends Fragment implements SensorEventListener {

    SensorManager sensorManager;
    private Sensor stepSensor;
    private Sensor rotationSensor;
    private Sensor magneticSensor;
    private Sensor accelerationSensor;

    public rx.Observable<Integer> stepObservable;
    public rx.Observable<Integer> azimuthObservable;

    private PublishSubject<Integer> stepSubject = PublishSubject.create();
    private PublishSubject<float[]> accelerometerSubject = PublishSubject.create();
    private PublishSubject<float[]> magneticSubject = PublishSubject.create();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (rotationSensor == null) {
            rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }

        stepObservable = stepSubject.asObservable();
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_GAME);
        //sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
        boolean onko = sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_GAME);

        azimuthObservable = Observable.combineLatest(magneticSubject, accelerometerSubject, new Func2<float[], float[], Integer>() {
            @Override
            public Integer call(float[] magnetic, float[] accelerometer) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, magnetic,
                        accelerometer);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    Integer azimut = (int) Math.round(Math.toDegrees(orientation[0]));
                    return azimut;
                } else {
                    return null;
                }
            }
        });

        return inflater.inflate(R.layout.fragment_sensors, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR)
        {
            stepSubject.onNext(1);
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            accelerometerSubject.onNext(event.values);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            magneticSubject.onNext(event.values);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
