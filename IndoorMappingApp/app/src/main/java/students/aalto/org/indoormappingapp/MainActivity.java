package students.aalto.org.indoormappingapp;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import students.aalto.org.indoormappingapp.deadreckoning.DeadReckoning;
import students.aalto.org.indoormappingapp.model.MapPosition;
import students.aalto.org.indoormappingapp.sensors.SensorsFragment;

public class MainActivity extends AppCompatActivity {

    final int gridSize = 100;
    final int gridStep = 80;
    SurfaceHolder mSurfaceHolder;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;


    private void drawGrid(Canvas canvas, int x_off, int y_off) {
        //pixels
        final int startX = -1000;
        final int startY = -1000;
        final int endY = 3000;

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(2);
        for(int i = 0; i < gridSize; i++) {
            canvas.drawLine(startX + gridStep * i + x_off, startY + y_off, startX + gridStep * i + x_off, endY + y_off, paint);
        }
        for(int i = 0; i < gridSize; i++) {
            canvas.drawLine(startX + x_off, startY + gridStep * i + y_off, endY + x_off, startY + gridStep * i + y_off, paint);
        }
    }

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
        final TextView stopText = (TextView) findViewById(R.id.stop_to_turn_label);
        final TextView okText = (TextView) findViewById(R.id.ok_to_turn_label);

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
        Observable<Float> zoomObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(final Subscriber<? super Integer> subscriber) {
                leftButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscriber.onNext(-1);
                    }
                });

                rightButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscriber.onNext(1);
                    }
                });
            }
        }).scan(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                return integer + integer2;
            }
        }).startWith(0).map(new Func1<Integer, Float>() {
            @Override
            public Float call(Integer integer) {
                return 1.0f + ((float)integer * 0.1f);
            }
        });


        SensorsFragment sensors = (SensorsFragment) getSupportFragmentManager().findFragmentById(R.id.sensors_fragment);
        /*
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
        }).mergeWith(sensors.stepWithDirectionObservable).replay().refCount();

        buttonStepObservable.doOnNext(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                stopText.setVisibility(View.VISIBLE);
                okText.setVisibility(View.INVISIBLE);
            }
        }).debounce(5, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                stopText.setVisibility(View.INVISIBLE);
                okText.setVisibility(View.VISIBLE);
            }
        });


                    */

        rx.Observable<MapPosition> direction =
                sensors.stepWithDirectionObservable.map(new Func1<Integer, MapPosition>() {
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
        Observable<ArrayList<MapPosition>> positionObs = direction.scan(new Func2<MapPosition, MapPosition, MapPosition>() {
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
        });
        Observable.combineLatest(positionObs, zoomObservable, new Func2<ArrayList<MapPosition>, Float, Pair<ArrayList<MapPosition>, Float>>() {
            @Override
            public Pair<ArrayList<MapPosition>, Float> call(ArrayList<MapPosition> mapPositions, Float aFloat) {
                return new Pair<ArrayList<MapPosition>, Float>(mapPositions, aFloat);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Pair<ArrayList<MapPosition>, Float>>() {
            @Override
            public void call(Pair<ArrayList<MapPosition>, Float> arrayListFloatPair) {
                if (mSurfaceHolder == null) return;

                ArrayList<MapPosition> positions = arrayListFloatPair.first;
                Canvas canvas = mSurfaceHolder.lockCanvas();
                if (canvas == null) return;
                canvas.drawColor(Color.WHITE);

                int centerX = canvas.getWidth() / 2;
                int centerY = canvas.getHeight() / 2;
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.RED);
                paint.setStrokeWidth(10);

                Integer translationX;
                Integer translationY;
                if (positions.size() > 0) {
                    translationX = positions.get(positions.size() - 1).X;
                    translationY = positions.get(positions.size() - 1).Y;
                } else {
                    translationX = 0;
                    translationY = 0;
                }

                float scaleX = arrayListFloatPair.second;
                float scaleY = scaleX;

                canvas.translate(((float) canvas.getWidth() - scaleX * (float) canvas.getWidth()) / 2.0f,
                        ((float) canvas.getHeight() - scaleY * (float) canvas.getHeight()) / 2.0f);
                canvas.scale(scaleX, scaleY);
                drawGrid(canvas, -translationX % gridStep, -translationY % gridStep);
                //location = DeadReckoning.calculatePositionDelta(location.first, location.second, 100, null);
                for (int i = 0; i < positions.size(); i++) {
                    MapPosition start = positions.get(i);
                    MapPosition end = positions.size() > i + 1 ? positions.get(i + 1) : null;
                    if (end != null) {
                        canvas.drawLine(centerX + start.X - translationX, centerY + start.Y - translationY, centerX + end.X - translationX, centerY + end.Y - translationY, paint);
                    } else {
                        canvas.drawCircle(centerX + start.X - translationX, centerY + start.Y - translationY, 10, paint);
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
        /*

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendLocation(0,0,0).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        // create Intent to take a picture and return control to the calling application
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                        // start the image capture Intent
                        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                });


            }
        });
        */
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

    public static Observable<Boolean> sendLocation(final float x, final float y, final float z) {
        final OkHttpClient client = new OkHttpClient();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Callable c = new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final OutputStream out = new ByteArrayOutputStream();
                JsonFactory f = new JsonFactory();
                JsonGenerator g = f.createGenerator(out);
                g.writeStartObject();
                g.writeNumberField("xCoordinate", x);
                g.writeNumberField("yCoordinate", y);
                g.writeNumberField("zCoordinate", z);
                g.writeEndObject();
                g.close();
                String json = out.toString();
                Request request = new Request.Builder()
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .url("http://requestb.in/wztvrxwz")
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                //String jsonResponse = response.body().string();
                //ObjectMapper mapper = new ObjectMapper();
                //JsonNode node = mapper.readTree(jsonResponse);
                //TypeReference<User> typeRef = new TypeReference<User>(){};
                //User list;
                //list = mapper.readValue(node.traverse(), typeRef);

                return true;
            }
        };

        FutureTask<Boolean> task = new FutureTask<Boolean>(c);
        executor.execute(task);
        return Observable.from(task).subscribeOn(Schedulers.newThread());
    }

}
