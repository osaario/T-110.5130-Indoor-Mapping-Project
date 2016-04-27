package students.aalto.org.indoormappingapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import rx.functions.Action1;
import students.aalto.org.indoormappingapp.model.ApplicationState;
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.model.Location;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class LocationEditActivity extends AppCompatActivity {
    DataSet dataSet;
    Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_edit);

        final Context context = this;
        final Button save = (Button) findViewById(R.id.button_save);

        dataSet = ApplicationState.Instance().getSelectedDataSet();
        location= ApplicationState.Instance().getSelectedLocation();

        EditText name = (EditText) findViewById(R.id.edit_name);
        EditText description = (EditText) findViewById(R.id.edit_description);

        name.setText(location.Name);
        description.setText(location.Description);

        save.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                EditText name = (EditText) findViewById(R.id.edit_name);
                EditText description = (EditText) findViewById(R.id.edit_description);


                if (name.getText().toString().matches("")) {
                    Toast.makeText(context, context.getResources().getString(R.string.error_name), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (description.getText().toString().matches("")) {
                    Toast.makeText(context, context.getResources().getString(R.string.error_description), Toast.LENGTH_SHORT).show();
                    return;
                }

                save.setEnabled(false);
                name.setEnabled(false);
                //String dataSetID, Location location

                location.Name = name.getText().toString();
                location.Description = description.getText().toString();
                NetworkService.saveLocation(dataSet.ID, location).subscribe(new Action1<Location>() {
                    @Override
                    public void call(Location location) {
                        LocationEditActivity.this.finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("location", throwable.toString());
                        Toast.makeText(context, context.getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                    }
                });

            }

        });

    }




}
