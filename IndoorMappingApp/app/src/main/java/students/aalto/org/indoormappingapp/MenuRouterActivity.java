package students.aalto.org.indoormappingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import students.aalto.org.indoormappingapp.tests.SensorsTestActivity;
import students.aalto.org.indoormappingapp.tests.ServiceTestActivity;

public abstract class MenuRouterActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_test) {
            //Intent intent = new Intent(getApplicationContext(), ServiceTestActivity.class);
            Intent intent = new Intent(getApplicationContext(), SensorsTestActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
