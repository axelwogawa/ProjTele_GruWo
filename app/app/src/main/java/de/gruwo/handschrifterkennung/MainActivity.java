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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.myscript.atk.sltw.SingleLineWidget;
import com.myscript.atk.sltw.SingleLineWidgetApi;

import java.util.ArrayList;


//TODO: add some description, if you like
/**
 * The type Main activity.
 */
public class MainActivity extends MySLWTActivity{

    private ListView listViewLastItem;
    private ArrayList<String> arrayListLastItem = new ArrayList<String>();
    private ArrayAdapter<String> adapterLastItem = null;

    private ListView listViewOffer;
    private ArrayList<String> arrayListOffer = new ArrayList<>();
    private ArrayAdapter adapterOffer = null;

    //request code
    private final int REQUEST_SETUP_BT_CONNECTION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize
        final Button toggleButton = (Button) findViewById(R.id.toggleInsert);
        final Button toggleMemo = (Button) findViewById(R.id.toggleMemo);
        final Button inheritButton = (Button) findViewById(R.id.buttonInherit);
        final Button memoButton = (Button) findViewById(R.id.toggleMemo);
        final ImageButton deleteButton = (ImageButton) findViewById(R.id.buttonDelete);
        final Button clearButton = (Button) findViewById(R.id.buttonClearAll);
        final Button blankButton = (Button) findViewById(R.id.buttonBlank);
        this.listViewLastItem = (ListView) findViewById(R.id.listViewLastItem);
        this.listViewOffer = (ListView) findViewById(R.id.listViewOffer);
        this.widget = (SingleLineWidget) findViewById(R.id.singleLine_widget);


        //"Eingabe"_Button: switch to MainActivity
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


        //"Notizen"-Button: switch to MemoActivity
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


        //"Übernehmen"-Button: Send text from widget to "zuletzt verwendet" list (and to other
        // application in the future)
        inheritButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if(editedText.getText().equals("")){
                    Toast.makeText(MainActivity.this, "Es gab keine Eingabe.", Toast.LENGTH_LONG).show();
                }else {
                    //insert in array to show in list
                    arrayListLastItem.add(0, String.valueOf(editedText.getText()));
                    //delete the last item of the arraylist
                    //disadvantage: items not fixed saved for a long time
                    arrayListLastItem.remove(arrayListLastItem.size() - 1);

                    //update text in adapter's items
                    adapterLastItem.notifyDataSetChanged();

                    //clear the widget (ansonsten wird der alte Text weiterhin verwendet)
                    widget.clear();

                    updateListOffer();
                }
            }
        });


        //TODO: add short description
        memoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent serverIntent = new Intent(getApplicationContext(),MemoActivity.class);
                startActivityForResult(serverIntent, REQUEST_SETUP_BT_CONNECTION);
            }
        });


        //"←"-Button: delete character left to the cursor in MyScript SingleLineWidget
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                removeCharacter(widget);
            }
        });


        //"Alles löschen"-Button: Clear widget and label
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                widget.clear();
            }
        });


        //"Leerzeichen"-Button: Insert whitespace at cursor position in MyScript SingleLineWidget
        blankButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                insertWhitespace(widget);
            }
        });


        // add 12 items to arrayListLastItem
        for(int i=0; i<=11; i++){
            this.arrayListLastItem.add("");
        }


        this.adapterLastItem = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.arrayListLastItem){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                textView.setTextColor(Color.LTGRAY);

                return view;
            }
        };

        this.listViewLastItem.setAdapter(this.adapterLastItem);
        this.listViewLastItem.setOnItemClickListener(this);

        updateListOffer();
    }


    //TODO: A little documentation
    @Override
    public void onItemClick(AdapterView<?> lV, View view, int pos, long id){
        String newWord;

        if(lV.getId() == R.id.listViewLastItem){
            if(this.arrayListLastItem.get(pos).equals("")){
                //do nothing
                Toast.makeText(this,  "kein Eintrag vorhanden", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,  this.arrayListLastItem.get(pos) + " ausgewählt.", Toast.LENGTH_SHORT).show();
            }


            //change the word in the widget
            newWord = this.arrayListLastItem.get(pos);
            this.insertString(this.widget, newWord);

        }else if(lV.getId() == R.id.listViewOffer){
            if(this.arrayListOffer.get(pos).equals("")){
                //do nothing
                Toast.makeText(this,  "kein Eintrag vorhanden", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,  this.arrayListOffer.get(pos) + " ausgewählt.", Toast.LENGTH_SHORT).show();
            }


            //change the word in the widget
            newWord = this.arrayListOffer.get(pos);
            this.replaceWord(this.widget, newWord);

        }else{
            Toast.makeText(this, "Keine Liste ausgewählt.", Toast.LENGTH_SHORT).show();
        }
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


    @Override
    public void onTextChanged(SingleLineWidgetApi widget, String s, boolean intermediate){
        super.onTextChanged(widget, s, intermediate);
        //add elements to arraylistoffer
        this.arrayListOffer = getCandidateStrings(widget);

        //TODO: Warum wird das selbe in updateListOffer() unmittelbar danach nochmal ausgeführt?
        this.adapterOffer = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.arrayListOffer);
        this.listViewOffer.setAdapter(this.adapterOffer);
        this.listViewOffer.setOnItemClickListener(this);

        if(editedText.getText().equals("")){
            updateListOffer();
        }

    }


    @Override
    public void onSingleTapGesture(SingleLineWidgetApi widget, int index){
        super.onSingleTapGesture(widget, index);
        updateListOffer();
    }


    //TODO: siehe MySLTWActivity updateListOfferNotes()
    /**
     * This method is called when the listview with offers has to be updated.
     */
    private void updateListOffer(){
        //update the listview with offers

        //add elements to arraylistoffer
        for(int i= 0; i<3; i++){
            this.arrayListOffer.add("");
        }

        //TODO: Reicht es nicht, Adapter einmal in onCreate zu definieren und zu setzen statt dies
        // jedes Mal neu zu tun?
        this.adapterOffer = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.arrayListOffer){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                textView.setTextColor(Color.LTGRAY);

                return view;
            }
        };
        this.listViewOffer.setAdapter(this.adapterOffer);
        this.listViewOffer.setOnItemClickListener(this);
    }
}


