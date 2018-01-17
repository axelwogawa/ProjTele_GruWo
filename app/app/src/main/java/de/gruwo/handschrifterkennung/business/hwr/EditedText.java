package de.gruwo.handschrifterkennung.business.hwr;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is intended to be instanciated by a handwriting recognition widget. It holds the
 * result and state of the handwritten text being input into the widget.
 */
public class EditedText {

    private String text;
    private String[] wordArray;
    private List<WordInfo> wordList;
    private Boolean intermediate;
    public static final String TAG = "GruWo_HWR";


    /**
     * Instantiates a new {@code EditedText} object.
     */
    public EditedText() {
        this.text = new String();
        this.wordList = new LinkedList<>();
        this.intermediate = false;
    }


    /**
     * Sets {@code text} attribute to empty String and {@code wordArray} to {@code null}.
     */
    public void clearText(){
        this.setText("");
        this.setWordArray(null);
        this.setWordList(null);
    }


    /**
     * Gets the whole text sequence currently held by the related text widget as one String.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }


    /**
     * Sets text. If the text is no longer intermediate, the {@code wordArray} attribute is updated
     * by splitting the {@code text} sequence into words and passing a {@code String[]} array
     * holding the discrete words.
     *
     * @param text the text
     * @see EditedText#getIntermediate()
     * @see EditedText#getWordArray()
     */
    public void setText(String text) {
        this.text = text;
        if(this.intermediate == false){
            this.setWordArray(text.split("[ ]")); //\s,.
        }
    }


    /**
     * Gets word list. This is a {@code List} containing one {@link WordInfo} object per word.
     *
     * @return the word list
     */
    public List<WordInfo> getWordList() {
        return wordList;
    }


    /**
     * Sets word list. This is a {@code List} containing one {@link WordInfo} object per word.
     *
     * @param wordList the word list
     */
    public void setWordList(List<WordInfo> wordList) {
        this.wordList = wordList;
    }


    /**
     * Gets an array of words readily recognised by the related text widget.
     *
     * @return the string [ ]
     */
    public String[] getWordArray() {
        return wordArray;
    }


    /**
     * Sets word array and updates the word list.
     *
     * @param wordArray the word array
     */
    public void setWordArray(String[] wordArray) {
        this.wordArray = wordArray;

        int i = 0, j = 0;
        int lenDiff = 0, lenDiffSgn = 0;
        boolean updateStartIndices = false, sizeModified = true;
        while(i < Math.max(this.wordArray.length, this.wordList.size())){
            if (sizeModified || i == 0) {
                lenDiff = this.wordArray.length - this.wordList.size();
                if (lenDiff < 0) {
                    //list has more elements than array
                    lenDiffSgn = -1;
                    Log.i(this.TAG, "Word deletion detected");
                } else if (lenDiff == 0) {
                    //list and array have same length
                    lenDiffSgn = 0;
                    Log.i(this.TAG, "Word change detected");
                } else {
                    //list has less elements than array
                    lenDiffSgn = 1;
                    Log.i(this.TAG, "Word addition detected");
                }
                sizeModified = false;
            }

            switch(lenDiffSgn) {
                case -1: //word deleted
                    if(i<this.wordArray.length
                        && this.wordList.get(i).getText().equals(this.wordArray[i])) {
                        //word to be deleted not found yet
                        j++;
                    } else {
                        //word to be deleted found
                        Log.i(this.TAG, "Deleting word at index " + i + ": " +
                                        this.wordList.get(i).getText());
                        this.wordList.remove(i);
                        updateStartIndices = true;
                        sizeModified = true;
                    }
                break;

                case 0: //word changed
                    if(this.wordList.get(i).getText().equals(this.wordArray[i])) {
                        //word to be changed not found yet
                    } else {
                        //word to be changed found
                        Log.i(this.TAG, "Changing word at index " + i + " from: " +
                                this.wordList.get(i).getText() + " to: " + this.wordArray[i]);
                        this.wordList.get(i).setText(this.wordArray[i]);
                        updateStartIndices = true;
                    }
                    j++;
                    break;

                case 1: //word added
                default:
                    if(i<this.wordList.size()
                            && this.wordList.get(i).getText().equals(this.wordArray[i])) {
                        //word to be added not found yet
                    } else {
                        //word to be added found
                        Log.i(this.TAG, "Adding word at index " + i + ": " + this.wordArray[i]);
                        this.wordList.add(i, new WordInfo(this.wordArray[i],
                                i == 0 ? i : this.wordList.get(i-1).getEndIndex()+1));
                        updateStartIndices = true;
                        sizeModified = true;
                    }
                    j++;
                    break;
            }

            if (updateStartIndices) {
                if (i>0
                    && (this.wordList.get(i-1).getEndIndex()+1) == this.wordList.get(i).getStartIndex()) {
                    //current start index matching previous end index
                    Log.i(this.TAG, "Start index matching at index " + i);
                } else if (i == 0) {
                    //start index of the first word is not 0
                    this.wordList.get(i).setStartIndex(0);
                    Log.i(this.TAG, "Changing start index of word " +
                            this.wordList.get(i).getText() + " to 0");
                } else {
                    //current start index not matching previous end index
                    this.wordList.get(i).setStartIndex(this.wordList.get(i-1).getEndIndex()+1);
                    Log.i(this.TAG, "Changing start index of word " +
                            this.wordList.get(i).getText() + " to " +
                            this.wordList.get(i).getStartIndex());
                }
            }

            i = j;
        }
    }


    /**
     * Returns whether the recognition result returned by {@link EditedText#getText()} is still
     * intermediate which means the handwriting recognition engine is still processing the digital
     * ink input into the text widget (i.e. the MyScript {@code SigleLineTextWidget} related to this
     * {@link EditedText} object).  If the result is still intermediate, the recognition result may
     * drastically change until the recognition process has finished.
     *
     * @return {@code true}, if the handwriting recognition result is still intermediate,
     *         {@code false} otherwise
     */
    public Boolean getIntermediate() {
        return intermediate;
    }


    /**
     * Sets intermediate.
     *
     * @param intermediate the intermediate
     */
    public void setIntermediate(Boolean intermediate) {
    this.intermediate = intermediate;
    }
}



