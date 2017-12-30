package de.gruwo.handschrifterkennung;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import de.gruwo.handschrifterkennung.certificate.MyCertificate;

/**
 * Created by bi on 24.11.17.
 */

public class MemoActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener,
        SingleLineWidgetApi.OnConfiguredListener,
        SingleLineWidgetApi.OnTextChangedListener{

    private SingleLineWidgetApi widget;

    private EditedText editedText;

    //request code
    final int REQUEST_SETUP_BT_CONNECTION = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        this.editedText = new EditedText();


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

        //the following code was taken from MyScript Single Line Text Widged documentation
        //url: https://developer.myscript.com/old-docs/atk/2.2/android/text/sltw.html
        //CITATION_START
        widget = (SingleLineWidget) findViewById(R.id.singleLine_widget_notes);
        if (!widget.registerCertificate(MyCertificate.getBytes()))
        {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Please use a valid certificate.");
            dlgAlert.setTitle("Invalid certificate");
            dlgAlert.setCancelable(false);
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    //dismiss the dialog
                }
            });
            dlgAlert.create().show();
            return;
        }

        widget.setOnConfiguredListener(this);
        widget.setOnTextChangedListener(this);

        // references assets directly from the APK to avoid extraction in application
        // file system
        //TODO: check, if path makes sense
        widget.addSearchDir("zip://" + getPackageCodePath() + "!/assets/conf");

        // The configuration is an asynchronous operation. Callbacks are provided to
        // monitor the beginning and end of the configuration process and update the UI
        // of the input method accordingly.
        //
        //TODO: switch language to German, check for further configurations
        // "en_US" references the en_US bundle name in conf/en_US.conf file in your assets.
        // "cur_text" references the configuration name in en_US.conf
        widget.configure("de_DE", "cur_text");
        //CITATION_END
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * This method was copied from MyScript Single Line Text Widged documentation;
     * url: https://developer.myscript.com/old-docs/atk/2.2/android/text/sltw.html
     */
    @Override
    public void onConfigured(SingleLineWidgetApi widget, boolean success)
    {
        if(!success)
        {
            Toast.makeText(getApplicationContext(), widget.getErrorString(), Toast.LENGTH_LONG).show();
            //Log.e(TAG, "Unable to configure the Single Line Widget: " + widget.getErrorString());
            return;
        }
        Toast.makeText(getApplicationContext(), "Single Line Widget Configured", Toast.LENGTH_SHORT).show();
        if(BuildConfig.DEBUG)
            ;//Log.d(TAG, "Single Line Widget configured!");
    }

    /**
     * This method was copied from MyScript Single Line Text Widged documentation;
     * url: https://developer.myscript.com/old-docs/atk/2.2/android/text/sltw.html
     */
    @Override
    public void onTextChanged(SingleLineWidgetApi widget, String s, boolean intermediate)
    {
        Toast.makeText(getApplicationContext(), "Recognition update: "+s, Toast.LENGTH_SHORT).show();

        //Textanzeige des eingegebenen Textes in Echtzeit ändern
        final TextView textanzeige = (TextView) findViewById(R.id.textView_notes);
        textanzeige.setText(s);

        if(BuildConfig.DEBUG)
        {
            //Log.d(TAG, "Single Line Widget recognition: " + widget.getText());
        }
        this.editedText.setText(s);
        this.editedText.setIntermediate(intermediate);
    }
}
