package students.aalto.org.indoormappingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {

    private Spinner spinner;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addItemsOnSpinner();
        addListenerOnButton();
    }

    private void addListenerOnButton() {
        spinner = (Spinner) findViewById(R.id.spinner);
        btnNext = (Button) findViewById(R.id.button);

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String building = String.valueOf(spinner.getSelectedItem());;
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("building", building );
                startActivity(intent);
            }

        });

    }

    private void addItemsOnSpinner() {
        spinner = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        list.add("building 1");
        list.add("building 2");
        list.add("building 3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }
}
