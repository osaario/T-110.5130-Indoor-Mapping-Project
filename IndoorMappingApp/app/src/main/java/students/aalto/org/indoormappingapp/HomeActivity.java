package students.aalto.org.indoormappingapp;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.model.Location;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class HomeActivity extends MenuRouterActivity {

    private ListView listview;
    private List<DataSet> loadedDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        addItemsToListView();
        addListenerOnListView();

    }

    public void addListenerOnListView(){

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
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("building", buildingName);
                intent.putExtra("ID", buildingID);
                startActivity(intent);
            }
        });
    }

    private void addItemsToListView() {

        final Context myContext = this;

        NetworkService.getDataSets().subscribe(new Action1<List<DataSet>>() {

            @Override
            public void call(List<DataSet> dataSets) {
                loadedDataset = dataSets;
                ArrayList<String> items = new ArrayList<String>();


                for (DataSet ds : dataSets) {
                    items.add(ds.Name + " building");
                }

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(myContext, R.layout.listitem, items);
                ListView listView = (ListView) findViewById(R.id.listView_home);
                listView.setAdapter(adapter);
            }
        });

    }

}
