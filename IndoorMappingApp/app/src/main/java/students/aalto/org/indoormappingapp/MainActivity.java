package students.aalto.org.indoormappingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import students.aalto.org.indoormappingapp.model.ApplicationState;
import students.aalto.org.indoormappingapp.model.Location;
import students.aalto.org.indoormappingapp.services.NetworkService;

class RenderState {
    public RenderState(float x,float y,float zoom, Bitmap bitmap) {
        X = x;
        Y = y;
        Zoom = zoom;
        BackgroundImage = bitmap;
    }
    public Float X;
    public Bitmap BackgroundImage;
    public Float Y;
    public Float Zoom;
}

public class MainActivity extends AppCompatActivity {

    SurfaceHolder mSurfaceHolder;
    private Float translationX = 0f;
    private Float translationY = 0f;
    private List<Location> photos;
    private Location recordStartLoc;
    private Subscription subscription;


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private Location locationUnderCursor() {
        for(Location loc : photos) {
            if(loc.X > translationX - 10 && loc.X < translationX + 10 && loc.Y > translationY - 10 && loc.Y < translationY + 10) {
                return loc;
            }
        }
        return null;
    }

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
        final Button startRecordButton = (Button) findViewById(R.id.button_start_record);
        final Button endRecordButton = (Button) findViewById(R.id.button_end_record);

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

        if(ApplicationState.Instance().getSelectedLocation() != null) {
            startRecordButton.setVisibility(View.GONE);
            endRecordButton.setVisibility(View.GONE);
        } else {
            startRecordButton.setVisibility(View.VISIBLE);
            endRecordButton.setVisibility(View.GONE);
        }

        startRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordStartLoc = locationUnderCursor();
                Toast.makeText(MainActivity.this, R.string.start_walking, Toast.LENGTH_LONG).show();
                startRecordButton.setVisibility(View.GONE);
                endRecordButton.setVisibility(View.VISIBLE);
            }
        });

        endRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordStartLoc = null;
                Toast.makeText(MainActivity.this, R.string.record_ended, Toast.LENGTH_LONG).show();
                startRecordButton.setVisibility(View.VISIBLE);
                endRecordButton.setVisibility(View.GONE);

            }
        });

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
                        dialog.setMessage(getString(R.string.sending));
                        dialog.show();
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
                dialog.dismiss();
                MainActivity.this.finish();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_LONG);
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
        Observable<Float> zoomObservable = Observable.create(new Observable.OnSubscribe<Float>() {
            @Override
            public void call(final Subscriber<? super Float> subscriber) {
                leftButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscriber.onNext(-1f);
                    }
                });

                rightButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscriber.onNext(1f);
                    }
                });
            }
        }).scan(new Func2<Float, Float, Float>() {
            @Override
            public Float call(Float integer, Float integer2) {
                return integer + integer2;
            }
        }).startWith(0f).map(new Func1<Float, Float>() {
            @Override
            public Float call(Float aFloat) {
                return 1.0f + (aFloat * 0.1f);
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
                        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
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
        }).startWith(new Pair<Float, Float>(0f,0f));

        Observable<Bitmap> backgroundObs = Observable.just(1).observeOn(Schedulers.newThread()).map(new Func1<Integer, Bitmap>() {
            @Override
            public Bitmap call(Integer integer) {
                Bitmap bmp = getBitmapFromURL(ApplicationState.Instance().getSelectedDataSet().MapPhoto.URL);
                return bmp;
            }
        }).observeOn(AndroidSchedulers.mainThread());

        Observable<RenderState> transitionAndZoomObservable = Observable.combineLatest(zoomObservable, translationObservable, backgroundObs, new Func3<Float, Pair<Float, Float>, Bitmap, RenderState>() {
            @Override
            public RenderState call(Float aFloat, Pair<Float, Float> floatFloatPair, Bitmap bitmap) {
                RenderState t = new RenderState(floatFloatPair.first, floatFloatPair.second, aFloat, bitmap);
                return t;
            }
        });

        dialog.show();
        subscription = Observable.combineLatest(NetworkService.getLocations(ApplicationState.Instance().getSelectedDataSet().ID).map(new Func1<List<Location>, List<Location>>() {
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
        }), transitionAndZoomObservable, new Func2<List<Location>, RenderState, Pair<List<Location>, RenderState>>() {
            @Override
            public Pair<List<Location>, RenderState> call(List<Location> locations, RenderState transitionAndZoom) {
                return new Pair<List<Location>, RenderState>(locations, transitionAndZoom);
            }
        }).subscribe(new Action1<Pair<List<Location>, RenderState>>() {
            @Override
            public void call(Pair<List<Location>, RenderState> listTransitionAndZoomPair) {
                if (mSurfaceHolder == null) return;
                dialog.dismiss();

                photos = listTransitionAndZoomPair.first;
                Canvas canvas = mSurfaceHolder.lockCanvas();
                if (canvas == null) return;
                canvas.drawColor(Color.WHITE);

                float centerX = canvas.getWidth() / 2;
                float centerY = canvas.getHeight() / 2;

                float scaleX = listTransitionAndZoomPair.second.Zoom;
                float scaleY = listTransitionAndZoomPair.second.Zoom;
                translationX = listTransitionAndZoomPair.second.X;
                translationY = listTransitionAndZoomPair.second.Y;

                Paint paintRed = new Paint();
                paintRed.setStyle(Paint.Style.FILL);
                paintRed.setColor(Color.RED);
                paintRed.setStrokeWidth(10f / scaleX);

                Paint paintYellow = new Paint();
                paintYellow.setStyle(Paint.Style.FILL);
                paintYellow.setColor(Color.MAGENTA);
                paintYellow.setStrokeWidth(10f / scaleX);

                Paint paintGreen = new Paint();
                paintGreen.setStyle(Paint.Style.FILL);
                paintGreen.setColor(Color.GREEN);
                paintGreen.setStrokeWidth(10f / scaleX);


                selectedLocationContainer.setVisibility(View.GONE);
                for (Location loc : photos) {
                    if (loc.X > translationX - 10 && loc.X < translationX + 10 && loc.Y > translationY - 10 && loc.Y < translationY + 10) {
                        selectedLocationContainer.setVisibility(View.VISIBLE);
                        selectedLocName.setText(loc.Name);
                        selectedLocPhotos.setText(loc.Photos.size() + " photos");
                    }
                }

                canvas.translate(((float) canvas.getWidth() - scaleX * (float) canvas.getWidth()) / 2.0f,
                        ((float) canvas.getHeight() - scaleY * (float) canvas.getHeight()) / 2.0f);
                canvas.scale(scaleX + 0.01f, scaleY + 0.01f);
                if (listTransitionAndZoomPair.second.BackgroundImage != null) {
                    canvas.drawBitmap(listTransitionAndZoomPair.second.BackgroundImage, centerX - translationX - (listTransitionAndZoomPair.second.BackgroundImage.getWidth() / 2), centerY - translationY - (listTransitionAndZoomPair.second.BackgroundImage.getHeight() / 2), null);
                }
                //location = DeadReckoning.calculatePositionDelta(location.first, location.second, 100, null);
                for (int i = 0; i < photos.size(); i++) {
                    Location start = photos.get(i);
                    Paint tmpPaint;
                    if (start.Photos == null || start.Photos.size() == 0) {
                        tmpPaint = paintRed;
                    } else if (start.Photos.size() < 3) {
                        tmpPaint = paintYellow;
                    } else {
                        tmpPaint = paintGreen;
                    }

                    canvas.drawCircle(centerX + (float) start.X - translationX, centerY + (float) start.Y - translationY, 10f / scaleX, tmpPaint);
                }

                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.BLUE);
                paint.setStrokeWidth(5);

                canvas.drawLine(centerX - 20f, centerY, centerX + 20f, centerY, paint);
                canvas.drawLine(centerX, centerY - 20f, centerX, centerY + 20f, paint);
                mSurfaceHolder.unlockCanvasAndPost(canvas);


            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_LONG);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

}
