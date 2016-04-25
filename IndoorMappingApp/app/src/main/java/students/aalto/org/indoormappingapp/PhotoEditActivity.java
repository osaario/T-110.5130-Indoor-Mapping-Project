package students.aalto.org.indoormappingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import rx.functions.Action1;
import students.aalto.org.indoormappingapp.model.ApplicationState;
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.model.Location;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class PhotoEditActivity extends AppCompatActivity {

    DataSet dataSet;
    Location location;
    String photoID;

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
            Intent intent = new Intent(getBaseContext(), PhotoListActivity.class);
            startActivity(intent);


            //progress.setVisibility(View.VISIBLE);
            //String dataSetID, String locationID
            dataSet = ApplicationState.Instance().getSelectedDataSet();
            location = ApplicationState.Instance().getSelectedLocation();
            NetworkService.removePhoto(dataSet.ID,location.ID,photoID).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    //progress.setVisibility(View.GONE);
                    finish();
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.e("photo", throwable.toString());
                    Toast.makeText(context, context.getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                    //progress.setVisibility(View.GONE);
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        photoID = intent.getStringExtra("photoID");
        ((TextView) findViewById(R.id.textView_photoID)).setText(photoID);

    }

}
