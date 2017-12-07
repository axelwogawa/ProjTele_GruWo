package de.gruwo.handschrifterkennung.business.hwr;

/**
 * This class holds the intermediate result of the handwritten text being processed.
 */

public class EditedText {

    private String text;

    private Boolean intermediate;


    public EditedText() {
        this.text = new String();
        this.intermediate = false;
    }


    public void clearText(){
        setText("");
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getIntermediate() {
        return intermediate;
    }

    public void setIntermediate(Boolean intermediate) {
    this.intermediate = intermediate;
    }
}



