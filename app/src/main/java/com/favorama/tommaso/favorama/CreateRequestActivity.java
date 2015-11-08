package com.favorama.tommaso.favorama;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class CreateRequestActivity extends ActionBarActivity {

    EditText title;
    EditText description;
    RadioButton free;
    RadioButton paid;
    EditText amount;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title = (EditText) findViewById(R.id.editText7);
        description = (EditText) findViewById(R.id.editText8);
        free = (RadioButton) findViewById(R.id.radioButton);
        paid = (RadioButton) findViewById(R.id.radioButton2);
        amount = (EditText) findViewById(R.id.editText9);
        submit = (Button) findViewById(R.id.button8);
    }

}
