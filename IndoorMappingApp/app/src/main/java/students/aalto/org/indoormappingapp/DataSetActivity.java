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
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class DataSetActivity extends AppCompatActivity {

    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_set);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);

        final Context context = this;
        final Button save = (Button) findViewById(R.id.button_save);

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText name = (EditText) findViewById(R.id.edit_name);
                EditText description = (EditText) findViewById(R.id.edit_description);
                if (name.getText().toString().matches("")) {
                    Toast.makeText(context, context.getResources().getString(R.string.error_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                save.setEnabled(false);
                name.setEnabled(false);
                description.setEnabled(false);

                progress.setVisibility(View.VISIBLE);
                createDataSet(new DataSet(name.getText().toString(), description.getText().toString()));
            }
        });
    }

    protected void createDataSet(DataSet dataSet) {
        final Context context = this;
        NetworkService.saveDataSet(dataSet).subscribe(new Action1<DataSet>() {
            @Override
            public void call(DataSet dataSet) {
                Intent intent = new Intent(context, PhotoListActivity.class);
                finish();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e("dataset", throwable.toString());
                Toast.makeText(context, context.getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();

                progress.setVisibility(View.GONE);
                findViewById(R.id.button_save).setEnabled(true);
                findViewById(R.id.edit_name).setEnabled(true);
                findViewById(R.id.edit_description).setEnabled(true);
            }
        });
    }

}
