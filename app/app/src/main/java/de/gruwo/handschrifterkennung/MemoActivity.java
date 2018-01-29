package de.gruwo.handschrifterkennung;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.myscript.atk.sltw.SingleLineWidget;
import com.myscript.atk.sltw.SingleLineWidgetApi;

import de.gruwo.handschrifterkennung.business.hwr.EditedText;

/**
 * Created by bi on 24.11.17.
 */

public class MemoActivity extends MySLWTActivity{
    //request code
    final int REQUEST_SETUP_BT_CONNECTION = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

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
                Toast.makeText(MemoActivity.this, "Hier muss noch implementiert werden...", Toast.LENGTH_LONG).show();

                if(editedText.getText().equals("")){
                    Toast.makeText(MemoActivity.this, "Es erfolgte keine Eingabe.", Toast.LENGTH_LONG).show();
                }else {
                    //clear EditText and delete the insert
                    editedText.clearText();
                    //clear the widget (ansonsten wird der alte Text weiterhin verwendet)
                    widget.clear();
                }
            }
        });

        //was passiert beim Löschen-Button
        final Button clearButton = (Button) findViewById(R.id.button_clear);
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
    
}
