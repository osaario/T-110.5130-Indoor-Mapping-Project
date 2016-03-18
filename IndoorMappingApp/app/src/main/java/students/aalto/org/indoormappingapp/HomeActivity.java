package students.aalto.org.indoormappingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {


    private Button btnNext;
    private ListView listview;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addItemsToListView();
        addListenerOnButton();
        addListenerOnListView();
    }



    private void addListenerOnButton() {

        btnNext = (Button) findViewById(R.id.button);

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String building = "building1";
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("building", building);
                startActivity(intent);
            }
        });
    }
    public void addListenerOnListView(){



    }
    private void addItemsToListView() {
        String[] items = {"building1","building2","building3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, items);
        ListView listView = (ListView) findViewById(R.id.listView_home);
        listView.setAdapter(adapter);
    }
}
