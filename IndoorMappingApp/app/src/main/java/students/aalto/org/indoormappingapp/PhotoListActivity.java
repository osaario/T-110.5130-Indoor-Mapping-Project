package students.aalto.org.indoormappingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import students.aalto.org.indoormappingapp.adapters.PhotoListAdapter;
import students.aalto.org.indoormappingapp.model.ApplicationState;
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.model.Location;
import students.aalto.org.indoormappingapp.model.Photo;
import students.aalto.org.indoormappingapp.model.Sensor;
import students.aalto.org.indoormappingapp.sensors.SensorsFragment;
import students.aalto.org.indoormappingapp.services.ImageUpload;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class PhotoListActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    static public String LOCATION_ID = "locationId";
    static public String DATASET_ID = "datasetId";

    DataSet dataSet;
    Location location;

    String datasetId;
    String locationId;
    List<Photo> loadedPhotos;
    Photo capturedPhoto = null;
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    ProgressBar progress;
    FloatingActionButton button;
    PhotoListAdapter adapter;
    private Subscription subscription;
    private ProgressDialog dialog;

    SensorsFragment sensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(ApplicationState.Instance().getSelectedLocation().Name);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        sensors = (SensorsFragment) getSupportFragmentManager().findFragmentById(R.id.sensors_fragment);
        sensors.configureCache(30, true);

        //url is like this:
        // https://indoor-mapping-app-server.herokuapp.com/api/datasets/datasetID/locations/locationID/photos

        Intent intent = getIntent();
        /*
        datasetId = intent.getStringExtra(DATASET_ID);
        locationId = intent.getStringExtra(LOCATION_ID);
        */
        datasetId = ApplicationState.Instance().getSelectedDataSet().ID;
        locationId = ApplicationState.Instance().getSelectedLocation().ID;


        button = (FloatingActionButton) findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        adapter = new PhotoListAdapter(this, R.layout.listitem);
        ListView list = (ListView) findViewById(R.id.listView_photoList);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Photo photo = adapter.getItem(i);
                Intent intent = new Intent(getApplicationContext(), PhotoEditActivity.class);
                intent.putExtra("photoID",photo.ID);
                startActivity(intent);
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.loading));

        dialog.show();
        subscription = NetworkService.getPhotos(datasetId, locationId).subscribe(new Action1<List<Photo>>() {

            @Override
            public void call(List<Photo> photos) {
                adapter.clear();
                dialog.dismiss();
                adapter.addAll(photos);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                dialog.dismiss();
                Toast.makeText(PhotoListActivity.this, getString(R.string.network_error), Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_pin_to_map) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_delete) {
            Intent intent = new Intent(getBaseContext(), LocationListActivity.class);
            startActivity(intent);
            //remove location
            final Context context = this;
            //progress.setVisibility(View.VISIBLE);
            //String dataSetID, String locationID
            dataSet = ApplicationState.Instance().getSelectedDataSet();
            location = ApplicationState.Instance().getSelectedLocation();
            NetworkService.removeLocation(dataSet.ID,location.ID).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    //progress.setVisibility(View.GONE);
                    finish();
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.e("location", throwable.toString());
                    Toast.makeText(context, context.getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                    //progress.setVisibility(View.GONE);
                }
            });


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        sensors.startRecording(false);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            capturedPhoto = new Photo("");
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(capturedPhoto.createFilePath()));
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            button.setEnabled(false);

            sensors.stopRecording();
            capturedPhoto.Sensor = new Sensor(sensors.cache.getList());

            dialog.setMessage(getString(R.string.sending));
            final Context context = this;
            dialog.show();
            NetworkService.savePhoto(datasetId, locationId, capturedPhoto).switchMap(new Func1<Photo, Observable<ImageUpload>>() {
                @Override
                public Observable<ImageUpload> call(Photo photo) {
                    return NetworkService.saveImage(photo);
                }
            }).subscribe(new Action1<ImageUpload>() {
                @Override
                public void call(ImageUpload imageUpload) {
                    dialog.dismiss();
                    adapter.add(capturedPhoto);
                    button.setEnabled(true);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    dialog.dismiss();
                    Log.e("photos", throwable.toString());
                    Toast.makeText(context, context.getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                    button.setEnabled(true);
                }
            });
        }
    }

}
