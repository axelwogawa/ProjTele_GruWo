package de.gruwo.handschrifterkennung;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.myscript.atk.sltw.SingleLineWidget;

import java.util.ArrayList;


/**
 * The type Main activity.
 */
public class MainActivity extends MySLWTActivity{

    //TODO: make attributes public or private
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
        final Button toggleButton = (Button) findViewById(R.id.toggleInsert);
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
        final Button toggleMemo = (Button) findViewById(R.id.toggleMemo);
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

        //was passiert beim Übernehmen-Button
        final Button inheritButton = (Button) findViewById(R.id.buttonInherit);
        //on click call the BluetoothActivity to choose a listed device
        inheritButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //read EditText and insert the value in the TextView
                //(EditText) findViewById(R.id.editTextInsert);
                TextView view = (TextView) findViewById(R.id.textViewValue);
                view.setText(editedText.getText());

                if(editedText.getText().equals("")){
                    Toast.makeText(MainActivity.this, "Es gab keine Eingabe.", Toast.LENGTH_LONG).show();
                }else {

                    //insert in array to show in list
                    arrayListLastItem.add(0, String.valueOf(editedText.getText()));
                    //letztes Element aus der ArrayList entfernen (damit die Anzeige schöner ist)
                    //Nachteil: nicht alle Elemente werden für immer gespeichert, sondern nur die letzten vier
                    arrayListLastItem.remove(arrayListLastItem.size() - 1);

                    //update text in adapter's items
                    adapterLastItem.notifyDataSetChanged();

                    //clear EditText and delete the insert
                    editedText.clearText();
                    //clear the widget (ansonsten wird der alte Text weiterhin verwendet)
                    widget.clear();
                }
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
        adapterLastItem = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListLastItem){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                /*YOUR CHOICE OF COLOR*/
                textView.setTextColor(Color.LTGRAY);

                return view;
            }
        };

        /*SET THE ADAPTER TO LISTVIEW*/
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

        View widgetView = findViewById(R.id.singleLine_widget);
        this.widget = (SingleLineWidget) widgetView;
        super.configureWidget(this.widget, widgetView);
    }


    @Override
    public void onItemClick(AdapterView<?> lV, View view, int pos, long id){
        Toast.makeText(this, "Eintrag " + arrayListLastItem.get(pos) + " ausgewählt",
        Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    /**
     * This method was taken from MyScript Single Line Text Widged documentation;
     * url: https://developer.myscript.com/old-docs/atk/2.2/android/text/sltw.html
     */
    @Override
    protected void onDestroy()
    {
        this.widget.setOnTextChangedListener(null);
        this.widget.setOnConfiguredListener(null);
        super.onDestroy();
    }


    /**
     * handle pressing button with alert dialog if connected(non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
