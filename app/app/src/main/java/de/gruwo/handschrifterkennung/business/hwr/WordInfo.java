package de.gruwo.handschrifterkennung.business.hwr;

/**
 * This class contains information about a specific word currently managed by the HWR app,
 * especially about its position and length.
 * Created by axel on 10.01.18.
 */
public class WordInfo {
    private String text;
    private int startIndex;
//    private int endIndex;


    /**
     * Instantiates a new Word info.
     *
     * @param text       the text
     * @param startIndex the start index
     */
    public WordInfo(String text, int startIndex){
        this.text = text;
        this.startIndex = startIndex;
//        this.endIndex = startIndex + text.length();
    }


    /**
     * Gets text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }


    /**
     * Sets text.
     *
     * @param text the text
     */
    public void setText(String text) {
        this.text = text;
    }


    /**
     * Gets start index. The start index is the index of the word's
     * first character in the widget string. The widget string is the character sequence that the
     * myScript SingleLineTextWidget currently shows in its upper line (including whitespaces),
     * i.e. the current result of the handwriting recognition. If for example the widget currently
     * holds the text "Hello world", then the start index of "Hello" would be 0, the start index of
     * "world" would be 6.
     *
     * @return the start index
     */
    public int getStartIndex() {
        return startIndex;
    }


    /**
     * Sets start index to the passed parameter value. The start index is the index of the word's
     * first character in the widget string. The widget string is the character sequence that the
     * myScript SingleLineTextWidget currently shows in its upper line (including whitespaces),
     * i.e. the current result of the handwriting recognition. If for example the widget currently
     * holds the text "Hello world", then the start index of "Hello" would be 0, the start index of
     * "world" would be 6.
     *
     * @param startIndex the start index
     */
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
//        this.endIndex = startIndex+this.getText().length();
    }


    /**
     * Returns the end index of the word, i.e. the index of the next whitespace character. Each
     * word spans the character positions [startIndex, endIndex-1].
     *
     * @return the end index
     * @see WordInfo#getStartIndex()
     */
    public int getEndIndex() {
        return startIndex+this.getText().length();
    }


//    /**
//     * Sets the end index of the word, i.e. the index of the next whitespace character. So the
//     * word spans the character positions [startIndex, endIndex-1].
//     *
//     * @param endIndex the end index
//     */
//    public void setEndIndex(int endIndex) {
//        this.endIndex = endIndex;
//    }
}
