package com.favorama.tommaso.favorama;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends ActionBarActivity {

    EditText email;
    EditText username;
    EditText password;
    EditText password2;
    Button register;
    List<NameValuePair> inputData;
    String inputEmail;
    String inputUsername;
    String inputPassword;
    String inputPassword2;
    Intent intent;
    String serverResponse;
    Button goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        email = (EditText) findViewById(R.id.editText2);
        username = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText3);
        password2 = (EditText) findViewById(R.id.editText4);
        register = (Button) findViewById(R.id.button);
        goToLogin = (Button) findViewById(R.id.button4);
        inputData = new ArrayList<>();
        intent = new Intent(this, LoginActivity.class);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEmail = email.getText().toString();
                inputUsername = username.getText().toString();
                inputPassword = password.getText().toString();
                inputPassword2 = password2.getText().toString();
                if (inputPassword.equals(inputPassword2)) {
                    inputData.add(new BasicNameValuePair("email", inputEmail));
                    inputData.add(new BasicNameValuePair("username", inputUsername));
                    inputData.add(new BasicNameValuePair("password", inputPassword));
                    try{
                        serverResponse = new sendData().execute(inputData).get().toString();
                    } catch (Exception e){
                        Log.e("log_tag3", "FAILED TO GET RESPONSE FROM sendData");
                    }
                    if (serverResponse.equals("Thank you for registering!")) {
                        startActivity(intent);
                    }
                    Toast.makeText(getApplicationContext(), serverResponse, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong input, try again!", Toast.LENGTH_LONG).show();
                }
            }
        });
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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
