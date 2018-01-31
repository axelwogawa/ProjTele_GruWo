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
import android.widget.ImageButton;
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

    //Array zum Text speichern
    private ArrayList <String> textArray;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        //initialize textArray
        textArray = new ArrayList<>();

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

        //initialize
        final TextView label = (TextView) findViewById(R.id.textView_notes);



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

                if(editedText.getText().equals("") && label.getText().equals("")){
                    Toast.makeText(MemoActivity.this, "Es erfolgte keine Eingabe.", Toast.LENGTH_LONG).show();
                }else {
                    //create a new button
                    RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout_notes);
                    Button btn = new Button(MemoActivity.this);
                    btn.setWidth(50);
                    btn.setHeight(25);
                    btn.setId(ids.get(numberEntries));

                    //add text to button
                    if(label.getText().equals("")){
                        textArray.add(numberEntries, widget.getText());
                    }else if(widget.getText().equals("")){
                        textArray.add(numberEntries, String.valueOf(label.getText()));
                    }else{
                        String string1 = String.valueOf(label.getText());
                        String string2 = widget.getText();

                        textArray.add(numberEntries, string1 + " " + string2);
                    }


                    //increment counter
                    numberEntries++;


                    //set the place of the button
                    //calculatethe yPosition of the button
                    if(numberEntries > 1){
                        ySet = ySet + 104;
                    }
                    btn.setX(xSet);
                    btn.setY(ySet);


                    //set the colours of the button
                    btn.setBackgroundColor(Color.LTGRAY);
                    btn.setTextColor(Color.BLACK);
                    btn.setText("Memo " + numberEntries);

                    //set OnClickListener
                    btn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            //TODO: implement the method
                            Button btn = (Button) findViewById(view.getId());
                            String s = String.valueOf(btn.getText());
                            String substring = s.substring(s.length()-1);
                            //int position = s.charAt(s.length()-1);
                            int position = Integer.parseInt(substring);
                            System.out.println(s + " " + position);

                            //Toast.makeText(MemoActivity.this, btn.getId(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(MemoActivity.this, textArray.get(position -1), Toast.LENGTH_SHORT).show();
                        }
                    });
                    //add button to view
                    rl.addView(btn);
                    rl.invalidate();

                    //clear EditText and delete the insert
                    //editedText.clearText();
                    //clear the widget (ansonsten wird der alte Text weiterhin verwendet)
                    widget.clear();
                    label.setText("");

                    final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
                    scrollView.invalidate();

                    final RelativeLayout relative = (RelativeLayout) findViewById(R.id.rl_notes);
                    relative.invalidate();



                    //clear EditText and delete the insert
                    //clear the widget (ansonsten wird der alte Text weiterhin verwendet)
                    editedText.setText("");
                    widget.clear();
                    updateListOfferNotes();

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
                label.setText(widget.getText());

                editedText.setText("");
                widget.clear();
                updateListOfferNotes();
            }
        });

        //was passiert beim Löschen-Button
        final Button clearNotesButton = (Button) findViewById(R.id.buttonClearAllNotes);
        //on click call the BluetoothActivity to choose a listed device
        clearNotesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //final TextView textanzeige = (TextView) findViewById(R.id.textView_notes);

                //clear EditText and delete the insert
                editedText.setText("");
                widget.clear();
                label.setText("");
                //textanzeige.setText("eingegebener Text...");
                updateListOfferNotes();

            }
        });

        final ImageButton deleteNotesButton = (ImageButton) findViewById(R.id.buttonDeleteNotes);
        //on click call the BluetoothActivity to choose a listed device
        deleteNotesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                removeCharacter(widget);
            }
        });

        final Button holdBackButton = (Button) findViewById(R.id.button_holdBack);
        //on click call the BluetoothActivity to choose a listed device
        holdBackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //get string from label
                String stringHold = String.valueOf(label.getText());

                //set text in widget
                widget.setText(stringHold);

                //clear label
                label.setText("");
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
        String newWord;

        if(parent.getId() == R.id.listViewOfferNotes){
            Toast.makeText(this,  arrayListOfferNotes.get(position) + " ausgewählt.", Toast.LENGTH_SHORT).show();

            //change the word in the widget
            newWord = arrayListOfferNotes.get(position);
            this.replaceWord(this.widget, newWord);

        }else{
            Toast.makeText(this, "Keine Liste ausgewählt.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTextChanged(SingleLineWidgetApi widget, String s, boolean intermediate){
        super.onTextChanged(widget, s, intermediate);
        //Vorschlagsliste aktualisieren
        //add elements to arraylistoffer
        updateListOfferNotes();
    }

    private void updateListOfferNotes(){
        //clear the current arrayListOfferNotes
        arrayListOfferNotes.clear();

        //arrayListLastItem 2 Elemente hinzufügen
        if(editedText.getText().equals("")){
            for(int i=0; i<=2; i++){
                arrayListOfferNotes.add("");
            }
        }else{
            this.widget = (SingleLineWidget) findViewById(R.id.singleLine_widget_notes);
            arrayListOfferNotes = getCandidateStrings((SingleLineWidget) findViewById(R.id.singleLine_widget_notes));
            System.out.println("Methode akzeptiert " + arrayListOfferNotes);
        }


        //Referenz auf die View besorgen
        listViewOfferNotes = (ListView) findViewById(R.id.listViewOfferNotes);
        adapterOfferNotes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListOfferNotes){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                textView.setTextColor(Color.BLACK);

                return view;
            }
        };

        listViewOfferNotes.setAdapter(adapterOfferNotes);
        listViewOfferNotes.setOnItemClickListener(this);

        this.widget = (SingleLineWidget) findViewById(R.id.singleLine_widget_notes);
    }
    
}
