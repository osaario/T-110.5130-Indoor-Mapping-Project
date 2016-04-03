package students.aalto.org.indoormappingapp;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.model.Location;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class TestActivity extends AppCompatActivity {

    public DataSet selectedDataSet = null;
    public Location selectedLocation = null;

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

        final TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Created test\n");

        // Define multiple asynchronous tasks after each other using flatMap.
        NetworkService.getDataSets().flatMap(new Func1<List<DataSet>, Observable<DataSet>>() {
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
        }).flatMap(new Func1<DataSet, Observable<List<Location>>>() {
            @Override
            public Observable<List<Location>> call(DataSet dataSet) {

                selectedDataSet = dataSet;
                textView.append("Selected set " + selectedDataSet.ID + "\n");
                return NetworkService.getLocations(selectedDataSet.ID);

            }
        }).flatMap(new Func1<List<Location>, Observable<Location>>() {
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
        }).flatMap(new Func1<Location, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Location location) {

                selectedLocation = location;
                textView.append("Selected location " + selectedLocation.ID + "\n");
                return Observable.just(true);

            }
        }).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

                // Handle errors from all tasks.
                textView.append("Error, all is lost.\n");

            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {

                textView.append("Finished.\n");
            }
        });
    }
}
