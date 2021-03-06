package students.aalto.org.indoormappingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;
import students.aalto.org.indoormappingapp.adapters.LocationListAdapter;
import students.aalto.org.indoormappingapp.model.ApplicationState;
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.model.Location;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class LocationListActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private LocationListAdapter listAdapter;
    private Subscription loadSubscription;
    DataSet dataSet;

    @Override
    protected void onPause() {
        super.onPause();
        loadSubscription.unsubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_map) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_edit) {
            dataSet = ApplicationState.Instance().getSelectedDataSet();
            Intent intent = new Intent(getBaseContext(), EditBuildingActivity.class);
            intent.putExtra("building", dataSet.Name);
            intent.putExtra("ID", dataSet.Description);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationState.Instance().setSelectedLocation(null);

        dialog.show();
        loadSubscription = NetworkService.getLocations(ApplicationState.Instance().getSelectedDataSet().ID)
                .subscribe(new Action1<List<Location>>() {
                    @Override
                    public void call(List<Location> locations) {
                        dialog.hide();
                        listAdapter.clear();
                        listAdapter.addAll(locations);
                    }
                });
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

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.loading));

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
    }

}
