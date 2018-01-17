package de.gruwo.handschrifterkennung;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.myscript.atk.sltw.SingleLineWidgetApi;
import com.myscript.atk.text.CandidateInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.gruwo.handschrifterkennung.business.hwr.EditedText;
import de.gruwo.handschrifterkennung.business.hwr.WordInfo;
import de.gruwo.handschrifterkennung.certificate.MyCertificate;



public class MainActivity
    extends AppCompatActivity
    implements AdapterView.OnItemClickListener,
        SingleLineWidgetApi.OnConfiguredListener,
        SingleLineWidgetApi.OnTextChangedListener,
        SingleLineWidgetApi.OnSingleTapGestureListener,
        SingleLineWidgetApi.OnPenAbortListener
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

    public static final String TAG = "GruWo_HWR";


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

        //the following code was taken from MyScript Single Line Text Widged documentation
        //url: https://developer.myscript.com/old-docs/atk/2.2/android/text/sltw.html
        //CITATION_START
        this.widget = (SingleLineWidget) findViewById(R.id.singleLine_widget);
        if (!this.widget.registerCertificate(MyCertificate.getBytes()))
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

        //register listeners
        this.widget.setOnConfiguredListener(this);
        this.widget.setOnTextChangedListener(this);
        this.widget.setOnSingleTapGestureListener(this);
        this.widget.setOnPenAbortListener(this);

        // references assets directly from the APK to avoid extraction in application
        // file system
        this.widget.addSearchDir("zip://" + getPackageCodePath() + "!/assets/conf");

        // The configuration is an asynchronous operation. Callbacks are provided to
        // monitor the beginning and end of the configuration process and update the UI
        // of the input method accordingly.
        //
        // "de_DE" references the de_DE bundle name in conf/de_DE.conf file in your assets.
        // "cur_text" references the configuration name in de_DE.conf
        this.widget.configure("de_DE", "cur_text");
        //CITATION_END

        //Set number of alternative word suggestions to 3
        this.widget.setWordCandidateListSize(3);

        //set appearance of the MyScript widget
        float margin = (float)(findViewById(R.id.singleLine_widget).getWidth()*0.4);
        this.widget.setAutoScrollMargin(margin);
        this.widget.setLeftScrollArrowResource(R.drawable.sltw_arrowleft_xml);
        this.widget.setRightScrollArrowResource(R.drawable.sltw_arrowright_xml);
        this.widget.setScrollbarResource(R.drawable.sltw_scrollbar_xml);
        this.widget.setScrollbarBackgroundResource(R.drawable.sltw_scrollbar_background);
        this.widget.setScrollbarMaskResource(R.drawable.sltw_scrollbar_mask);
//        this.widget.setWritingAreaBackgroundResource(R.drawable.sltw_bg_pattern);
        this.widget.setWritingAreaBackgroundColor(-Integer.parseInt("ffffff", 16) +
                Integer.parseInt("f0f0f0", 16));
        this.widget.setCursorResource(R.drawable.sltw_text_cursor_holo_light);
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



