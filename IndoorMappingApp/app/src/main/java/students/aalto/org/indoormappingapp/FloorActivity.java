package students.aalto.org.indoormappingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FloorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor);

        //setting width and height of the popup
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));


        addItemsToListView();
        addListenerOnListView();

    }

    private void addListenerOnListView() {
        ListView listView = (ListView) findViewById(R.id.ListView_floor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {

                String value = (String) adapter.getItemAtPosition(position);
                Intent intent = new Intent(getBaseContext(), MainActivity.class);

                Intent comingIntent = getIntent();
                String building = intent.getStringExtra("building");
                intent.putExtra("floor", value);
                intent.putExtra("building", building);
                startActivity(intent);

            }

        });
    }

    private void addItemsToListView() {
        String[] items = {"floor1", "floor2", "floor3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, items);
        ListView listView = (ListView) findViewById(R.id.ListView_floor);
        listView.setAdapter(adapter);

    }

}
