package students.aalto.org.indoormappingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {


    private Button btnNext;
    private ListView listview;
    private RadioGroup radioGroup;
    private Button rgButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addItemsToRadioGroup();
        addListenerOnButton();
        addListenerOnListView();
    }



    private void addItemsToRadioGroup() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup_home);

        for(int i = 0; i < 5; i++) {
            rgButton  = new RadioButton(this);
            rgButton.setText("Building" + i);
            radioGroup.addView(rgButton);

        }


        RadioButton rbu1 =(RadioButton)radioGroup.getChildAt(0);
        int firstButtonId = rbu1.getId();
        radioGroup.check(firstButtonId);
    }

    private void addListenerOnButton() {

        btnNext = (Button) findViewById(R.id.button);

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String building = ((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("building", building);
                startActivity(intent);
            }
        });
    }
    public void addListenerOnListView(){



    }
    private void addItemsToListView() {

    }
}
