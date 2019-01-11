package com.example.gonza.passwordgenerator;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> passwordList;
    ArrayAdapter<String> passwordListAdapter;
    ListView passwordListView;
    String password;
    //code used to identify our SavedPasswords Activity
    public final static int EDIT_REQUEST_CODE = 100;
    //keys that will be used to pass data to other activity
    public final static String ITEM_TEXT = "itemText";
    public final static String ITEM_POSITION = "itemPosition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readItems();
        passwordListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, passwordList);
        passwordListView = findViewById(R.id.password_list);
        passwordListView.setAdapter(passwordListAdapter);
        //assigning password to be empty indicates that nothing has been generated yet
        password = "";

        setupListViewListener();


        findViewById(R.id.generate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = generate();
                ((TextView) (findViewById(R.id.password_text))).setText("Your generated password is: " + password);
            }
        });


        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //if password.equals("") is true, then that means nothing has been generated
                if (!password.equals("")) {
                    passwordList.add(password);
                    Toast.makeText(MainActivity.this, "Successfully saved password", Toast.LENGTH_SHORT).show();
                    passwordListAdapter.notifyDataSetChanged();
                    writeItems();
                } else {
                    Toast.makeText(MainActivity.this, "Please generate a password", Toast.LENGTH_SHORT).show();
                }

            }
        });

        findViewById(R.id.show_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.show_pass).setVisibility(View.INVISIBLE);
                findViewById(R.id.hide_pass).setVisibility(View.VISIBLE);

                findViewById(R.id.showSaved_text).setVisibility(View.INVISIBLE);
                findViewById(R.id.hideSaved_text).setVisibility(View.VISIBLE);

                findViewById(R.id.details).setVisibility(View.INVISIBLE);
                findViewById(R.id.generate_button).setVisibility(View.INVISIBLE);
                findViewById(R.id.password_text).setVisibility(View.INVISIBLE);
                findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
                findViewById(R.id.password_list).setVisibility(View.VISIBLE);
                findViewById(R.id.instructions).setVisibility(View.VISIBLE);

            }
        });
        findViewById(R.id.hide_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.show_pass).setVisibility(View.VISIBLE);
                findViewById(R.id.hide_pass).setVisibility(View.INVISIBLE);

                findViewById(R.id.showSaved_text).setVisibility(View.VISIBLE);
                findViewById(R.id.hideSaved_text).setVisibility(View.INVISIBLE);

                findViewById(R.id.details).setVisibility(View.VISIBLE);
                findViewById(R.id.generate_button).setVisibility(View.VISIBLE);
                findViewById(R.id.password_text).setVisibility(View.VISIBLE);
                findViewById(R.id.save_button).setVisibility(View.VISIBLE);
                findViewById(R.id.password_list).setVisibility(View.INVISIBLE);
                findViewById(R.id.instructions).setVisibility(View.INVISIBLE);

            }
        });


    }

    public static String generate() {
        String mix = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String num = mix.substring(0, 10);
        String upper = mix.substring(10, 36);
        String lower = mix.substring(36);

        ArrayList<Character> password = new ArrayList<Character>();
        for (int i = 0; i < 3; i++) {
            password.add(num.charAt((int) (Math.random() * num.length())));
            password.add(lower.charAt((int) (Math.random() * lower.length())));
            password.add(upper.charAt((int) (Math.random() * upper.length())));
        }

        return returnList(randomize(randomize(randomize(password))));//randomizes 3 times

    }

    public static String returnList(ArrayList<Character> array) {
        String list = "";
        for (int i = 0; i < array.size(); i++)
            list += array.get(i);
        return list;
    }

    public static ArrayList<Character> randomize(ArrayList<Character> array) {
        int random = (int) (Math.random() * array.size());
        for (int i = 0; i < array.size() - 1; i++) {
            char temp = array.get(i);
            array.set(i, array.get(random));
            array.set(random, temp);
            random = (int) (Math.random() * array.size());
        }

        return array;
    }

    //-------
    private void setupListViewListener() {
        Log.i("SavedPasswords", "Setting up listener on a list view");
        passwordListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("SavedPasswords", "Item removed from list: " + position);
                passwordList.remove(position);
                passwordListAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });

        passwordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //creating a new activity
                Intent intent = new Intent(MainActivity.this, SavedPasswords.class);
                //passing the data to SavedPasswords
                intent.putExtra(ITEM_TEXT, passwordList.get(position));
                intent.putExtra(ITEM_POSITION, position);
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE) {
            //updatedPass will hold the string from SavedPasswords
            String updatedPass = data.getExtras().getString(ITEM_TEXT);
            //newPosition is the original position of the updatedPass
            int newPosition = data.getExtras().getInt(ITEM_POSITION);
            //setting the previous listed item to the newly updated password
            passwordList.set(newPosition, updatedPass);
            //notifying adapter that the list model is updated
            passwordListAdapter.notifyDataSetChanged();
            //persist changed model of passwords
            writeItems();
            Toast.makeText(this, "Successfully named item", Toast.LENGTH_SHORT).show();

        }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "SavedPasswords.txt");
    }

    private void readItems() {
        try {
            passwordList = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("SavedPasswords", "Error reading file", e);
            passwordList = new ArrayList<>();
        }
    }

    private void writeItems() {
        try {
            FileUtils.writeLines(getDataFile(), passwordList);
        } catch (IOException e) {
            Log.e("SavedPasswords", "Error writing file", e);
            passwordList = new ArrayList<>();
        }
    }
}
