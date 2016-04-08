package students.aalto.org.indoormappingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class LocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);




        Button save = (Button) findViewById(R.id.button_save);

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PhotoListActivity.class);
                //intent.putExtra("locationID", 0);
                //intent.putExtra("datasetID", 0);
                startActivity(intent);
            }
        });



    }



}
