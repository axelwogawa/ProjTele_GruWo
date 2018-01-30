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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.myscript.atk.sltw.SingleLineWidget;
import com.myscript.atk.sltw.SingleLineWidgetApi;

import java.util.ArrayList;

import de.gruwo.handschrifterkennung.business.hwr.EditedText;

/**
 * Created by bi on 24.11.17.
 */

public class MemoActivity extends MySLWTActivity{
    //request code
    final int REQUEST_SETUP_BT_CONNECTION = 1;

    private int numberEntries;

    float xSet = 60;
    float ySet = 0;

    //to show the items of the list
    private ListView listViewOfferNotes;
    ArrayList<String> arrayListOfferNotes = new ArrayList<>();
    ArrayAdapter adapterOfferNotes = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        //set int-values of the ID
        final ArrayList<Integer> ids = new ArrayList<>();
        ids.add(0, R.id.button_Memo1);
        ids.add(1, R.id.button_Memo2);
        ids.add(2, R.id.button_Memo3);
        ids.add(3, R.id.button_Memo4);
        ids.add(4, R.id.button_Memo5);
        ids.add(5, R.id.button_Memo6);
        ids.add(6, R.id.button_Memo7);
        ids.add(7, R.id.button_Memo8);
        ids.add(8, R.id.button_Memo9);
        ids.add(9, R.id.button_Memo10);

        //initialisze the counter
        numberEntries = 0;

        //initialize listView for offers
        updateListOfferNotes();



        final Button backButtonNotes = (Button) findViewById(R.id.buttonBackNotes);
        //on click call the BluetoothActivity to choose a listed device
        backButtonNotes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent serverIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivityForResult(serverIntent, REQUEST_SETUP_BT_CONNECTION);
            }
        });


        //was passiert beim Speicher-Button
        final Button saveButton = (Button) findViewById(R.id.button_save);
        //on click call the BluetoothActivity to choose a listed device
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Toast.makeText(MemoActivity.this, "Hier muss noch implementiert werden...", Toast.LENGTH_LONG).show();

                if(editedText.getText().equals("")){
                    Toast.makeText(MemoActivity.this, "Es erfolgte keine Eingabe.", Toast.LENGTH_LONG).show();
                }else {
                    //create a new button
                    RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout_notes);
                    Button btn = new Button(MemoActivity.this);
                    btn.setWidth(50);
                    btn.setHeight(25);
                    btn.setId(ids.get(numberEntries));
                    //increment counter
                    numberEntries++;

                    //set the place of the button
                    //calculatethe yPosition of the button
                    if(numberEntries > 1){
                        ySet = ySet + 104;
                    }
                    //btn.setX(xSet);
                    //btn.setY(ySet);

                    final Button last = (Button) findViewById(R.id.lastButton);

                    btn.setX(xSet);
                    btn.setY(last.getY() + 50);

                    //set the colours of the button
                    btn.setBackgroundColor(Color.GRAY);
                    btn.setTextColor(Color.BLACK);
                    btn.setText("Memo " + numberEntries);

                    //set OnClickListener
                    btn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            //TODO: implement the method
                        }
                    });
                    //add button to view
                    rl.addView(btn);
                    rl.invalidate();

                    //clear EditText and delete the insert
                    //editedText.clearText();
                    //clear the widget (ansonsten wird der alte Text weiterhin verwendet)
                    widget.clear();

                    final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
                    scrollView.invalidate();

                    final RelativeLayout relative = (RelativeLayout) findViewById(R.id.rl_notes);
                    relative.invalidate();
                }
            }
        });

        //was passiert beim Speicher-Button
        final Button saveBetweenButton = (Button) findViewById(R.id.button_saveBetween);
        //on click call the BluetoothActivity to choose a listed device
        saveBetweenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
               //TODO: implement method
                //Übernahme in Label oberhalb des SingleLineWidgets
            }
        });

        //was passiert beim Löschen-Button
        final Button clearButton = (Button) findViewById(R.id.buttonClearAllNotes);
        //on click call the BluetoothActivity to choose a listed device
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //final TextView textanzeige = (TextView) findViewById(R.id.textView_notes);

                //clear EditText and delete the insert
                editedText.clearText();
                widget.clear();
                //textanzeige.setText("eingegebener Text...");

            }
        });

        //toggle button allows user to set mode of the NXT device
        final Button toggleInsertNotes = (Button) findViewById(R.id.toggleInsertNotes);
        //disable button initially
        toggleInsertNotes.setEnabled(true);
        //on click change mode
        toggleInsertNotes.setOnClickListener(new View.OnClickListener() {
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
        final Button toggleMemoNotes = (Button) findViewById(R.id.toggleMemoNotes);
        //disable button initially
        toggleMemoNotes.setEnabled(false);
        //on click change mode
        toggleMemoNotes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                boolean checked = ((ToggleButton) v).isChecked();
                if (checked) {
                    Log.e("Toggle","Toggled to Memo");
                } else{
                    Log.e("Toggle","Toggled to Insert");
                }
            }
        });

        //change tab -> user press the insert button
        final Button insertButtonNotes = (Button) findViewById(R.id.toggleInsertNotes);
        //on click call the BluetoothActivity to choose a listed device
        insertButtonNotes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //neue MainActivity starten -> aber alte Daten gehen dabei verloren
                //Intent serverIntent = new Intent(getApplicationContext(),MainActivity.class);
                //startActivityForResult(serverIntent, REQUEST_SETUP_BT_CONNECTION);

                //alte Daten bleiben erhalten
                onBackPressed();
            }
        });

        this.widget = (SingleLineWidget) findViewById(R.id.singleLine_widget_notes);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onTextChanged(SingleLineWidgetApi widget, String s, boolean intermediate){
        super.onTextChanged(widget, s, intermediate);
        //Vorschlagsliste aktualisieren
        //add elements to arraylistoffer
        this.widget = (SingleLineWidget) findViewById(R.id.singleLine_widget_notes);
        arrayListOffer = getCandidateStrings((SingleLineWidget) findViewById(R.id.singleLine_widget_notes));

        //reference to view
        listViewOfferNotes = (ListView) findViewById(R.id.listViewOfferNotes);
        adapterOfferNotes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListOfferNotes);
        listViewOfferNotes.setAdapter(adapterOfferNotes);
        listViewOfferNotes.setOnItemClickListener(this);


    }

    private void updateListOfferNotes(){
        //arrayListLastItem 2 Elemente hinzufügen
        for(int i=0; i<=2; i++){
            arrayListOfferNotes.add("");
        }

        //Referenz auf die View besorgen
        listViewOfferNotes = (ListView) findViewById(R.id.listViewOfferNotes);
        adapterOfferNotes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListOfferNotes){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                textView.setTextColor(Color.LTGRAY);

                return view;
            }
        };

        listViewOfferNotes.setAdapter(adapterOfferNotes);
        listViewOfferNotes.setOnItemClickListener(this);

        this.widget = (SingleLineWidget) findViewById(R.id.singleLine_widget_notes);
    }
    
}
