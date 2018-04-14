package com.example.kcroz.joggr;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;


public class EnterValues_DatabaseHelper extends Activity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_enter_values);
        dbHelper = new DatabaseHelper(this);
    }

    /*
    public void saveConV(View v) {
        dbHelper.insertRunValues(getName(), getAge());
        dbHelper.insertPointValues(getName(), getAge());
    }

    public void saveRunExec(View v) {
        dbHelper.saveRunsExec(getName(), getAge());
        dbHelper.savePointsExec(getName(), getAge());
    }
*/

    //start the next activity when the next button is clicked
    /*public void next(View V)
    {
        Intent i = new Intent(EnterValues.this, ViewData.class);
        startActivity(i);
    }*/




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_enter_values, menu);
        return true;
    }
}