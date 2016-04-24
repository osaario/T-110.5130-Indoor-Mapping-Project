package students.aalto.org.indoormappingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
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
import rx.functions.Func3;
import students.aalto.org.indoormappingapp.model.ApplicationState;
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.model.Location;
import students.aalto.org.indoormappingapp.model.MapPosition;
import students.aalto.org.indoormappingapp.model.Photo;
import students.aalto.org.indoormappingapp.model.RenderData;
import students.aalto.org.indoormappingapp.sensors.SensorsFragment;
import students.aalto.org.indoormappingapp.sensors.SensorsSnapshot;
import students.aalto.org.indoormappingapp.services.NetworkService;
import students.aalto.org.indoormappingapp.tests.SensorsTestActivity;

class TransitionAndZoom {
    public TransitionAndZoom(float x,float y,float zoom) {
        X = x;
        Y = y;
        Zoom = zoom;
    }
    public Float X;
    public Float Y;
    public Float Zoom;
}

public class MainActivity extends MenuRouterActivity {

    SurfaceHolder mSurfaceHolder;
    private Float translationX = 0f;
    private Float translationY = 0f;
    private List<Location> photos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.loading));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle(ApplicationState.Instance().getSelectedDataSet().Name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final SurfaceView surfaceView = (SurfaceView) findViewById(R.id.main_map);

        final Button leftButton = (Button) findViewById(R.id.button_left);
        final Button rightButton = (Button) findViewById(R.id.button_right);
        final Button photoButton = (Button) findViewById(R.id.button_photo);
        final Button showLocationButton = (Button) findViewById(R.id.button_show_location);

        final View selectedLocationContainer = (View) findViewById(R.id.stop_to_turn_label);
        final TextView selectedLocName = (TextView) findViewById(R.id.selected_location_name);
        final TextView selectedLocPhotos = (TextView) findViewById(R.id.selected_location_photos);

        if(ApplicationState.Instance().getSelectedLocation() == null) {
            photoButton.setVisibility(View.GONE);
            showLocationButton.setVisibility(View.VISIBLE);
        } else {
            photoButton.setVisibility(View.VISIBLE);
            showLocationButton.setVisibility(View.GONE);
        }

        showLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               for(Location loc : photos) {
                   if(loc.X > translationX - 10 && loc.X < translationX + 10 && loc.Y > translationY - 10 && loc.Y < translationY + 10) {
                       Intent intent = new Intent(MainActivity.this, PhotoListActivity.class);
                       ApplicationState.Instance().setSelectedLocation(loc);
                       startActivity(intent);
                   }
               }

            }
        });

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(final Subscriber<? super Integer> subscriber) {
                photoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        subscriber.onNext(0);
                    }
                });
            }
        }).switchMap(new Func1<Integer, Observable<?>>() {
            @Override
            public Observable<?> call(Integer integer) {
                Location loc = ApplicationState.Instance().getSelectedLocation();
                loc.X = translationX.intValue();
                loc.Y = translationY.intValue();
                return NetworkService.saveLocation(ApplicationState.Instance().getSelectedDataSet().ID, loc);
            }
        }).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                MainActivity.this.finish();
            }
        });

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

        Observable<Pair<Float, Float>> translationObservable = Observable.create(new Observable.OnSubscribe<Pair<Float, Float>>() {
            @Override
            public void call(final Subscriber<? super Pair<Float, Float>> subscriber) {
                final Float[] downX = {null};
                final Float[] downY = {null};
                surfaceView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            downX[0] = motionEvent.getX();
                            downY[0] = motionEvent.getY();
                        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                            if (downX[0] != null && downY[0] != null) {
                                subscriber.onNext(new Pair<Float, Float>(-(motionEvent.getX() - downX[0]), -(motionEvent.getY() - downY[0])));
                                downX[0] = motionEvent.getX();
                                downY[0] = motionEvent.getY();

                            }
                        } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            downX[0] = null;
                            downY[0] = null;
                        }
                        return true;
                    }
                });

            }
        }).scan(new Func2<Pair<Float, Float>, Pair<Float, Float>, Pair<Float, Float>>() {
            @Override
            public Pair<Float, Float> call(Pair<Float, Float> floatFloatPair, Pair<Float, Float> floatFloatPair2) {
                floatFloatPair = floatFloatPair == null ? new Pair<Float, Float>(0f, 0f) : floatFloatPair;
                //accumulate
                return new Pair<Float, Float>(floatFloatPair.first + floatFloatPair2.first, floatFloatPair.second + floatFloatPair2.second);
            }
        });

        Observable<TransitionAndZoom> transitionAndZoomObservable = Observable.combineLatest(zoomObservable, translationObservable, new Func2<Float, Pair<Float, Float>, TransitionAndZoom>() {
            @Override
            public TransitionAndZoom call(Float aFloat, Pair<Float, Float> floatFloatPair) {
                TransitionAndZoom t = new TransitionAndZoom(floatFloatPair.first, floatFloatPair.second, aFloat);
                return t;
            }
        }).startWith(new TransitionAndZoom(0f,0f,1f));

        dialog.show();
        Observable.combineLatest(NetworkService.getLocations(ApplicationState.Instance().getSelectedDataSet().ID).map(new Func1<List<Location>, List<Location>>() {
            @Override
            public List<Location> call(List<Location> locations) {
                if(ApplicationState.Instance().getSelectedLocation() == null) return locations;
                ArrayList<Location> filtered = new ArrayList<Location>();
                for(Location loc : locations) {
                    if(!loc.ID.equals(ApplicationState.Instance().getSelectedLocation().ID)) {
                        filtered.add(loc);
                    }

                }
                return filtered;
            }
        }), transitionAndZoomObservable, new Func2<List<Location>, TransitionAndZoom, Pair<List<Location>, TransitionAndZoom>>() {
            @Override
            public Pair<List<Location>, TransitionAndZoom> call(List<Location> locations, TransitionAndZoom transitionAndZoom) {
                return new Pair<List<Location>, TransitionAndZoom>(locations, transitionAndZoom);
            }
        }).subscribe(new Action1<Pair<List<Location>, TransitionAndZoom>>() {
            @Override
            public void call(Pair<List<Location>, TransitionAndZoom> listTransitionAndZoomPair) {
                if (mSurfaceHolder == null) return;
                dialog.dismiss();

                photos = listTransitionAndZoomPair.first;
                Canvas canvas = mSurfaceHolder.lockCanvas();
                if (canvas == null) return;
                canvas.drawColor(Color.WHITE);

                float centerX = canvas.getWidth() / 2;
                float centerY = canvas.getHeight() / 2;

                Paint paintRed = new Paint();
                paintRed.setStyle(Paint.Style.FILL);
                paintRed.setColor(Color.RED);
                paintRed.setStrokeWidth(10);

                Paint paintYellow = new Paint();
                paintYellow.setStyle(Paint.Style.FILL);
                paintYellow.setColor(Color.MAGENTA);
                paintYellow.setStrokeWidth(10);

                Paint paintGreen = new Paint();
                paintGreen.setStyle(Paint.Style.FILL);
                paintGreen.setColor(Color.GREEN);
                paintGreen.setStrokeWidth(10);


                float scaleX = listTransitionAndZoomPair.second.Zoom;
                float scaleY = listTransitionAndZoomPair.second.Zoom;
                translationX = listTransitionAndZoomPair.second.X;
                translationY = listTransitionAndZoomPair.second.Y;

                selectedLocationContainer.setVisibility(View.GONE);
                for(Location loc : photos) {
                    if(loc.X > translationX - 10 && loc.X < translationX + 10 && loc.Y > translationY - 10 && loc.Y < translationY + 10) {
                        selectedLocationContainer.setVisibility(View.VISIBLE);
                        selectedLocName.setText(loc.Name);
                        selectedLocPhotos.setText(loc.Photos.size() + " photos");
                    }
                }

                canvas.translate(((float) canvas.getWidth() - scaleX * (float) canvas.getWidth()) / 2.0f,
                        ((float) canvas.getHeight() - scaleY * (float) canvas.getHeight()) / 2.0f);
                canvas.scale(scaleX, scaleY);
                //location = DeadReckoning.calculatePositionDelta(location.first, location.second, 100, null);
                for (int i = 0; i < photos.size(); i++) {
                    Location start = photos.get(i);
                    Paint tmpPaint;
                    if(start.Photos == null || start.Photos.size() == 0) {
                        tmpPaint = paintRed;
                    } else if(start.Photos.size() < 3) {
                        tmpPaint = paintYellow;
                    } else {
                        tmpPaint = paintGreen;
                    }

                    canvas.drawCircle(centerX + (float) start.X - translationX, centerY + (float) start.Y - translationY, 10, tmpPaint);
                }

                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.GRAY);
                paint.setStrokeWidth(2);

                canvas.drawLine(centerX - 20f, centerY, centerX + 20f, centerY, paint);
                canvas.drawLine(centerX, centerY - 20f, centerX, centerY + 20f, paint);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            MainActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

}
