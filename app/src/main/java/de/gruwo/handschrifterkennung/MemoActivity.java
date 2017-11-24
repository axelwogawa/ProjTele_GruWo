package de.gruwo.handschrifterkennung;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by bi on 24.11.17.
 */

public class MemoActivity extends Activity{

    //request code
    final int REQUEST_SETUP_BT_CONNECTION = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);


        final Button backButton = (Button) findViewById(R.id.buttonBackNotes);
        //on click call the BluetoothActivity to choose a listed device
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent serverIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivityForResult(serverIntent, REQUEST_SETUP_BT_CONNECTION);
            }
        });
    }
}
