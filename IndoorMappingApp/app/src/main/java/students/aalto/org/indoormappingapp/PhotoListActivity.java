package students.aalto.org.indoormappingapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import students.aalto.org.indoormappingapp.model.Photo;
import students.aalto.org.indoormappingapp.services.ImageUpload;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class PhotoListActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    static public String LOCATION_ID = "locationId";
    static public String DATASET_ID = "datasetId";

    private String datasetId;
    private String locationId;
    private List<Photo> loadedPhotos;
    private Photo capturedPhoto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        // imageView_testPhoto

        //url is like this:
        // https://indoor-mapping-app-server.herokuapp.com/api/datasets/datasetID/locations/locationID/photos
        Intent intent = getIntent();

        datasetId = intent.getStringExtra(DATASET_ID);
        locationId = intent.getStringExtra(LOCATION_ID);
        final Context myContext = this;

        try {
            NetworkService.getPhotos(datasetId, locationId).subscribe(new Action1<List<Photo>>() {

                @Override
                public void call(List<Photo> photos) {

                    ArrayList<String> items = new ArrayList<String>();

                    for (Photo photo : photos) {
                        items.add(photo.Created.toString());
                    }

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(myContext, R.layout.listitem, items);
                    ListView listView = (ListView) findViewById(R.id.listView_photoList);
                    listView.setAdapter(adapter);
                }
            });
        } catch (IOException e) {
            Log.e("photolist", e.toString());
        }

        Button button = (Button) findViewById(R.id.button_photoList);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
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

            // Store a photo object and upload image.
            NetworkService.savePhoto(datasetId, locationId, capturedPhoto).switchMap(new Func1<Photo, Observable<ImageUpload>>() {
                @Override
                public Observable<ImageUpload> call(Photo photo) {
                    return NetworkService.saveImage(photo);
                }
            }).subscribe(new Action1<ImageUpload>() {
                @Override
                public void call(ImageUpload imageUpload) {
                    //Add to photo list.
                }
            });
        }
    }

}
