package students.aalto.org.indoormappingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import rx.functions.Action1;
import students.aalto.org.indoormappingapp.model.ApplicationState;
import students.aalto.org.indoormappingapp.model.Location;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class LocationActivity extends AppCompatActivity {

    static public String DATASET_ID = "dataset";
    static public String X_COORDINATE = "x";
    static public String Y_COORDINATE = "y";
    static public String Z_COORDINATE = "z";

    String dataSetID;
    Integer x, y, z;

    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        dataSetID = intent.getStringExtra(DATASET_ID);
        x = intent.getIntExtra(X_COORDINATE, 0);
        y = intent.getIntExtra(Y_COORDINATE, 0);
        z = intent.getIntExtra(Z_COORDINATE, 0);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);

        final Context context = this;
        final Button save = (Button) findViewById(R.id.button_save);

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText name = (EditText) findViewById(R.id.edit_name);
                if (name.getText().toString().matches("")) {
                    Toast.makeText(context, context.getResources().getString(R.string.error_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                save.setEnabled(false);
                name.setEnabled(false);
                progress.setVisibility(View.VISIBLE);
                createLocation(new Location(x, y, z, name.getText().toString()));
            }

        });
    }

    protected void createLocation(Location location) {
        final Context context = this;
        NetworkService.saveLocation(dataSetID, location).subscribe(new Action1<Location>() {
            @Override
            public void call(Location location) {
                Intent intent = new Intent(context, PhotoListActivity.class);
                ApplicationState.Instance().setSelectedLocation(location);
                startActivity(intent);
                finish();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e("location", throwable.toString());
                Toast.makeText(context, context.getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();

                progress.setVisibility(View.GONE);
                findViewById(R.id.button_save).setEnabled(true);
                findViewById(R.id.edit_name).setEnabled(true);
            }
        });
    }

}
