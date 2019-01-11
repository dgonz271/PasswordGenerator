package com.example.gonza.passwordgenerator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.example.gonza.passwordgenerator.MainActivity.ITEM_POSITION;
import static com.example.gonza.passwordgenerator.MainActivity.ITEM_TEXT;

public class SavedPasswords extends AppCompatActivity {

    EditText editItemText;
    int position;
    String passwordItem;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_passwords);

        //variable will hold the password itself
        passwordItem = getIntent().getStringExtra(ITEM_TEXT);
        //setting the password_preview to the password that was clicked on
        ((TextView) findViewById(R.id.password_preview)).setText(passwordItem);

        position = getIntent().getIntExtra(ITEM_POSITION, 0);


        editItemText = (EditText) findViewById(R.id.password_name);



        findViewById(R.id.return_to_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


    }

    public void saveName(View v){
        Intent intent = new Intent();

        if(editItemText.getText().toString().length() == 0 )
            intent.putExtra(ITEM_TEXT, passwordItem );
        else
            intent.putExtra(ITEM_TEXT, passwordItem + "\t\t( " + editItemText.getText().toString() + " )" );
        intent.putExtra(ITEM_POSITION, position);

        setResult(RESULT_OK, intent);
        finish();
    }


}
