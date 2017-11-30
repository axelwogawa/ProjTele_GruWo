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

    private ListView listViewLastItem;
    ArrayList<String> arrayListLastItem = new ArrayList<String>();
    //ArrayAdapter deklarieren -> initialisiert wird später (line 115)
    ArrayAdapter<String> adapterLastItem = null;

    private ListView listViewOffer;
    ArrayList<String> arrayListOffer = new ArrayList<>();
    ArrayAdapter adapterOffer = null;

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

                //insert in array to show in list
                arrayListLastItem.add(0, String.valueOf(text.getText()));
                //letztes Element aus der ArrayList entfernen (damit die Anzeige schöner ist)
                //Nachteil: nicht alle Elemente werden für immer gespeichert, sondern nur die letzten vier
                //arrayListLastItem.remove(arrayListLastItem.size() -1);

                //update text in adapter's items
                adapterLastItem.notifyDataSetChanged();

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


        //arrayListLastItem 12 Elemente hinzufügen
        for(int i=0; i<=11; i++){
            arrayListLastItem.add("Eintrag " + (i+1));
        }

        //Referenz auf die View besorgen
        listViewLastItem = (ListView) findViewById(R.id.listViewLastItem);
        adapterLastItem = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListLastItem);
        listViewLastItem.setAdapter(adapterLastItem);
        listViewLastItem.setOnItemClickListener(this);

        //add elements to arraylistoffer
        for(int i= 0; i<3; i++){
            arrayListOffer.add("Vorschlag " + (i+1));
        }

        //reference to view
        listViewOffer = (ListView) findViewById(R.id.listViewOffer);
        adapterOffer = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListOffer);
        listViewOffer.setAdapter(adapterOffer);
        //listViewOffer.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> lV, View view, int pos, long id){
        Toast.makeText(this, "Eintrag " + arrayListLastItem.get(pos) + " ausgewählt",
        Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