//MyScript SingleLineWidget listeners

    /**
     * This method was partially taken from MyScript Single Line Text Widged documentation;
     * url: https://developer.myscript.com/old-docs/atk/2.2/android/text/sltw.html.
     * It's called as soon as the MyScript SingleLineWidget is configured.
     */
    @Override
    public void onConfigured(SingleLineWidgetApi widget, boolean success)
    {
        if(!success)
        {
            Toast.makeText(getApplicationContext(), widget.getErrorString(),
                    Toast.LENGTH_LONG).show();
            Log.e(TAG, "Unable to configure the Single Line Widget: " +
                    widget.getErrorString());
            return;
        }
        Toast.makeText(getApplicationContext(), "Single Line Widget Configured",
                Toast.LENGTH_SHORT).show();
        if(BuildConfig.DEBUG)
            Log.d(TAG, "Single Line Widget configured!");
    }


    /**
     * This method was copied from MyScript Single Line Text Widged documentation;
     * url: https://developer.myscript.com/old-docs/atk/2.2/android/text/sltw.html.
     * It's called as soon as the text recognised by the MyScript SingleLineWidget has changed.
     *
     */
    @Override
    public void onTextChanged(SingleLineWidgetApi widget, String s, boolean intermediate)
    {
        //Textanzeige in einem TextView in Echtzeit
        final TextView displayText = (TextView) findViewById(R.id.textView_display);
        displayText.setText(s);

        this.editedText.setIntermediate(intermediate);
        this.editedText.setText(s);

        widget.setCursorIndex(widget.getText().length());

        Log.i(this.TAG, "#########################################");
        Log.i(this.TAG, "Single Line Widget recognition: " + widget.getText());
        Log.i(this.TAG, "Passed Text: " + s);
        Log.i(this.TAG, "Intermediate: " + intermediate);
        Log.i(this.TAG, "Word List: " + Arrays.toString(this.editedText.getWordArray()));
        Log.i(this.TAG, "Word List Size: " + this.editedText.getWordArray().length);

        Log.i(this.TAG, "Cursor Index: " + new Integer(widget.getCursorIndex()).toString());
        Log.i(this.TAG, "Selection Index: " +
                new Integer(widget.getSelectionIndex()).toString());
    }


    /**
     * This method is called as soon as the MyScript SingleLineWidget recognises a single tap.
     *
     */
    @Override
    public void onSingleTapGesture(SingleLineWidgetApi widget, int index) {
        Log.d(this.TAG, "Single tap gesture detected at index=" + index);
        widget.setCursorIndex(index);
//        this.widget.selectWord();
    }


    /**
     * This method is called as soon as the MyScript SingleLineWidget recognises that the user stops
     * writing (which currently seems not to work properly).
     *
     */
    @Override
    public void onPenAbort(SingleLineWidgetApi widget){
        Log.d(this.TAG, "Pen abort gesture detected. Setting cursor to position " +
                (widget.getText().length()-1));

    }


//some more text manipulation methods

    /**
     * This method returns the most probable HWR results for the current word. The current word is
     * the word where the cursor is currently positioned (i.e. to select a word,
     * the cursor must be positioned in front of the first character or behind the last
     * character or anywhere in between). The length of the returned String list is the number
     * of possible word candidates set via the method
     * {@code SingleLineWidgetAPI#setWordCandidatesListSize(int i)}. The first String in this
     * list is the most probable HWR result, which usually gets printed in the widget text line,
     * as long as no other candidate has been selected.
     *
     * @param widget the MyScript SingleLineWidget
     * @return the list of Strings representing the most probable HWR results for the
     * current word
     */
    public List<String> getCandidateStrings(SingleLineWidgetApi widget){
        CandidateInfo candidates = this.getCurrentCandidateInfo(widget);
        String selectedLabel = candidates.getSelectedLabel();
        Log.d(TAG, "Selected Label: " + selectedLabel);
        List<String> labels = candidates.getLabels();
        Log.d(TAG, "All possible Labels: ");
        for(int i=0; i < labels.size(); i++){
            Log.d(TAG, labels.get(i));
        }
        return labels;
    }


    public CandidateInfo getCurrentCandidateInfo(SingleLineWidgetApi widget){
        int candidateIndex = widget.getCursorIndex();
        if (candidateIndex > 0
                && (candidateIndex == widget.getText().length()
                || widget.getText().charAt(candidateIndex) == ' '
        )
                )
        {
            //if cursor is at a word's end, select this word by decrementing the selection index
            candidateIndex--;
        }
        return widget.getWordCandidates(candidateIndex);
    }


    /**
     * This method removes the character left to the cursor. If the cursor is at the beginning
     * of the text or no text is available, the method returns without any operation.
     *
     * @param widget the MyScript SingleLineWidget
     */
    public void removeCharacter(SingleLineWidgetApi widget){
        int cursorIndex = widget.getCursorIndex();
        if (widget.getText().length() == 0 || cursorIndex == 0)
            return;
        //TODO: check, if indices are set correctly to only remove the character left to the cursor
        widget.replaceCharacters(cursorIndex-1, cursorIndex, null);
    }

    /**
     * This method replaces the current word with an arbitrary String. The current word is the word
     * where the cursor is currently positioned (i.e. to select a word, the cursor must be
     * positioned in front of the first character or behind the last character or anywhere in
     * between). If no text is available, the method returns without any operation.
     *
     * @param widget the MyScript SingleLineWidget
     */
    public void replaceWord(SingleLineWidgetApi widget, String newWord){
        //TODO: check, if indices are set correctly to replace the complete word but nothing else
        CandidateInfo candidates = this.getCurrentCandidateInfo(widget);
        if (candidates.getStart() < 0 || candidates.getEnd() <= candidates.getStart())
            return;
        widget.replaceCharacters(candidates.getStart(), candidates.getEnd(), newWord);
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
