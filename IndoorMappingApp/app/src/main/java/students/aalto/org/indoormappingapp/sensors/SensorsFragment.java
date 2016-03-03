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
import rx.functions.Func1;
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
    private PublishSubject<float[]> rotationSubject = PublishSubject.create();

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
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
        boolean onko = sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_GAME);

        /*azimuthObservable = Observable.combineLatest(magneticSubject, accelerometerSubject, new Func2<float[], float[], Integer>() {
            @Override
            public Integer call(float[] magnetic, float[] accelerometer) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, magnetic,
                        accelerometer);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    Integer azimut = (int) Math.round(Math.toDegrees(orientation[1]));
                    //Log.d("test", "orientation 0:" + orientation[0] + " 1:" + orientation[1] + " 2:" + orientation[2]);
                    return azimut;
                } else {
                    return null;
                }
            }
        });*/

        azimuthObservable = rotationSubject.map(new Func1<float[], Integer>() {
            @Override
            public Integer call(float[] floats) {
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, floats);
                float[] orientation = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientation);
                Integer azimut = (int) Math.round(Math.toDegrees(orientation[0]));
                //Log.d("test", "rotation 0:" + Math.toDegrees(orientation[0]) + " 1:" + Math.toDegrees(orientation[1]) + " 2:" + Math.toDegrees(orientation[2]));
                return azimut;
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
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_STEP_DETECTOR)
        {
            stepSubject.onNext(1);
        }
        if (type == Sensor.TYPE_ACCELEROMETER)
        {
            accelerometerSubject.onNext(event.values);
        }
        if (type == Sensor.TYPE_MAGNETIC_FIELD)
        {
            magneticSubject.onNext(event.values);
        }
        if (type == Sensor.TYPE_GAME_ROTATION_VECTOR || type == Sensor.TYPE_ROTATION_VECTOR)
        {
            rotationSubject.onNext(event.values);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
