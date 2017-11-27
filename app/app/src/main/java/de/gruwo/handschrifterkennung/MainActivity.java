package de.gruwo.handschrifterkennung;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private String[] eintraege = new String[] {"Eintrag 1", "Eintrag 2", "Eintrag 3"};

    String eintrag1 = eintraege[0];
    String eintrag2 = eintraege[1];
    String eintrag3 = eintraege[2];

    //request code
    final int REQUEST_SETUP_BT_CONNECTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //toggle button allows user to set mode of the NXT device
        final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleInsert);
        //disable button initially
        toggleButton.setEnabled(false);
        //on click change mode
        toggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean checked = ((ToggleButton) v).isChecked();
                if (checked) {
                    Log.e("Toggle", "Toggled to Insert");
                } else {
                    Log.e("Toggle", "Toggled to Memo");
                }
            }
         });

        //toggle button allows user to set mode of the NXT device
        final ToggleButton toggleMemo = (ToggleButton) findViewById(R.id.toggleMemo);
        //disable button initially
        toggleMemo.setEnabled(true);
        //on click change mode
        toggleMemo.setOnClickListener(new View.OnClickListener() {
              public void onClick(View v){
                  boolean checked = ((ToggleButton) v).isChecked();
                  if (checked) {
                      Log.e("Toggle","Toggled to Memo");
                  } else{
                      Log.e("Toggle","Toggled to Insert");
                  }
              }
        });



        final Button inheritButton = (Button) findViewById(R.id.buttonInherit);
        //on click call the BluetoothActivity to choose a listed device
        inheritButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //read EditText and insert the value in the TextView
                EditText text = (EditText) findViewById(R.id.editTextInsert);
                TextView view = (TextView) findViewById(R.id.textViewValue);
                view.setText(text.getText());

                //insert in aray to show in list
                eintraege[2] = eintraege[1];
                eintraege[1] = eintraege[0];
                eintraege[0] = text.getText().toString();

                listView = (ListView) findViewById(R.id.listView);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1, eintraege);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(this);

                //clear EditText and delete the insert
                text.setText("");
            }
        });

        final Button memoButton = (Button) findViewById(R.id.toggleMemo);
        //on click call the BluetoothActivity to choose a listed device
        memoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent serverIntent = new Intent(getApplicationContext(),MemoActivity.class);
                startActivityForResult(serverIntent, REQUEST_SETUP_BT_CONNECTION);
            }
        });



        //Referenz auf die View besorgen
        listView = (ListView) findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eintraege);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> lV, View view, int pos, long id){
        Toast.makeText(this, "Eintraege " + eintraege[pos] + " ausgewählt",
        Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
