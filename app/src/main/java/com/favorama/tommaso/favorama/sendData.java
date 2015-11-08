package com.favorama.tommaso.favorama;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class sendData extends AsyncTask<List<NameValuePair>,Void,String> {
    @Override
    protected String doInBackground(List<NameValuePair>... inputData) {
        String serverResponse;
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://128.61.104.207:8165/favorama_registration.php");
            httpPost.setEntity(new UrlEncodedFormEntity(inputData[0]));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity responseEntity = httpResponse.getEntity();
            is = responseEntity.getContent();
        } catch (Exception e){
            Log.e("log_tag1", "CONNECTION FAILED in Registration Activity: " + e);
        }
        try {
            try {
                String line;
                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e){
            Log.e("log_tag3", "ERROR IN PARSING RESPONSE: " + e);
        }
        serverResponse = sb.toString();
        return serverResponse;
    }
}
