package students.aalto.org.indoormappingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;
import students.aalto.org.indoormappingapp.adapters.LocationListAdapter;
import students.aalto.org.indoormappingapp.model.ApplicationState;
import students.aalto.org.indoormappingapp.model.Location;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class LocationListActivity extends AppCompatActivity {

    private LocationListAdapter listAdapter;
    private Subscription loadSubscription;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadSubscription.unsubscribe();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(ApplicationState.Instance().getSelectedDataSet().Name);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start create location.
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                intent.putExtra(LocationActivity.DATASET_ID, ApplicationState.Instance().getSelectedDataSet().ID);
                intent.putExtra(LocationActivity.X_COORDINATE, -1);
                intent.putExtra(LocationActivity.Y_COORDINATE, -1);
                intent.putExtra(LocationActivity.Z_COORDINATE, -1);
                startActivity(intent);
            }
        });

        ListView locationListView = (ListView) findViewById(R.id.location_list_view);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.location_list_progress);

        listAdapter = new LocationListAdapter(this, 0);

        locationListView.setAdapter(listAdapter);

        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Location location = listAdapter.getItem(i);
                ApplicationState.Instance().setSelectedLocation(location);
                Intent intent = new Intent(getApplicationContext(), PhotoListActivity.class);
                startActivity(intent);
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        loadSubscription = NetworkService.getLocations(ApplicationState.Instance().getSelectedDataSet().ID)
                .subscribe(new Action1<List<Location>>() {
                    @Override
                    public void call(List<Location> locations) {
                        progressBar.setVisibility(View.GONE);
                        listAdapter.clear();
                        listAdapter.addAll(locations);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LocationListActivity.this, throwable.getLocalizedMessage(), Toast.LENGTH_LONG);
                    }
                });
    }

}
