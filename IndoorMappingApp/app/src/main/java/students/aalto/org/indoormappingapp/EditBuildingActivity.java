package students.aalto.org.indoormappingapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import students.aalto.org.indoormappingapp.model.ApplicationState;
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.model.Photo;
import students.aalto.org.indoormappingapp.services.ImageUpload;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class EditBuildingActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 2;

    ProgressBar progress;
    Button cameraButton;

    DataSet dataSet;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_building_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        final Context context = this;

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
            startActivity(intent);

            progress.setVisibility(View.VISIBLE);
            NetworkService.removeDataSet(dataSet.ID).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    progress.setVisibility(View.GONE);
                    finish();
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.e("dataset", throwable.toString());
                    Toast.makeText(context, context.getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_building);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataSet = ApplicationState.Instance().getSelectedDataSet();
        String title = "Edit " + dataSet.Name;
        setTitle(title);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        cameraButton = (Button) findViewById(R.id.button_capturePhoto);

        progress.setVisibility(View.GONE);



        ((EditText) findViewById(R.id.editText_buildingName)).setText(dataSet.Name);
        ((EditText) findViewById(R.id.editText_buildingDesc)).setText(dataSet.Description);

        final Context context = this;

        final Button updateButton = (Button) findViewById(R.id.button_buildingUpdate);
        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dataSet.Name = ((EditText) findViewById(R.id.editText_buildingName)).getText().toString();
                dataSet.Description = ((EditText) findViewById(R.id.editText_buildingDesc)).getText().toString();

                progress.setVisibility(View.VISIBLE);
                NetworkService.saveDataSet(dataSet).subscribe(new Action1<DataSet>() {
                    @Override
                    public void call(DataSet dataSet) {
                        progress.setVisibility(View.GONE);
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("dataset", throwable.toString());
                        Toast.makeText(context, context.getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.GONE);
                    }
                });
            }
        });

        ((Button) findViewById(R.id.button_capturePhoto)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(dataSet.MapPhoto.createFilePath()));
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            progress.setVisibility(View.VISIBLE);
            cameraButton.setEnabled(false);

            final Context context = this;
            NetworkService.saveImage(dataSet.MapPhoto).subscribe(new Action1<ImageUpload>() {
                @Override
                public void call(ImageUpload imageUpload) {
                    progress.setVisibility(View.GONE);
                    cameraButton.setEnabled(true);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.e("dataset", throwable.toString());
                    Toast.makeText(context, context.getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                    cameraButton.setEnabled(true);
                }
            });
        }
    }

}
