package students.aalto.org.indoormappingapp.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

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

    private rx.Observable<float[]> stepObservable;
    public rx.Observable<Integer> stepWithDirectionObservable;

    private PublishSubject<float[]> stepSubject = PublishSubject.create();

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

        stepWithDirectionObservable = stepSubject.filter(new Func1<float[], Boolean>() {
            @Override
            public Boolean call(float[] floats) {
                if(floats == null) {
                    Log.d("","no directio at the time");
                }
                return floats != null;
            }
        }).map(new Func1<float[], Integer>() {
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
        }).replay().refCount();

        return inflater.inflate(R.layout.fragment_sensors, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sensorManager.unregisterListener(this);
    }

    HashMap<Long, float[]> directionAtTime = new HashMap<>();

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_STEP_DETECTOR)
        {
            Long secondTimeStamp = (Long)(event.timestamp / 1000000000);
            float[] direction = directionAtTime.get(secondTimeStamp);
            stepSubject.onNext(direction);
        }
        if (type == Sensor.TYPE_GAME_ROTATION_VECTOR || type == Sensor.TYPE_ROTATION_VECTOR)
        {
            Long secondTimeStamp = (Long)(event.timestamp / 1000000000);
            if (!directionAtTime.containsKey(secondTimeStamp)) {
                directionAtTime.put(secondTimeStamp, event.values);
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
