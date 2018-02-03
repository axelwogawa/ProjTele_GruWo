package de.gruwo.handschrifterkennung;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import de.gruwo.handschrifterkennung.business.hwr.EditedText;

/**
 * Created by bi on 24.11.17.
 */

public class MemoActivity extends MySLWTActivity{
    //request code
    final int REQUEST_SETUP_BT_CONNECTION = 1;

    private int numberEntries;
    private final ArrayList<Integer> ids;// = new ArrayList<>();

    private float xSet = 80;
    private float ySet = 20;

    //to show the items of the list
    private ListView listViewOfferNotes;
    private ArrayList<String> arrayListOfferNotes = new ArrayList<>();
    private ArrayAdapter adapterOfferNotes = null;

    //Array to save the input (text from the user)
    private ArrayList <String> textArray;
    
    //maximum number of memos to save
//    private int MAX_NUM_MEMOS = 10;


    public MemoActivity (){
        super();
        //set int-values of the ID
        this.ids = new ArrayList<>();
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
//        ids.add(9, R.id.button_Memo11);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        //initialize
        final TextView label = (TextView) findViewById(R.id.textView_notes);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        final RelativeLayout relative = (RelativeLayout) findViewById(R.id.rl_notes);
        final ImageButton deleteNotesButton = (ImageButton) findViewById(R.id.buttonDeleteNotes);
        final Button backButtonNotes = (Button) findViewById(R.id.buttonBackNotes);
        final Button saveButton = (Button) findViewById(R.id.button_save);
        final Button saveBetweenButton = (Button) findViewById(R.id.button_saveBetween);
        final Button clearNotesButton = (Button) findViewById(R.id.buttonClearAllNotes);
        final Button blankNotesButton = (Button) findViewById(R.id.button_blankNotes);
        final Button holdBackButton = (Button) findViewById(R.id.button_holdBack);
        final Button toggleInsertNotes = (Button) findViewById(R.id.toggleInsertNotes);
        final Button toggleMemoNotes = (Button) findViewById(R.id.toggleMemoNotes);
        final Button insertButtonNotes = (Button) findViewById(R.id.toggleInsertNotes);
        listViewOfferNotes = (ListView) findViewById(R.id.listViewOfferNotes);
        this.widget = (SingleLineWidget) findViewById(R.id.singleLine_widget_notes);
        final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout_notes);

        //initialisze the counter
        this.numberEntries = 0;

        //initialize textArray
        this.textArray = new ArrayList<>();

        //initialize listView for offers
        updateListOfferNotes();

