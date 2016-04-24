package students.aalto.org.indoormappingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class EditBuildingActivity extends AppCompatActivity {

    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_building);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        String building = intent.getStringExtra("building");

        TextView buildingNameTextView = (TextView)findViewById(R.id.Textview_buildingName);
        TextView buildingIDTextView = (TextView)findViewById(R.id.Textview_buildingID);

        buildingIDTextView.setText(id);
        buildingNameTextView.setText(building);

        final Button deleteButton = (Button) findViewById(R.id.button_dataset_remove);
        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NetworkService.removeDataSet(id);
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });





        final Button updateButton = (Button) findViewById(R.id.button_buildingUpdate);
        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Textview_buildingID
                EditText buildingNameEdit = (EditText)findViewById(R.id.editText_buildingName);
                EditText buildingDescEdit = (EditText)findViewById(R.id.editText_buildingDesc);
                final String newDatasetName = buildingNameEdit.getText().toString();
                final String newDatasetDesc = buildingDescEdit.getText().toString();
                DataSet datasetnew = new DataSet(newDatasetName,newDatasetDesc);
                datasetnew.ID = id;
                NetworkService.saveDataSet(datasetnew);
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

    }

}
