package com.example.w0279488.quizbuilder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {

    // create the controls that need interacting
    EditText edtTxtName;
    Button btnBeginQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtTxtName = (EditText)findViewById(R.id.edtTxtName);
        btnBeginQuiz = (Button)findViewById(R.id.btnBeginQuiz);

        // set the focus listener on edit text to get keyboard
        edtTxtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    System.out.println("in listener");
                    // show the keyboard
                }
            }
        });

        // button listener to go to quiz
        btnBeginQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("in button");

                // if name is valid, save it and go to quiz question activity
                if (isValidName(edtTxtName.getText().toString())) {
                    Intent i = new Intent(MainActivity.this, QuizQuestionActivity.class); //create intent object
                    Bundle extras = new Bundle(); //create bundle object
                    extras.putString(Keys.USER_NAME_KEY, edtTxtName.getText().toString()); //fill bundle
                    i.putExtras(extras);
                    startActivity(i);
                } else {
                    // show toast telling user they are incorrect
                    Toast.makeText(getApplicationContext(), "Letters Only! 12 character max!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // unload the bundle
        Bundle extras=getIntent().getExtras();
        if(extras != null)//if bundle has content
        {
            edtTxtName.setText(extras.getString(Keys.USER_NAME_KEY));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Checks if the input name is valid format
    public boolean isValidName(String name){
        boolean isValid = false;

        // at-lest one character, no more than 12
        if(name.matches("[a-zA-Z]{1,12}")){
            isValid = true;
        }

        return isValid;
    }
}
