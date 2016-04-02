package students.aalto.org.indoormappingapp;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import students.aalto.org.indoormappingapp.model.DataSet;
import students.aalto.org.indoormappingapp.services.NetworkService;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        final TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Created test\n");

        // Define multiple asynchronous tasks after each other using flatMap.
        NetworkService.getDataSets().flatMap(new Func1<List<DataSet>, Observable<DataSet>>() {
            @Override
            public Observable<DataSet> call(List<DataSet> dataSets) {

                // Do something with the data from the first task.
                textView.append("Received " + dataSets.size() + " datasets.\n");
                for (DataSet ds : dataSets) {
                    if (ds.Name.equals("Test 1")) {

                        // Continue immediately with the next task.
                        return Observable.just(null);
                    }
                }

                // Start a second task.
                textView.append("Adding test dataset.\n");
                DataSet ds = new DataSet("Test 1", "Testing the API.");
                return NetworkService.saveDataSet(ds);
            }
        }).flatMap(new Func1<DataSet, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(DataSet dataSet) {

                if (dataSet != null) {
                    textView.append("Created dataset id=" + dataSet.ID);
                }
                return Observable.just(true);
            }
        }).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

                // Handle errors from all tasks.
                textView.append("Error, all is lost.\n");
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {

                // Do something with the data from the last task.
                textView.append("Finished.\n");
            }
        });
    }
}
