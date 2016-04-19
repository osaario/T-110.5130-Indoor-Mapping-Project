package students.aalto.org.indoormappingapp;

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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import students.aalto.org.indoormappingapp.adapters.PhotoListAdapter;
import students.aalto.org.indoormappingapp.model.ApplicationState;
import students.aalto.org.indoormappingapp.model.Location;
import students.aalto.org.indoormappingapp.model.Photo;
import students.aalto.org.indoormappingapp.services.ImageUpload;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class PhotoListActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    static public String LOCATION_ID = "locationId";
    static public String DATASET_ID = "datasetId";

    String datasetId;
    String locationId;
    List<Photo> loadedPhotos;
    Photo capturedPhoto = null;
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    ProgressBar progress;
    FloatingActionButton button;
    PhotoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        //url is like this:
        // https://indoor-mapping-app-server.herokuapp.com/api/datasets/datasetID/locations/locationID/photos


        Intent intent = getIntent();
        /*
        datasetId = intent.getStringExtra(DATASET_ID);
        locationId = intent.getStringExtra(LOCATION_ID);
        */
        datasetId = ApplicationState.Instance().getSelectedDataSet().ID;
        locationId = ApplicationState.Instance().getSelectedLocation().ID;



        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);

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

        final Context context = this;
        NetworkService.getPhotos(datasetId, locationId).subscribe(new Action1<List<Photo>>() {

            @Override
            public void call(List<Photo> photos) {
                adapter.clear();
                adapter.addAll(photos);
                progress.setVisibility(View.GONE);
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            capturedPhoto = new Photo(0, 0, 0, "");
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(capturedPhoto.createFilePath()));
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            progress.setVisibility(View.VISIBLE);
            button.setEnabled(false);

            final Context context = this;
            NetworkService.savePhoto(datasetId, locationId, capturedPhoto).switchMap(new Func1<Photo, Observable<ImageUpload>>() {
                @Override
                public Observable<ImageUpload> call(Photo photo) {
                    return NetworkService.saveImage(photo);
                }
            }).subscribe(new Action1<ImageUpload>() {
                @Override
                public void call(ImageUpload imageUpload) {
                    adapter.add(capturedPhoto);
                    progress.setVisibility(View.GONE);
                    button.setEnabled(true);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.e("photos", throwable.toString());
                    Toast.makeText(context, context.getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                    button.setEnabled(true);
                }
            });
        }
    }

}