        //setup the buttons
        backButtonNotes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent serverIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivityForResult(serverIntent, REQUEST_SETUP_BT_CONNECTION);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if(editedText.getText().equals("") && label.getText().equals("")){
                    Toast.makeText(MemoActivity.this, "Es erfolgte keine Eingabe.", Toast.LENGTH_LONG).show();
                }else if(numberEntries <= 10) {
                    String memoString = saveMemoArray(label);
                    saveMemoFile(memoString);
                    createNewMemoButton(numberEntries, rl, scrollView, relative);

                    //clear the widget (otherwise the old text is used)
                    editedText.setText("");
                    clearContent(widget);
                    updateListOfferNotes();

                }else{
                    Toast.makeText(MemoActivity.this, "Leider kein Speicher mehr frei.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        saveBetweenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //inherit the text from the widget (SingleLineWidget) to the label above the widget
                label.setText(widget.getText());

                editedText.setText("");
                widget.clear();
                updateListOfferNotes();
            }
        });


        clearNotesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //clear EditText and delete the insert
                editedText.setText("");
                widget.clear();
                label.setText("");
                updateListOfferNotes();

            }
        });

        deleteNotesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                removeCharacter(widget);
            }
        });

        blankNotesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                insertWhitespace(widget);
            }
        });


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


        //disable button initially
        toggleMemoNotes.setEnabled(false);
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
        insertButtonNotes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //save the old data from the activity (don't create a new one)
                onBackPressed();
            }
        });

        this.widget = (SingleLineWidget) findViewById(R.id.singleLine_widget_notes);
        
        //read existing memo files
        numberEntries = readMemoFiles();

        //create button for each read memo
        for (int i = 0; i < numberEntries; i++){
            createNewMemoButton(i, rl, scrollView, relative);
        }
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String newWord;

        if(parent.getId() == R.id.listViewOfferNotes){
            if(this.arrayListOfferNotes.get(position).equals("")){
                //do nothing
                Toast.makeText(this,  "kein Eintrag vorhanden", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,  this.arrayListOfferNotes.get(position) + " ausgewählt.", Toast.LENGTH_SHORT).show();
            }


            //change the word in the widget
            newWord = this.arrayListOfferNotes.get(position);
            this.replaceWord(this.widget, newWord);

        }else{
            Toast.makeText(this, "Keine Liste ausgewählt.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onTextChanged(SingleLineWidgetApi widget, String s, boolean intermediate){
        super.onTextChanged(widget, s, intermediate);
        //update listOffer
        updateListOfferNotes();
    }


    /**
     * This method is called when the listview with offers has to be updated.
     */
    private void updateListOfferNotes(){
        //clear the current arrayListOfferNotes
        this.arrayListOfferNotes.clear();

        //add 3 elements to the arrayListLastItem
        if(editedText.getText().equals("")){
            for(int i=0; i<=2; i++){
                this.arrayListOfferNotes.add("");
            }
        }else{
            this.arrayListOfferNotes = getCandidateStrings(this.widget);
        }


        //get reference to view
        this.adapterOfferNotes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.arrayListOfferNotes){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                textView.setTextColor(Color.BLACK);

                return view;
            }
        };

        this.listViewOfferNotes.setAdapter(this.adapterOfferNotes);
        this.listViewOfferNotes.setOnItemClickListener(this);

        this.widget = (SingleLineWidget) findViewById(R.id.singleLine_widget_notes);
    }


    /**
     * This method reads all memo files placed in the documents directory's subdirectory "HWR_memos"
     * named "memo[index].txt" where [index] is an integer within the range [0, 9]. The Strings
     * read from these files are stored consecutively in {@code textArray}. For each of the files
     * a memo button is created to make the memo text available for text manipulation.
     *
     * @return the number of files that were found and read
     */
    public int readMemoFiles(){
        int i = 0;
        String root =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        String memoDirName = "HWR_memos";
        File memoDir = new File(root + "/" + memoDirName);
        if (memoDir.exists() == false){
            Toast.makeText(getApplicationContext(),
                    "No memo files found!",
                    Toast.LENGTH_LONG).show();
            return i;
        }
        String memoName;
        File memo;
        for (int j = 0; j < ids.size(); j++){
            memoName = "memo" + j + ".txt";
            memo = new File(memoDir, memoName);
            if (memo.exists()){
                try {
                    BufferedReader memoReader = new BufferedReader(new FileReader(memo));
                    String line;
                    StringBuilder memoString = new StringBuilder();
                    while ((line = memoReader.readLine()) != null) {
                        memoString.append(line);
                        memoString.append('\n');
                    }
                    textArray.add(i, memoString.toString());
                    i++;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        Toast.makeText(getApplicationContext(),
                i + " memo files found!",
                Toast.LENGTH_LONG).show();
        return i;
    }


    /**
     * Save memo array string.
     *
     * @param label the label holding the current memo text
     * @return the memo string
     */
    public String saveMemoArray(TextView label){
        //add text to text array
        String memoString;
        if(label.getText().equals("")){
            memoString = widget.getText();
        }else if(widget.getText().equals("")){
            memoString = String.valueOf(label.getText());
        }else{
            memoString = String.valueOf(label.getText()) + "\n" + widget.getText();
        }
        textArray.add(numberEntries, memoString);
        return memoString;
    }
    
    
    /**
     * This method saves a complete memo both as a String object and a file. The String object is
     * appended to a text array. The file is placed in a directory called "HWR_memos", which is
     * created (if not already existing) in the system's documents directory. The file is named
     * "memo[index].txt" where [index] is replaced by an ascending integer, starting from 0.
     *
     * @return the {@code true}, if file creation was successful, {@code false} otherwise
     */
    public boolean saveMemoFile(String memoString){
        //save text as a file
        boolean isSuccessfullySaved = false;
        String root =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        String memoDirName = "HWR_memos";
        File memoDir = new File(root + "/" + memoDirName);
        if (memoDir.exists() == false){
            if (memoDir.mkdirs() == false){
                Toast.makeText(getApplicationContext(),
                        "Directory couldn't be created!",
                        Toast.LENGTH_LONG).show();
                return isSuccessfullySaved;
            }
        }
        String memoName = "memo" + numberEntries + ".txt";
        File memo = new File(memoDir, memoName);
        if (memo.exists()){
            Toast.makeText(getApplicationContext(),
                    "Memo file already exists and will be overwritten!",
                    Toast.LENGTH_LONG).show();
            memo.delete();
        }
        try{
            memo.createNewFile();
            FileWriter memoWriter = new FileWriter(memo);
            memoWriter.write(memoString);
            memoWriter.flush();
            memoWriter.close();
            isSuccessfullySaved = true;
            Toast.makeText(getApplicationContext(),
                    "Memo file saved as memo" + numberEntries + ".txt",
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Memo file could not be saved!",
                    Toast.LENGTH_LONG).show();
        }

        //increment counter
        numberEntries++;

        return isSuccessfullySaved;
    }


    //TODO add info to scrollView and relative (what's the difference between rl and relative?)
    /**
     * This method creates a new button for a saved memo and labels the button with the memo index.
     * The memo index is an integer ascending as a new memo is being saved.
     *
     * @param rl         the relative layout the butten is to be placed in
     * @param scrollView ???
     * @param relative   ???
     * @return {@code true}, if the button was successfully created
     */
    public boolean createNewMemoButton(int n,
                                       RelativeLayout rl,
                                       ScrollView scrollView,
                                       RelativeLayout relative){
        //create a new button
        Button btn = new Button(MemoActivity.this);
        btn.setWidth(50);
        btn.setHeight(25);
        btn.setId(ids.get(n));

        //set the place of the button
        //calculate the yPosition of the button
        if(n > 1){
            ySet = ySet + 110;
        }
        btn.setX(xSet);
        btn.setY(ySet);

        //set the colours of the button
        btn.setBackgroundColor(Color.LTGRAY);
        btn.setTextColor(Color.BLACK);
        btn.setText("Memo " + n);

        //set OnClickListener
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Button btn = (Button) findViewById(view.getId());
                String s = String.valueOf(btn.getText());
                String substring = s.substring(s.length()-1);
                int position = Integer.parseInt(substring);

                Toast.makeText(MemoActivity.this, textArray.get(position -1), Toast.LENGTH_SHORT).show();
            }
        });
        //add button to view
        rl.addView(btn);
        rl.invalidate();

        scrollView.invalidate();
        relative.invalidate();

        return true;
    }
    
}
