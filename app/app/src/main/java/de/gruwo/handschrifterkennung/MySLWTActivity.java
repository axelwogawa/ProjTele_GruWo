package de.gruwo.handschrifterkennung;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.myscript.atk.sltw.SingleLineWidget;
import com.myscript.atk.sltw.SingleLineWidgetApi;
import com.myscript.atk.text.CandidateInfo;

import java.util.ArrayList;
import java.util.List;

import de.gruwo.handschrifterkennung.business.hwr.EditedText;
import de.gruwo.handschrifterkennung.certificate.MyCertificate;


/**
 * The super class of MainActivity and MemoActivity containing common functionality: Widget
 * config, widget gesture listeners, word candidates handling, text manipulation methods.
 */

public class MySLWTActivity
        extends AppCompatActivity
        implements AdapterView.OnItemClickListener
        ,SingleLineWidgetApi.OnConfiguredListener
        ,SingleLineWidgetApi.OnTextChangedListener
        ,SingleLineWidgetApi.OnSingleTapGestureListener
        ,SingleLineWidgetApi.OnPenAbortListener
        ,SingleLineWidgetApi.OnSelectGestureListener
        ,SingleLineWidgetApi.OnEraseGestureListener
        ,SingleLineWidgetApi.OnInsertGestureListener
        ,SingleLineWidgetApi.OnJoinGestureListener
        ,SingleLineWidgetApi.OnLongPressGestureListener
        ,SingleLineWidgetApi.OnOverwriteGestureListener
        ,SingleLineWidgetApi.OnUnderlineGestureListener
