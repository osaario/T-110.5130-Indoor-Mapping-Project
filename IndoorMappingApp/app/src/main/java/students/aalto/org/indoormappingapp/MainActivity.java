package students.aalto.org.indoormappingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import students.aalto.org.indoormappingapp.deadreckoning.DeadReckoning;
import students.aalto.org.indoormappingapp.model.MapPosition;
import students.aalto.org.indoormappingapp.sensors.SensorsFragment;

public class MainActivity extends AppCompatActivity {

    SurfaceHolder mSurfaceHolder;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SurfaceView surfaceView = (SurfaceView) findViewById(R.id.main_map);
        final TextView xTextView = (TextView) findViewById(R.id.x_text);
        final TextView yTextView = (TextView) findViewById(R.id.y_text);
        final TextView azTextView = (TextView) findViewById(R.id.az_text);
        final Button leftButton = (Button) findViewById(R.id.button_left);
        final Button stepButton = (Button) findViewById(R.id.button_step);
        final Button rightButton = (Button) findViewById(R.id.button_right);

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

        // Create mock step from button.
        Observable<Integer> buttonStepObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(final Subscriber<? super Integer> subscriber) {
                stepButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscriber.onNext(1);
                    }
                });
            }
        });

        SensorsFragment sensors = (SensorsFragment) getSupportFragmentManager().findFragmentById(R.id.sensors_fragment);

        rx.Observable<MapPosition> direction =
                sensors.azimuthObservable.sample(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).map(new Func1<Integer, MapPosition>() {
                    @Override
                    public MapPosition call(Integer azimuth) {
                        double x = Math.cos((double) azimuth * 0.0174532925) * 10;
                        double y = Math.sin((double) azimuth * 0.0174532925) * 10;
                        int xx = (int) x;
                        int yy = (int) y;
                        Log.d("x", x + "");
                        Log.d("y", y + "");
                        Log.d("azimuth", azimuth + "");
                        xTextView.setText("x" + x);
                        yTextView.setText("y" + y);
                        azTextView.setText("az" + azimuth);

                        return new MapPosition(xx, yy, 0);
                    }
                });
        //buttonStepObservable
        sensors.stepObservable.withLatestFrom(direction, new Func2<Integer, MapPosition, MapPosition>() {
            @Override
            public MapPosition call(Integer integer, MapPosition mapPosition) {
                return mapPosition;
            }
        }).scan(new Func2<MapPosition, MapPosition, MapPosition>() {
            @Override
            public MapPosition call(MapPosition mapPosition, MapPosition mapPosition2) {
                if (mapPosition == null) mapPosition = new MapPosition(0, 0, 0);
                return new MapPosition(mapPosition.X + mapPosition2.X, mapPosition.Y + mapPosition2.Y, 0);
            }
        }).scan(new ArrayList<MapPosition>(), new Func2<ArrayList<MapPosition>, MapPosition, ArrayList<MapPosition>>() {
            @Override
            public ArrayList<MapPosition> call(ArrayList<MapPosition> mapPositions, MapPosition integer) {
                mapPositions.add(integer);
                return mapPositions;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ArrayList<MapPosition>>() {
            @Override
            public void call(ArrayList<MapPosition> positions) {
                if (mSurfaceHolder == null) return;

                Canvas canvas = mSurfaceHolder.lockCanvas();
                if (canvas == null) return;
                canvas.drawColor(Color.WHITE);

                int centerX = canvas.getWidth() / 2;
                int centerY = canvas.getHeight() / 2;
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.RED);
                paint.setStrokeWidth(10);


                //location = DeadReckoning.calculatePositionDelta(location.first, location.second, 100, null);
                for (int i = 0; i < positions.size(); i++) {
                    MapPosition start = positions.get(i);
                    MapPosition end = positions.size() > i + 1 ? positions.get(i + 1) : null;
                    if (end != null) {
                        canvas.drawLine(centerX + start.X, centerY + start.Y, centerX + end.X, centerY + end.Y, paint);
                    } else {
                        canvas.drawCircle(centerX + start.X, centerY + start.Y, 10, paint);
                    }
                }
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

                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                        fileUri, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

    }

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
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

}
