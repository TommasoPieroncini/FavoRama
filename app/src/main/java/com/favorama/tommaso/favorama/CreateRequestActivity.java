package com.favorama.tommaso.favorama;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CreateRequestActivity extends ActionBarActivity {

    EditText titleInput;
    EditText descriptionInput;
    RadioButton free;
    RadioButton paid;
    EditText amountInput;
    Button submit;
    List<NameValuePair> favorInfo;
    String title;
    String description;
    String amount = "";
    String freeOrNot;
    Bundle recoverBundle;
    Intent recoverIntent;
    String x;
    String y;
    String username;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.e("log_tag7", "in create request activity");
        recoverIntent = getIntent();
        recoverBundle = recoverIntent.getExtras();
        if (recoverBundle != null){
            x = recoverBundle.getString("x");
            y = recoverBundle.getString("y");
            username = recoverBundle.getString("username");
        }
        titleInput = (EditText) findViewById(R.id.editText7);
        descriptionInput = (EditText) findViewById(R.id.editText8);
        free = (RadioButton) findViewById(R.id.radioButton);
        paid = (RadioButton) findViewById(R.id.radioButton2);
        amountInput = (EditText) findViewById(R.id.editText9);
        amountInput.setVisibility(View.GONE);
        submit = (Button) findViewById(R.id.button8);
        favorInfo = new ArrayList<>();
        intent = new Intent(this, AnswerRequestActivity.class);
        free.toggle();
        paid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                amountInput.setVisibility(View.GONE);
            }
        });
        free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                amountInput.setVisibility(View.VISIBLE);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleInput.getText().toString();
                description = descriptionInput.getText().toString();
                if (free.isChecked()) {
                    freeOrNot = "true";
                } else if (paid.isChecked()) {
                    freeOrNot = "false";
                    amount = amountInput.getText().toString();
                }
                favorInfo.add(new BasicNameValuePair("title", title));
                favorInfo.add(new BasicNameValuePair("description", description));
                favorInfo.add(new BasicNameValuePair("amount", amount));
                favorInfo.add(new BasicNameValuePair("freeOrNot", freeOrNot));
                favorInfo.add(new BasicNameValuePair("username", username));
                favorInfo.add(new BasicNameValuePair("x", x));
                favorInfo.add(new BasicNameValuePair("y", y));
                try{
                    String response = new sendFavorInfo().execute(favorInfo).get();
                } catch(Exception e) {
                    Log.e("log_tag_error", "failed to send favor data" + e.toString());
                }
                Toast.makeText(getApplicationContext(), "Your request has been submitted!", Toast.LENGTH_LONG);
                intent.putExtra("username",username);
                startActivity(intent);
                finish();
            }
        });
        descriptionInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // if edittext has 10chars & this is not called yet, add new line
                if (descriptionInput.getText().length()%20 == 0) {
                    descriptionInput.append("\n");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_request, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings3) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

class sendFavorInfo extends AsyncTask<List<NameValuePair>, Void, String> {

    @Override
    protected String doInBackground(List<NameValuePair>... favorInfo){
        InputStream is = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://128.61.104.207:8165/favorama_add_favor.php");
            httpPost.setEntity(new UrlEncodedFormEntity(favorInfo[0]));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("log_tag_good", "finished sending favorData");
        } catch (Exception e){
            Log.e("log_tag","Failed to send favorInfo to database: " + e);
        }
        return "ok";
    }
}
