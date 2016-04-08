package students.aalto.org.indoormappingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.model.Photo;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class PhotoListActivity extends AppCompatActivity {

    static public String LOCATION_ID = "locationId";
    static public String DATASET_ID = "datasetId";
    private List<Photo> loadedPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        // imageView_testPhoto

        //url is like this:
        // https://indoor-mapping-app-server.herokuapp.com/api/datasets/datasetID/locations/locationID/photos
        Intent intent = getIntent();

        String datasetId = intent.getStringExtra(DATASET_ID);
        String locationId = intent.getStringExtra(LOCATION_ID);
        final Context myContext = this;

        NetworkService.getPhotos(datasetId,locationId).subscribe(new Action1<List<Photo>>() {

            @Override
            public void call(List<Photo> photos) {
                loadedPhotos = loadedPhotos;
                ArrayList<String> items = new ArrayList<String>();


                for (Photo photo : photos) {
                    items.add(photo.Created + " building");
                }

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(myContext, R.layout.listitem, items);
                ListView listView = (ListView) findViewById(R.id.listView_photolist);
                listView.setAdapter(adapter);
            }
        });


    }

}
