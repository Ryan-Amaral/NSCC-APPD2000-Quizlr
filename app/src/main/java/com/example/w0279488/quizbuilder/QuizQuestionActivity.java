package com.example.w0279488.quizbuilder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class QuizQuestionActivity extends Activity {

    private final static String FILE_NAME = "TermsAndDefs.txt";

    private static boolean isComplete = false;

    // create objects for the properties in the activity
    RadioGroup rdBtnGrpA;
    Button btnExit;
    Button btnNextQuestion;
    TextView txtName;
    TextView txtScoreVal;
    TextView txtQuestion;
    RadioButton rdBtnA1;
    RadioButton rdBtnA2;
    RadioButton rdBtnA3;
    RadioButton rdBtnA4;


    int nextQEnabledColor = 0xff249824;// color of enabled next question button
    int nextQDisabledColor = 0xff839e83;// color of disabled next question button

    // values to hold on to
    String username; // the name the use entered
    int curQuestion = 0; // start at 0
    int maxQuestion; // the amount of questions
    int curCorrect = 0; // the amount of questions the user got correct
    int answerIndex; // the index of the current answer
    int selectedIndex; // the index of the current selected answer

    // hash map to store questions and their answers
    HashMap<String, String> defsAndTerms;
    // array list to store all the answers jumbled
    ArrayList<String> jumbledTerms;
    // array list to store all the questions jumbled
    ArrayList<String> jumbledDefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_question);

        // set the controls
        rdBtnGrpA = (RadioGroup)findViewById(R.id.rdBtnGrpA);
        btnExit = (Button)findViewById(R.id.btnExit);
        btnNextQuestion = (Button)findViewById(R.id.btnNextQuestion);
        txtName = (TextView)findViewById(R.id.txtName);
        txtScoreVal = (TextView)findViewById(R.id.txtScoreVal);
        txtQuestion = (TextView)findViewById(R.id.txtQuestion);
        rdBtnA1 = (RadioButton)findViewById(R.id.rdBtnA1);
        rdBtnA2 = (RadioButton)findViewById(R.id.rdBtnA2);
        rdBtnA3 = (RadioButton)findViewById(R.id.rdBtnA3);
        rdBtnA4 = (RadioButton)findViewById(R.id.rdBtnA4);

        txtQuestion.setMovementMethod(new ScrollingMovementMethod());

        // create click listener for exit
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go back to main menu/activity
                finish(); // stop this activity
            }
        });

        btnNextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if the selected index is the answer index, add one to answer score
                if(!isComplete) {
                    if (answerIndex == selectedIndex) {
                        curCorrect++;
                        // show toast telling user they are correct
                        Toast.makeText(getApplicationContext(), "Correct! +1 point!", Toast.LENGTH_SHORT).show();
                    } else {
                        // show toast telling user they are incorrect
                        Toast.makeText(getApplicationContext(), "Incorrect!", Toast.LENGTH_SHORT).show();
                    }
                }

                if(curQuestion + 1 < maxQuestion) {
                    // go to next question
                    curQuestion++; // increment question and load the next
                    loadQuestion(curQuestion, jumbledDefs, jumbledTerms, defsAndTerms);
                }else{
                    isComplete = true;
                    // unless this is last question, go to results
                    Intent i = new Intent(QuizQuestionActivity.this, ResultsActivity.class); //create intent object
                    Bundle extras = new Bundle(); //create bundle object
                    //fill bundle
                    extras.putString(Keys.USER_NAME_KEY, username);
                    extras.putInt(Keys.MAX_SCORE_KEY, maxQuestion);
                    extras.putInt(Keys.USER_SCORE_KEY, curCorrect);
                    i.putExtras(extras);
                    startActivity(i);
                }
            }
        });

        rdBtnGrpA.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // set the next question button enabled
                btnNextQuestion.setEnabled(true); // enable next question button
                btnNextQuestion.setBackgroundColor(nextQEnabledColor); // change color of button

                View radioButton = rdBtnGrpA.findViewById(checkedId);
                selectedIndex = rdBtnGrpA.indexOfChild(radioButton);
            }
        });

        String[][] tmpDefsAndTerms = getQsAndAsFromFile(); // get values from file
        defsAndTerms = new HashMap<String, String>();
        jumbledTerms = new ArrayList<String>(); // initialize arraylist
        jumbledDefs = new ArrayList<String>();

        maxQuestion = tmpDefsAndTerms.length;// set the max quastion (last question number)

        // put all terms and defs in array list and hash
        for(int i = 0; i < tmpDefsAndTerms.length; i++){
            defsAndTerms.put(tmpDefsAndTerms[i][0], tmpDefsAndTerms[i][1]); // create key value association in hash map
            jumbledDefs.add(tmpDefsAndTerms[i][0]);
            jumbledTerms.add(tmpDefsAndTerms[i][1]);
        }
        Collections.shuffle(jumbledDefs); // the defs are now shuffled

        // unload the bundle
        Bundle extras=getIntent().getExtras();
        if(extras != null)//if bundle has content
        {
            username = extras.getString(Keys.USER_NAME_KEY);
            txtName.setText(username);
        }

        loadQuestion(curQuestion, jumbledDefs, jumbledTerms, defsAndTerms); // load the first question
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quiz_question, menu);
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

    // Loads the current if there is one
    public void loadQuestion(int qNum, ArrayList<String> questions, ArrayList<String> answers,
                             HashMap<String, String> questionAnswers){
        screenRefresh();

        // set the question text view
        txtQuestion.setText(questions.get(qNum));
        Random rand = new Random();// the radio button that will have correct answer
        int ansIndex = rand.nextInt(4);
        answerIndex = ansIndex;

        int getIndex = 0; // the index to select answers

        Collections.shuffle(answers); // shuffle answers every time
        for(int i = 0; i < 4; i++){
            // help from: http://stackoverflow.com/questions/5096329/get-the-array-of-radiobuttons-in-a-radiogroup-in-android
            View rdGrpChild = rdBtnGrpA.getChildAt(i);
            RadioButton rdBtn = (RadioButton)rdGrpChild;

            // set this one to correct answer
            if(i == ansIndex){
                rdBtn.setText(questionAnswers.get(questions.get(qNum)));
            }else{
                // if answer is same as actual answer get other
                if(answers.get(getIndex).equals(questionAnswers.get(questions.get(qNum)))){
                    getIndex++;
                    rdBtn.setText(answers.get(getIndex));
                }else{ // take this answer
                    rdBtn.setText(answers.get(getIndex));
                }
                getIndex++;
            }
        }

        questionAnswers.remove(questions.get(qNum));
    }

    // refreshes the screen on new question
    public void screenRefresh(){
        rdBtnGrpA.clearCheck(); // remove selected radio button
        btnNextQuestion.setEnabled(false); // disable next question button
        btnNextQuestion.setBackgroundColor(nextQDisabledColor); // change color of button
        txtScoreVal.setText(Integer.toString(curCorrect)); // update the users score
        // change the text in next question button on last question
        if(curQuestion + 1 == maxQuestion){
            btnNextQuestion.setText(R.string.strFinish);
        }
        txtQuestion.scrollTo(0, 0); // set the question scroll to top
    }

    // gets the terms and defs from a text file separated by '~'s
    String[][] getQsAndAsFromFile(){
        String[][] tmpDefsAndTerms = new String[0][0];
        ArrayList<String> tmpDefs = new ArrayList<String>();
        ArrayList<String> tmpTerms = new ArrayList<String>();
        // try catch because potential io error
        try {
            // create the input stream object
            InputStream iStream = getAssets().open(FILE_NAME);
            BufferedReader bReader = new BufferedReader(new InputStreamReader(iStream));
            String line; // variable to hold one line at a time, split at each ~
            // on each line definition is first, term is second
            while((line = bReader.readLine()) != null){
                String[] dAndT = line.split("~"); // split the line
                // put values in defs and terms array lists
                tmpDefs.add(dAndT[0]);
                tmpTerms.add(dAndT[1]);
            }
            // put array list values in 2d string array
            tmpDefsAndTerms = new String[tmpDefs.size()][2];
            for(int x = 0; x < tmpDefs.size(); x++){
                tmpDefsAndTerms[x][0] = tmpDefs.get(x);
                tmpDefsAndTerms[x][1] = tmpTerms.get(x);
            }

            iStream.close();
        }catch(IOException e){
            Log.w("warning", "unable to get information from file.");
            e.printStackTrace();
        }

        return tmpDefsAndTerms;
    }

}