//        ,SingleLineWidgetApi.OnPenDownListener
//        ,SingleLineWidgetApi.OnPenMoveListener
//        ,SingleLineWidgetApi.OnPenUpListener
{
    private ArrayList<String> arrayListLastItem = new ArrayList<String>();
    protected SingleLineWidgetApi widget;
    public EditedText editedText;

    private boolean dontMoveCursor;

    public static final String TAG = "GruWo_HWR";



//Android onEvent methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.editedText = new EditedText();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        this.configureWidget(this.widget);
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
     * handle pressing button with alert dialog if connected(non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

        //margin setting must wait until now, because earlier the widget width is still 0.0 (due to
        //unfinished layout process)
        float margin = (float)(((SingleLineWidget) widget).getWidth()*0.2);
        this.widget.setAutoScrollMargin(margin);

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
        Log.i(this.TAG, "Single Line Widget recognition: " + widget.getText());
        Log.d(this.TAG, "Cursor Index: " + new Integer(widget.getCursorIndex()).toString());

        this.editedText.setIntermediate(intermediate);
        this.editedText.setText(s);

        if (this.dontMoveCursor == false){
            widget.setCursorIndex(widget.getText().length());
        }
        if (this.editedText.getIntermediate() == false) {
            this.dontMoveCursor = false;
        }
    }


    /**
     * This method is called as soon as the MyScript SingleLineWidget recognises a single tap. It
     * moves the cursor to the tap location.
     *
     */
    @Override
    public void onSingleTapGesture(SingleLineWidgetApi widget, int index) {
        Log.d(this.TAG, "Single tap gesture detected at index=" + index);
        Toast.makeText(getApplicationContext(), "Single tap gesture detected at index=" +
                    index, Toast.LENGTH_SHORT).show();
        widget.setCursorIndex(index);
    }


    /**
     * This method is called as soon as the MyScript SingleLineWidget recognises that the user stops
     * writing (which currently seems not to work properly).
     *
     * @see <a href="https://developer.myscript.com/old-docs/atk/2.2/android/_/sltw/ref/javadoc/index.html>MyScript
     * SingleLineWidget Docs</a>
     *
     */
    @Override
    public void onPenAbort(SingleLineWidgetApi widget){
        String message = "Pen abort gesture detected";
        Log.d(this.TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    /**
     * This method is called as soon as the MyScript SingleLineWidget recognises an erase gesture
     * (which is a stroke from left to right through parts of the recognised text or striking it
     * out with a zig zag line).
     *
     * @see <a href="https://developer.myscript.com/old-docs/atk/2.2/android/_/sltw/ref/javadoc/index.html>MyScript
     * SingleLineWidget Docs</a>
     *
     */
    @Override
    public  void onEraseGesture(SingleLineWidgetApi w, int start, int end){
        String message = "Erase gesture detected from index " + start + " to " + end;
        Log.d(this.TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    /**
     * This method is called as soon as the MyScript SingleLineWidget recognises an underline
     * gesture.
     *
     * @see <a href="https://developer.myscript.com/old-docs/atk/2.2/android/_/sltw/ref/javadoc/index.html>MyScript
     * SingleLineWidget Docs</a>
     *
     */
    @Override
    public  void onUnderlineGesture(SingleLineWidgetApi w, int start, int end){
        String message = "Underline gesture detected from index " + start + " to " + end;
        Log.d(this.TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    /**
     * This method is called as soon as the MyScript SingleLineWidget recognises a select gesture
     * (which is drawing a rectangle around parts of the recognised text).
     *
     * @see <a href="https://developer.myscript.com/old-docs/atk/2.2/android/_/sltw/ref/javadoc/index.html>MyScript
     * SingleLineWidget Docs</a>
     *
     */
    @Override
    public  void onSelectGesture(SingleLineWidgetApi w, int start, int end){
        String message = "Select gesture detected from index " + start + " to " + end;
        Log.d(this.TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    /**
     * This method is called as soon as the MyScript SingleLineWidget recognises an insert gesture
     * (which is a stroke from up to down anywhere within the recognised text).
     *
     * @see <a href="https://developer.myscript.com/old-docs/atk/2.2/android/_/sltw/ref/javadoc/index.html>MyScript
     * SingleLineWidget Docs</a>
     *
     */
    @Override
    public  void onInsertGesture(SingleLineWidgetApi w, int index){
        String message = "Insert gesture detected at index " + index;
        Log.d(this.TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    /**
     * This method is called as soon as the MyScript SingleLineWidget recognises a join gesture
     * (which is a stroke from down to up anywhere within the recognised text).
     *
     * @see <a href="https://developer.myscript.com/old-docs/atk/2.2/android/_/sltw/ref/javadoc/index.html>MyScript
     * SingleLineWidget Docs</a>
     *
     */
    @Override
    public  void onJoinGesture(SingleLineWidgetApi w, int start, int end){
        String message = "Join gesture detected from index " + start + " to " + end;
        Log.d(this.TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    /**
     * This method is called as soon as the MyScript SingleLineWidget recognises a long press
     * gesture.
     *
     * @see <a href="https://developer.myscript.com/old-docs/atk/2.2/android/_/sltw/ref/javadoc/index.html>MyScript
     * SingleLineWidget Docs</a>
     *
     */
    @Override
    public  void onLongPressGesture(SingleLineWidgetApi w, int index){
        String message = "Long press gesture detected at index " + index;
        Log.d(this.TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    /**
     * This method is called as soon as the MyScript SingleLineWidget recognises an overwrite
     * gesture (which is writing on top of already recognised text).
     *
     * @see <a href="https://developer.myscript.com/old-docs/atk/2.2/android/_/sltw/ref/javadoc/index.html>MyScript
     * SingleLineWidget Docs</a>
     *
     */
    @Override
    public  void onOverwriteGesture(SingleLineWidgetApi w, int start, int end){
        String message = "Overwrite gesture detected from index " + start + " to " + end;
        Log.d(this.TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
    public ArrayList<String> getCandidateStrings(SingleLineWidgetApi widget){
        CandidateInfo candidates = this.getCurrentCandidateInfo(widget);
        String selectedLabel = candidates.getSelectedLabel();
        ArrayList<String> arrayListLabels = new ArrayList<>();
        Log.d(TAG, "Selected Label: " + selectedLabel);
        List<String> labels = candidates.getLabels();
        Log.d(TAG, "Alternative candidates: ");
        for(int i=0; i < labels.size(); i++){
            Log.d(TAG, labels.get(i));
            arrayListLabels.add(labels.get(i));

        }

        return arrayListLabels;
    }


    /**
     * This method determines the currently selected word (to select a word, the cursor must be
     * positioned in front of the first character or behind the last character or anywhere in
     * between) and returns the {@code CandidateInfo} of this word.
     *
     * @param widget the MyScript SingleLineWidget
     * @return the {@code CandidateInfo}
     * @see <a href="https://developer.myscript.com/old-docs/atk/2.2/android/_/sltw/ref/javadoc/index.html>MyScript
     * SingleLineWidget Docs</a>
     */
    public CandidateInfo getCurrentCandidateInfo(SingleLineWidgetApi widget){
        int candidateIndex = Math.min(widget.getCursorIndex(), widget.getText().length());
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
        widget.replaceCharacters(cursorIndex-1, cursorIndex, null);
        widget.setCursorIndex(widget.getCursorIndex()-1);
        this.dontMoveCursor = true;
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
        CandidateInfo candidates = this.getCurrentCandidateInfo(widget);
        if (candidates.getStart() < 0 || candidates.getEnd() <= candidates.getStart())
            return;
        widget.replaceCharacters(candidates.getStart(), candidates.getEnd(), newWord);
        widget.setCursorIndex(candidates.getStart()+newWord.length());
        this.dontMoveCursor = true;
    }


    /**
     * Insert whitespace character at the current cursor position of the MyScript SingleLineWidget.
     *
     * @param widget the MyScript SingleLineWidget
     */
    public void insertWhitespace(SingleLineWidgetApi widget){
        widget.replaceCharacters(widget.getCursorIndex(), widget.getCursorIndex(), " ");
        widget.setCursorIndex(widget.getCursorIndex()+1);
        this.dontMoveCursor = true;
    }


    /**
     * Insert string at the current cursor position of the MyScript SingleLineWidget.
     *
     * @param widget the MyScript SingleLineWidget
     * @param s      the String to be inserted
     */
    public void insertString(SingleLineWidgetApi widget, String s){
        //insert the new word
        widget.replaceCharacters(widget.getCursorIndex(), widget.getCursorIndex(), s);
        widget.setCursorIndex(widget.getCursorIndex()+s.length());
        this.dontMoveCursor = true;
    }



//MyScript widget settings

    /**
     * Configures the MyScript SingleLineTextWidget, which includes certificate check, setting
     * listeners, HWR resources and appearance.
     *
     * @param widget the MyScript SingleLineWidget
     */
    public void configureWidget(SingleLineWidgetApi widget){
        //large parts of the following code were taken from MyScript Single Line Text Widged
        //documentation
        //url: https://developer.myscript.com/old-docs/atk/2.2/android/text/sltw.html
        //CITATION_START
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
        this.widget.setOnEraseGestureListener(this);
        this.widget.setOnUnderlineGestureListener(this);
        this.widget.setOnSelectGestureListener(this);
        this.widget.setOnInsertGestureListener(this);
        this.widget.setOnJoinGestureListener(this);
        this.widget.setOnLongPressGestureListener(this);
        this.widget.setOnOverwriteGestureListener(this);
//        this.widget.setOnPenDownListener(this);
//        this.widget.setOnPenMoveListener(this);
//        this.widget.setOnPenUpListener(this);

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

        //Set behaviour of the MyScript widget
        this.widget.setWordCandidateListSize(3);
        int delayWhenNearlyEmpty = 2000;    //default: 3200
        int delayWhenNearlyFull = 1500;     //default: 600
        this.widget.setAutoScrollDelays(delayWhenNearlyEmpty, delayWhenNearlyFull);
        delayWhenNearlyEmpty = 1000;    //default: 1000
        //this value is bigger, because the user might want to change text in the very beginning
        //(e.g. placing i-dot) after finishing writing -> takes longer time, if widget is nearly
        //full:
        delayWhenNearlyFull = 1500;     //default: 3600
        this.widget.setAutoTypesetDelays(delayWhenNearlyEmpty, delayWhenNearlyFull);

        //set appearance of the MyScript widget
        this.widget.setLeftScrollArrowResource(R.drawable.sltw_arrowleft_xml);
        this.widget.setRightScrollArrowResource(R.drawable.sltw_arrowright_xml);
        this.widget.setScrollbarResource(R.drawable.sltw_scrollbar_xml);
        this.widget.setScrollbarBackgroundResource(R.drawable.sltw_scrollbar_background);
        this.widget.setScrollbarMaskResource(R.drawable.sltw_scrollbar_mask);
        this.widget.setWritingAreaBackgroundColor(-Integer.parseInt("ffffff", 16) +
                Integer.parseInt("f0f0f0", 16));
        this.widget.setCursorResource(R.drawable.sltw_text_cursor_holo_light);
    }
}
