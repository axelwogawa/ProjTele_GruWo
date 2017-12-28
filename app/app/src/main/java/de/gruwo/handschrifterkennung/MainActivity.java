package de.gruwo.handschrifterkennung;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
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


import com.myscript.atk.sltw.SingleLineWidget;
import com.myscript.atk.sltw.SingleLineWidgetApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.gruwo.handschrifterkennung.business.hwr.EditedText;
import de.gruwo.handschrifterkennung.certificate.MyCertificate;



public class MainActivity
    extends AppCompatActivity
    implements AdapterView.OnItemClickListener,
        SingleLineWidgetApi.OnConfiguredListener,
        SingleLineWidgetApi.OnTextChangedListener
    {

    //TODO: make attributes public or private
    private ListView listViewLastItem;
    ArrayList<String> arrayListLastItem = new ArrayList<String>();
    //ArrayAdapter deklarieren -> initialisiert wird später (line 115)
    ArrayAdapter<String> adapterLastItem = null;

    private ListView listViewOffer;
    ArrayList<String> arrayListOffer = new ArrayList<>();
    ArrayAdapter adapterOffer = null;

    private SingleLineWidgetApi widget;

    private EditedText editedText;

    //request code
    final int REQUEST_SETUP_BT_CONNECTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.editedText = new EditedText();

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

                //insert in array to show in list
                arrayListLastItem.add(0, String.valueOf(editedText.getText()));
                //letztes Element aus der ArrayList entfernen (damit die Anzeige schöner ist)
                //Nachteil: nicht alle Elemente werden für immer gespeichert, sondern nur die letzten vier
                arrayListLastItem.remove(arrayListLastItem.size() -1);

                //update text in adapter's items
                adapterLastItem.notifyDataSetChanged();

                //clear EditText and delete the insert
                editedText.clearText();
                //clear the widget (ansonsten wird der alte Text weiterhin verwendet)
                widget.clear();
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

        //the following code was taken from MyScript Single Line Text Widged documentation
        //url: https://developer.myscript.com/old-docs/atk/2.2/android/text/sltw.html
        //CITATION_START
        widget = (SingleLineWidget) findViewById(R.id.singleLine_widget);
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
    public void onItemClick(AdapterView<?> lV, View view, int pos, long id){
        Toast.makeText(this, "Eintrag " + arrayListLastItem.get(pos) + " ausgewählt",
        Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * This method was copied from MyScript Single Line Text Widged documentation;
     * url: https://developer.myscript.com/old-docs/atk/2.2/android/text/sltw.html
     */
    @Override
    protected void onDestroy()
    {
        widget.setOnTextChangedListener(null);
        widget.setOnConfiguredListener(null);

        super.onDestroy();
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

        //Textanzeige in einem TextView in Echtzeit
        final TextView displayText = (TextView) findViewById(R.id.textView_display);
        displayText.setText(s);

        if(BuildConfig.DEBUG)
        {
            //Log.d(TAG, "Single Line Widget recognition: " + widget.getText());
        }
        this.editedText.setText(s);
        this.editedText.setIntermediate(intermediate);
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
