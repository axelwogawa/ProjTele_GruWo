package de.gruwo.handschrifterkennung;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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


//TODO: some documentation
/**
 * Created by bi on 24.11.17.
 */

public class MemoActivity extends MySLWTActivity{
    //TODO: make attributes public or private
    //request code
    final int REQUEST_SETUP_BT_CONNECTION = 1;

    private int textSelectionStart, textSelectionEnd;
    private final ArrayList<Integer> ids;
    private float xSet = 80;
    private float ySet = 20;
    private boolean editMode;

    //Array to save the input (text from the user)
    private ArrayList <String> textArray;

    //labels for offers
    private TextView offer1, offer2, offer3;

    protected TextView label;

    //TODO: remove if no longer required
    //um letzte Zeile zurück ins Widget zu holen
//    private String lastRow, otherText,
    private String selectedText;

    //TODO: remove if no longer required
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
        final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout_notes);
        this.widget = (SingleLineWidget) findViewById(R.id.singleLine_widget_notes);
        this.label = (TextView) findViewById(R.id.textView_notes);
        this.offer1 = (TextView) findViewById(R.id.textView_offer1);
        this.offer2 = (TextView) findViewById(R.id.textView_offer2);
        this.offer3 = (TextView) findViewById(R.id.textView_offer3);

        this.textSelectionStart = 0;
        this.textSelectionEnd = 0;


        //TODO: remove if no longer required
//        lastRow = "";
//        otherText = "";

        //initialize textArray
        this.textArray = new ArrayList<>();

        //initialize listView for offers
        updateListOfferNotes();


        //"Zurück"-Button
        //setup the buttons
        backButtonNotes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent serverIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivityForResult(serverIntent, REQUEST_SETUP_BT_CONNECTION);
            }
        });


        //Large parts of the code of the following callback were copied from
        //https://stackoverflow.com/questions/22832123/get-selected-text-from-textview#22833303
        //This sets the behaviour, if text inside the text label (over the widget) is selected
        label.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Remove the "select all" option
                menu.removeItem(android.R.id.selectAll);
                // Remove the "cut" option
                menu.removeItem(android.R.id.cut);
                // Remove the "copy all" option
                menu.removeItem(android.R.id.copy);
                menu.removeItem(android.R.id.shareText);
                return true;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.add(0, 0, 0, "Zum Bearbeiten auswählen");
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {}

            //set behaviour on menu item "Zum Bearbeiten auswählen" click
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (editMode){
                    //abort another selection, if some selected text is already being edited
                    Toast.makeText(MemoActivity.this, "Zuerst bearbeiteten Text übernehmen",
                            Toast.LENGTH_LONG).show();
                    return false;
                }

                //get selection indices and content
                int min = 0;
                int max = label.getText().length();
                if (label.isFocused()) {
                    textSelectionStart = label.getSelectionStart();
                    textSelectionEnd = label.getSelectionEnd();
                }
                textSelectionStart = Math.max(min, textSelectionStart);
                textSelectionEnd = Math.min(max, textSelectionEnd);
                selectedText = String.valueOf(label.getText().subSequence(textSelectionStart,
                        textSelectionEnd));
                mode.finish();
                return true;
            }
        });


        //"Speichern"-Button: Save text from label and widget to memo-file, clear both
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if(editedText.getText().equals("") && label.getText().equals("")){
                    Toast.makeText(MemoActivity.this, "Es erfolgte keine Eingabe.",
                            Toast.LENGTH_LONG).show();
                }else if(textArray.size() <= 10) {
                    String memoString = saveMemoArray(label);
                    saveMemoFile(memoString);
                    createNewMemoButton(textArray.size()-1, rl, scrollView, relative);

                    //TODO: remove if no longer required
                    //label-Instanzen zurücksetzen
//                    lastRow="";
//                    otherText="";

                    //clear the widget (otherwise the old text is used)
                    editedText.setText("");
                    widget.clear();
                    label.setText("");
                    selectedText = "";
                    textSelectionStart = 0;
                    textSelectionEnd = 0;
                    editMode = false;
                    updateListOfferNotes();
                }else{
                    Toast.makeText(MemoActivity.this, "Leider kein Speicher mehr frei.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        //"Übernehmen"-Button: Send text from widget to label as new line, clear widget
        saveBetweenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //TODO: remove if no longer required
//                String text="";
                if (editMode){
                    String newText = String.valueOf(label.getText());
                    newText = newText.substring(0, textSelectionStart) + widget.getText()
                            + newText.substring(textSelectionStart+1);
                    label.setText(newText);
                } else if(String.valueOf(label.getText()).equals("")){
                    //inherit the text from the widget (SingleLineWidget) to the label above the
                    // widget
//                    label.setText(widget.getText());
                    label.append(widget.getText());
//                    lastRow = widget.getText();
                }else{
                    label.append("\n"+widget.getText());

                    //TODO: remove if no longer required
//                    text = String.valueOf(label.getText());
//                    otherText = text;

                    //neue Zeile hinzufügen
//                    text = text + " " + widget.getText();
//                    label.setText(text);
//                    lastRow = widget.getText();

                }

                editedText.setText("");
                widget.clear();
                selectedText = "";
                textSelectionStart = 0;
                textSelectionEnd = 0;
                editMode = false;
                updateListOfferNotes();
            }
        });


        //"Alles löschen"-Button: Clear widget and label
        clearNotesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //clear EditText and delete the insert
                editedText.setText("");
                widget.clear();
                label.setText("");
                selectedText = "";
                textSelectionStart = 0;
                textSelectionEnd = 0;
                editMode = false;
                updateListOfferNotes();

            }
        });


        //"←"-Button: delete character left to the cursor in MyScript SingleLineWidget
        deleteNotesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                removeCharacter(widget);
            }
        });


        //"Leerzeichen"-Button: Insert whitespace at cursor position in MyScript SingleLineWidget
        blankNotesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                insertWhitespace(widget);
            }
        });


        //"Zurückholen"_Button: retrieve selected text from text label (over widget) back to widget
        holdBackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //TODO: remove if no longer required
