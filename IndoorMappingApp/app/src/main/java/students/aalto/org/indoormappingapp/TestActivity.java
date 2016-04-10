package students.aalto.org.indoormappingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.model.Location;
import students.aalto.org.indoormappingapp.model.Photo;
import students.aalto.org.indoormappingapp.services.ImageUpload;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class TestActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;

    public DataSet selectedDataSet = null;
    public Location selectedLocation = null;
    public Photo capturedPhoto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        // Add button for testing image uploading.
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.test_photo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDataSet != null && selectedLocation != null) {
                    dispatchTakePictureIntent();
                }
            }
        });

        final TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Created test\n");

        // Define multiple asynchronous tasks after each other using flatMap.
        NetworkService.getDataSets().switchMap(new Func1<List<DataSet>, Observable<DataSet>>() {
            @Override
            public Observable<DataSet> call(List<DataSet> dataSets) {

                // Do something with the data from the first task.
                textView.append("Received " + dataSets.size() + " datasets.\n");
                for (DataSet ds : dataSets) {
                    if (ds.Name.equals("Test 1")) {

                        // Skip the second task.
                        return Observable.just(ds);
                    }
                }

                // Start a second task.
                textView.append("Adding test dataset.\n");
                DataSet ds = new DataSet("Test 1", "Testing the API.");
                return NetworkService.saveDataSet(ds);

            }
        }).switchMap(new Func1<DataSet, Observable<List<Location>>>() {
            @Override
            public Observable<List<Location>> call(DataSet dataSet) {

                selectedDataSet = dataSet;
                textView.append("Selected set " + selectedDataSet.ID + "\n");
                return NetworkService.getLocations(selectedDataSet.ID);

            }
        }).switchMap(new Func1<List<Location>, Observable<Location>>() {
            @Override
            public Observable<Location> call(List<Location> locations) {

                textView.append("Received " + locations.size() + " locations.\n");
                for (Location l : locations) {
                    if (l.Name.equals("Origo")) {
                        return Observable.just(l);
                    }
                }
                textView.append("Adding test location.\n");
                Location l = new Location(0, 0, 0, "Origo");
                return NetworkService.saveLocation(selectedDataSet.ID, l);

            }
        }).doOnNext(new Action1<Location>() {
            @Override
            public void call(Location location) {
                selectedLocation = location;
                textView.append("Selected location " + selectedLocation.ID + "\n");
            }
            //no need to use flatMap in here
        }).map(new Func1<Location, Boolean>() {
            @Override
            public Boolean call(Location location) {
                return true;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {

                textView.append("Finished.\n");
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

                // Handle errors from all tasks.
                textView.append("Error, all is lost.\n");

            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                capturedPhoto = new Photo(0, 0, 0, "");
            } catch (IOException ex) {
                Log.e("test", ex.toString());
            }
            if (capturedPhoto != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(capturedPhoto.FilePath
                ));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            final TextView textView = (TextView) findViewById(R.id.textView);
            textView.append("Received an image from camera activity.\n");

            // Store a photo object and upload image.
            NetworkService.savePhoto(selectedDataSet.ID, selectedLocation.ID, capturedPhoto).flatMap(new Func1<Photo, Observable<ImageUpload>>() {
                @Override
                public Observable<ImageUpload> call(Photo photo) {
                    textView.append("Saved meta " + photo.ID + "\n");
                    return NetworkService.saveImage(photo);
                }
            }).doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    textView.append("Error, all is lost.\n");
                }
            }).subscribe(new Action1<ImageUpload>() {
                @Override
                public void call(ImageUpload imageUpload) {
                    textView.append("Uploaded image.\n");
                }
            });
        }
    }
}
