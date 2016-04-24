package students.aalto.org.indoormappingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;
import students.aalto.org.indoormappingapp.model.ApplicationState;
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.services.NetworkService;
import students.aalto.org.indoormappingapp.tests.SensorsTestActivity;

public class HomeActivity extends AppCompatActivity {
    boolean editMode = false;
    List<DataSet> loadedDataset;
    private ProgressDialog dialog;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.loading));

        addListenerOnListView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), DataSetActivity.class);
                startActivity(intent);
            }
        });

        Button button= (Button) findViewById(R.id.buttonEdit);
        //edit button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editMode = !editMode;
                if (editMode){
                    setTitle("Select building to modify!");
                } else{
                    setTitle("Indoor Mapping!");
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_test) {
            Intent intent = new Intent(getBaseContext(), SensorsTestActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        subscription.unsubscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialog.show();
        subscription = NetworkService.getDataSets().subscribe(new Action1<List<DataSet>>() {

            @Override
            public void call(List<DataSet> dataSets) {
                dialog.dismiss();
                loadedDataset = dataSets;
                ArrayList<String> items = new ArrayList<String>();

                for (DataSet ds : dataSets) {
                    items.add(ds.Name);
                }

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeActivity.this, R.layout.listitem, items);
                ListView listView = (ListView) findViewById(R.id.listView_home);
                listView.setAdapter(adapter);


            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                dialog.dismiss();
                Log.e("location", throwable.toString());
                Toast.makeText(HomeActivity.this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void addListenerOnListView() {

        ListView listView = (ListView) findViewById(R.id.listView_home);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                Log.v("tag", "position" + position);
                String buildingName = (String) loadedDataset.get(position).Name;
                String buildingID = (String) loadedDataset.get(position).ID;
                Log.v("tag", "ID " + buildingID);

                // Floor activity disabled temporarily
                //Intent intent = new Intent(getBaseContext(), FloorActivity.class);
                ApplicationState.Instance().setSelectedDataSet(loadedDataset.get(position));
                if (!editMode) {
                    //normal mode
                    Intent intent = new Intent(getBaseContext(), LocationListActivity.class);
                    intent.putExtra("building", buildingName);
                    intent.putExtra("ID", buildingID);
                    startActivity(intent);
                } else{
                    //edit mode
                    Intent intent = new Intent(getBaseContext(), EditBuildingActivity.class);
                    intent.putExtra("building", buildingName);
                    intent.putExtra("ID", buildingID);
                    startActivity(intent);
                }

            }
        });
    }

    private void addItemsToListView() {

        final Context context = this;

    }
}