//                if(lastRow.equals("")){
                if (editMode){
                    //abort retrieval procedure, if some selected text is already being edited
                    Toast.makeText(MemoActivity.this, "Nur einmal Zurückholen möglich.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    //block text selection and retrieval
                    editMode = true;

                    //get text from label to be edited
                    String editText = "";
                    if (selectedText.equals("")){
                        //if no text selected: retrieve complete last line
                        editText = String.valueOf(label.getText());
                        textSelectionStart = editText.lastIndexOf('\n')+1;
                        editText = editText.substring(textSelectionStart);
                        textSelectionEnd = textSelectionStart + editText.length();
                    } else {
                        //if text selected: retrieve it
                        editText = selectedText;
                    }

                    //replace selected text in the label by "_" as long as it's being edited
                    String newText = String.valueOf(label.getText());
                    newText = newText.substring(0, textSelectionStart) + "_" +
                            newText.substring(textSelectionEnd);
                    label.setText(newText);

                    //set text in widget
                    widget.setText(editText);

                    //TODO: remove if no longer required
                    //clear label
//                    label.setText(otherText);
                    //nur einmal zurückholen möglich
//                    lastRow="";
                }
            }
        });


        //"Eingabe"_Button: switch to MainActivity
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


        //"Notizen"-Button: switch to MemoActivity
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


        //TODO: short description
        //change tab -> user press the insert button
        insertButtonNotes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //save the old data from the activity (don't create a new one)
                onBackPressed();
            }
        });


        //read existing memo files
        readMemoFiles();

        //create button for each memo that was found
        for (int i = 0; i < this.textArray.size(); i++){
            createNewMemoButton(i, rl, scrollView, relative);
        }
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }


    //TODO: remove if no longer required
   /* @Override
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
*/


    @Override
    public void onTextChanged(SingleLineWidgetApi widget, String s, boolean intermediate){
        super.onTextChanged(widget, s, intermediate);
        //update listOffer
        updateListOfferNotes();
    }


    @Override
    public void onSingleTapGesture(SingleLineWidgetApi widget, int index){
        super.onSingleTapGesture(widget, index);
        updateListOfferNotes();
    }


    //TODO: siehe MySLTWActivity updateListOfferNotes()
    /**
     * This method is called when the listview with offers has to be updated.
     */
    private void updateListOfferNotes(){
        //add 3 elements to the arrayListLastItem
        if(editedText.getText().equals("")){
           offer1.setText("");
           offer2.setText("");
           offer3.setText("");
        }else if(getCandidateStrings(this.widget).size() == 3){
            offer1.setText(getCandidateStrings(this.widget).get(0));
            offer2.setText(getCandidateStrings(this.widget).get(1));
            offer3.setText(getCandidateStrings(this.widget).get(2));
        }else if(getCandidateStrings(this.widget).size() == 2){
            offer1.setText(getCandidateStrings(this.widget).get(0));
            offer2.setText(getCandidateStrings(this.widget).get(1));
        }else if(getCandidateStrings(this.widget).size() == 1){
            offer1.setText(getCandidateStrings(this.widget).get(0));
        }
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
        textArray.add(memoString);
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
        String memoName = "memo" + this.textArray.size() + ".txt";
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
                    "Memo file saved as " + memoName, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Memo file could not be saved!",
                    Toast.LENGTH_LONG).show();
        }

        return isSuccessfullySaved;
    }


    //TODO add info to scrollView and relative (what's the difference between rl and relative?)
    /**
     * This method creates a new button for a saved memo and labels the button with the memo index.
     * The memo index is an integer ascending as a new memo is being saved.
     *
     * @param n          index of the text entry in {@code textArray}
     * @param rl         the relative layout the button is to be placed in
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
        if(n > 0){
            ySet = ySet + 110;
        }
        btn.setX(xSet);
        btn.setY(ySet);

        //set the colours of the button
        btn.setBackgroundColor(Color.LTGRAY);
        btn.setTextColor(Color.BLACK);
        btn.setText("Memo " + (n+1));

        //set OnClickListener
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Button btn = (Button) findViewById(view.getId());
                String s = String.valueOf(btn.getText());
                String substring = s.substring(s.length()-1);
                int position = Integer.parseInt(substring)-1;

//                Toast.makeText(MemoActivity.this, textArray.get(position),
//                        Toast.LENGTH_SHORT).show();
                label.setText(textArray.get(position));
                editedText.setText("");
                widget.clear();
                selectedText = "";
                textSelectionStart = 0;
                textSelectionEnd = 0;
                editMode = false;
                updateListOfferNotes();
            }
        });
        //add button to view
        rl.addView(btn);
        rl.invalidate();

        scrollView.invalidate();
        relative.invalidate();

        return true;
    }


    /**
     * what happens when someone click on the textview below the singleLineWidget
     * @param v
     */
    public void onClick(View v) {
        String newWord = "";

            if(v.getId() == R.id.textView_offer1){
                Toast.makeText(MemoActivity.this, offer1.getText(), Toast.LENGTH_SHORT).show();
                newWord = String.valueOf(offer1.getText());

                //change the word in the widget
                this.replaceWord(this.widget, newWord);

            }else if(v.getId() == R.id.textView_offer2){
                Toast.makeText(MemoActivity.this, offer2.getText(), Toast.LENGTH_SHORT).show();
                newWord = String.valueOf(offer2.getText());

                //change the word in the widget
                this.replaceWord(this.widget, newWord);

            }else if(v.getId() == R.id.textView_offer3){
                Toast.makeText(MemoActivity.this, offer3.getText(), Toast.LENGTH_SHORT).show();
                newWord = String.valueOf(offer3.getText());

                //change the word in the widget
                this.replaceWord(this.widget, newWord);
            }else{
                Toast.makeText(MemoActivity.this, "Kein Label angeklickt.",
                        Toast.LENGTH_SHORT).show();
            }
    }

}
