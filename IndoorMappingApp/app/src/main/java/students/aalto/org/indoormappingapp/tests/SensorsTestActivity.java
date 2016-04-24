package students.aalto.org.indoormappingapp.tests;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import students.aalto.org.indoormappingapp.MenuRouterActivity;
import students.aalto.org.indoormappingapp.R;
import students.aalto.org.indoormappingapp.sensors.SensorsFragment;
import students.aalto.org.indoormappingapp.sensors.SensorsSnapshot;

public class SensorsTestActivity extends MenuRouterActivity {

    SurfaceHolder mSurfaceHolder;
    SensorsFragment sensors;

    float zoom = 20, translate[] = {0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sensors);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SurfaceView surfaceView = (SurfaceView) findViewById(R.id.main_map);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Canvas canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.WHITE);
                surfaceHolder.unlockCanvasAndPost(canvas);
                mSurfaceHolder = surfaceHolder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                mSurfaceHolder = surfaceHolder;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                mSurfaceHolder = null;
            }
        });

        sensors = (SensorsFragment) getSupportFragmentManager().findFragmentById(R.id.sensors_fragment);
        sensors.startFrom(new float[]{0, 0, 0});

        sensors.orientationObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<SensorsSnapshot>() {
            @Override
            public void call(SensorsSnapshot readings) {
                if (mSurfaceHolder == null) return;

                Canvas canvas = mSurfaceHolder.lockCanvas();
                if (canvas == null) return;

                canvas.drawColor(Color.WHITE);

                float origo[] = {canvas.getWidth() / 2, canvas.getHeight() / 2};
                List<SensorsSnapshot> path = sensors.path;

                // Walk path.
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.GRAY);
                paint.setStrokeWidth(10);
                for (int i = 0; i < path.size() - 1; i++) {
                    float[] beg = screenPoint(origo, path.get(i).Coordinates);
                    float[] end = screenPoint(origo, path.get(i+1).Coordinates);
                    canvas.drawLine(beg[0], beg[1], end[0], end[1], paint);
                }

                // Current orientation.
                paint.setColor(Color.RED);
                float r = 30, d = 3 * r / 2;
                float[] pos = screenPoint(origo, path.get(path.size() - 1).Coordinates);
                canvas.drawCircle(pos[0], pos[1], r, paint);
                canvas.drawCircle(
                        pos[0] + d * (float) Math.cos(readings.azimuth()),
                        pos[1] - d * (float) Math.sin(readings.azimuth()),
                        r, paint
                );

                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        });
    }

    private float[] screenPoint(float[] origo, float[] xyz) {
        return new float[]{
                origo[0] + zoom * xyz[0] - translate[0],
                origo[1] - zoom * xyz[1] + translate[1]
        };
    }

}
