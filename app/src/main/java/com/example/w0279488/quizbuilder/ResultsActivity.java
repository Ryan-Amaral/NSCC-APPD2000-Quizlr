package com.example.w0279488.quizbuilder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ResultsActivity extends AppCompatActivity {

    // create references to controls that change
    TextView txtBigName;
    TextView txtYourScoreVal;
    TextView txtYourScoreMax;
    ProgressBar prgrsBrScore;
    TextView txtWellDone;
    Button btnFinish;

    // values to hold on to
    String username = ""; // the name the use entered
    int userScore = 1; // the users final score
    int maxScore = 1; // the amount of questions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // assign controls
        txtBigName = (TextView)findViewById(R.id.txtBigName);
        txtYourScoreVal = (TextView)findViewById(R.id.txtYourScoreVal);
        txtYourScoreMax = (TextView)findViewById(R.id.txtYourScoreMax);
        prgrsBrScore = (ProgressBar)findViewById(R.id.prgrsBrScore);
        txtWellDone = (TextView)findViewById(R.id.txtWellDone);
        btnFinish = (Button)findViewById(R.id.btnFinish);

        // on click listener button to go to main
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResultsActivity.this, MainActivity.class); //create intent object
                Bundle extras = new Bundle(); //create bundle object
                extras.putString(Keys.USER_NAME_KEY, txtBigName.getText().toString()); //fill bundle
                i.putExtras(extras);
                startActivity(i);
            }
        });

        // unload the bundle
        Bundle extras=getIntent().getExtras();
        if(extras != null)//if bundle has content
        {
            username = extras.getString(Keys.USER_NAME_KEY);
            userScore = extras.getInt(Keys.USER_SCORE_KEY);
            maxScore = extras.getInt(Keys.MAX_SCORE_KEY);
        }

        // set name
        txtBigName.setText(username);

        // set user score
        txtYourScoreVal.setText(Integer.toString(userScore));

        // set max score
        txtYourScoreMax.setText("/" + Integer.toString(maxScore));

        // set progress
        int progress = (int)(((float)userScore / (float)maxScore) * 100);
        prgrsBrScore.setProgress(progress);
        // show message based on progress
        if(progress == 100) {
            txtWellDone.setText(R.string.strPerfect);
        }
        else if(progress < 40){
            txtWellDone.setText(R.string.strStudyHarder);
        }
        else if(progress < 75){
            txtWellDone.setText(R.string.strNiceTry);
        }
        else if(progress < 100){
            txtWellDone.setText(R.string.strWellDone);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
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
}
