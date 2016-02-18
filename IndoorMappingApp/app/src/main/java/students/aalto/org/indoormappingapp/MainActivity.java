package students.aalto.org.indoormappingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import students.aalto.org.indoormappingapp.deadreckoning.DeadReckoning;

public class MainActivity extends AppCompatActivity {

    SurfaceHolder mSurfaceHolder;
    Pair<Integer, Integer> location;
    SensorManager smm;
    List<Sensor> sensor;
    Spinner lv;
    String[] data = {"one", "two", "three", "four", "five"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SurfaceView surfaceView = (SurfaceView) findViewById(R.id.main_map);
        smm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lv = (Spinner) findViewById(R.id.spinner1);
        sensor = smm.getSensorList(Sensor.TYPE_ALL);
        lv.setAdapter(new ArrayAdapter<Sensor>(this, android.R.layout.simple_list_item_1,  sensor));

        mSurfaceHolder = surfaceView.getHolder();

        rx.Observable<SurfaceHolder> surfaceObservable = rx.Observable.create(new rx.Observable.OnSubscribe<SurfaceHolder>() {
            @Override
            public void call(final Subscriber<? super SurfaceHolder> subscriber) {
                mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder surfaceHolder) {
                        Canvas canvas = surfaceHolder.lockCanvas();
                        location = new Pair<Integer, Integer>(canvas.getWidth() / 2, canvas.getHeight() / 2);
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        subscriber.onNext(surfaceHolder);
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                        mSurfaceHolder = null;
                    }
                });
            }
        });
        
        rx.Observable<Long> pulseObs = rx.Observable.interval(100, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread());

        rx.Observable.combineLatest(surfaceObservable, pulseObs, new Func2<SurfaceHolder, Long, Pair<SurfaceHolder, Long>>() {
            @Override
            public Pair<SurfaceHolder, Long> call(SurfaceHolder surfaceHolder, Long integer) {
                return new Pair<SurfaceHolder, Long>(surfaceHolder, integer);
            }
        }).subscribe(new Action1<Pair<SurfaceHolder, Long>>() {
            @Override
            public void call(Pair<SurfaceHolder, Long> surfaceHolderLongPair) {
                if(mSurfaceHolder == null) return;
                Canvas canvas = mSurfaceHolder.lockCanvas();
                canvas.drawColor(Color.WHITE);

                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.RED);

                location = DeadReckoning.calculatePositionDelta(location.first, location.second, 100, null);
                canvas.drawCircle(location.first, location.second, 80, paint);
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        });

        /*final TextView helloView = (TextView) findViewById(R.id.hello_text_view);
        rx.Observable.interval(500, TimeUnit.MILLISECONDS).map(new Func1<Long, Long>() {
            @Override
            public Long call(Long aLong) {
                return aLong * 100;
            }
        }).filter(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long aLong) {
                return aLong > 300;
            }
        }).take(1).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                helloView.setText("Start load");
            }
        }).flatMap(new Func1<Long, rx.Observable<Long>>() {
            @Override
            public rx.Observable<Long> call(Long aLong) {
                return rx.Observable.interval(5000, 0, TimeUnit.MILLISECONDS).take(1);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                helloView.setText(aLong + "");
            }
        });*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_map) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
        */
}
