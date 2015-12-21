package com.favorama.tommaso.favorama;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AnswerRequestActivity extends FragmentActivity{

    private GoogleMap mMap;
    Intent intent;
    ImageView newFavorButton;
    Intent recoverIntent;
    String username;
    String password;
    Bundle recoverBundle;
    String provider;
    Button logOut;
    Intent logOutIntent;
    Bundle logOutBooleanBundle;
    Bundle sendToCreateRequest;
    Boolean camera = false;
    ArrayList[] markersData;
    ArrayList<String> ids = new ArrayList<>();
    ArrayList<String> usernames = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();
    ArrayList<Float> amounts = new ArrayList<>();
    ArrayList<Boolean> freeOrNots = new ArrayList<>();
    ArrayList<Double> xs = new ArrayList<>();
    ArrayList<Double> ys = new ArrayList<>();
    Boolean favorDataDetected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_request);
        StrictMode.enableDefaults();
        provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        CheckEnableGPS(provider);
        recoverIntent = getIntent();
        recoverBundle = recoverIntent.getExtras();
        if(recoverBundle != null) {
            username = recoverBundle.getString("username");
        }
        newFavorButton = (ImageView) findViewById(R.id.imageView1);
        newFavorButton.setVisibility(View.GONE);
        logOut = (Button) findViewById(R.id.button5);
        intent = new Intent(this,CreateRequestActivity.class);
        logOutIntent = new Intent(this,LoginActivity.class);
        logOutBooleanBundle = new Bundle();
        sendToCreateRequest = new Bundle();
        camera = true;
        setUpMapIfNeeded();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        try{
            markersData = new retrieveMarkersData().execute().get();
        } catch (Exception e){
            Log.e("log_tag_error", "Failed to retrieve markers data" + e.toString());
        }
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File authData = new File(getFilesDir(), "FavoRama_Authentification_Data.txt");
                Boolean infoDeleted = authData.delete();
                logOutBooleanBundle.putBoolean("infoDeleted", infoDeleted);
                logOutIntent.putExtras(logOutBooleanBundle);
                startActivity(logOutIntent);
                finish();
            }
        });
        if (markersData != null){
            favorDataDetected = true;
            ids = markersData[0];
            usernames = markersData[1];
            titles = markersData[2];
            descriptions = markersData[3];
            amounts = markersData[4];
            freeOrNots = markersData[5];
            xs = markersData[6];
            ys = markersData[7];

            for (int i = 0; i < ids.size(); i++){
                String snippet;
                BitmapDescriptor color;
                String title = "By " + usernames.get(i) + ". Title: " + titles.get(i);
                Double x = xs.get(i);
                Double y = ys.get(i);
                if (freeOrNots.get(i) == true){
                    snippet = "Free favor! Be nice to somebody, gratitude is your reward!";
                    color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                } else {
                    snippet = "Reward: $" + amounts.get(i);
                    color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
                }
                mMap.addMarker(new MarkerOptions().position(new LatLng(x,y)).title(title).snippet(snippet).icon(color));
            }
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(final Location location) {
            mMap.setMyLocationEnabled(true);
            if (camera) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                camera = false;
                mMap.animateCamera(cameraUpdate);
            }
            newFavorButton.setVisibility(View.VISIBLE);
            newFavorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                    if (provider.contains("gps")) {
                        Location location = mMap.getMyLocation();
                        Double x = location.getLatitude();
                        Double y = location.getLongitude();
                        sendToCreateRequest.putString("x",x.toString());
                        sendToCreateRequest.putString("y",y.toString());
                        sendToCreateRequest.putString("username",username);
                        intent.putExtras(sendToCreateRequest);
                        startActivity(intent);
                        finish();
                    } else {
                        CheckEnableGPS(provider);
                    }
                }
            });
            logOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File authData = new File(getFilesDir(), "FavoRama_Authentification_Data.txt");
                    Boolean infoDeleted = authData.delete();
                    logOutBooleanBundle.putBoolean("infoDeleted", infoDeleted);
                    logOutIntent.putExtras(logOutBooleanBundle);
                    startActivity(logOutIntent);
                }
            });
        }
    };

    private void CheckEnableGPS(String provider){
        if(provider.contains("gps")){
            //GPS Already Enabled
            Log.e("log_tag12", "GPS Already Enabled");
        }else{
            //GPS not enabled, prompt user
            final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            final AlertDialog.Builder builder =  new AlertDialog.Builder(this);
            final String message = "FavoRama needs GPS activated to function properly. Do you want to open GPS setting?";
            builder.setMessage(message)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    startActivity(intent);
                                    d.dismiss();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    d.cancel();
                                }
                            });
            builder.create().show();
        }

    }

    //TODO: Add settings and put "log out" in the settings spinner
    //TODO: Add "profile" to settings (profile pic --> database holds pictures



}

class retrieveMarkersData extends AsyncTask<Void, Void, ArrayList[]>{

    ArrayList<String> ids = new ArrayList<>();
    ArrayList<String> usernames = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();
    ArrayList<String> amounts = new ArrayList<>();
    ArrayList<Boolean> freeOrNots = new ArrayList<>();
    ArrayList<Double> xs = new ArrayList<>();
    ArrayList<Double> ys = new ArrayList<>();

    @Override
    protected ArrayList[] doInBackground(Void... Void){
        ArrayList[] data = new ArrayList[]{ids,usernames,titles,descriptions,amounts,freeOrNots,xs,ys};

        InputStream is = null;
        String result = "";

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://128.61.104.207:8165/favorama_retrieve_favor_data.php");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (Exception e) {
            Log.e("log_tag_error","Failed to reach retrieve_favor_data.php" + e.toString());
        }

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null){
                sb.append(line + "/n");
            }
            is.close();

            result = sb.toString();
        }
        catch(Exception e){
            Log.e("log_tag2", "Error converting result".toString());
        }

        try{
            JSONArray jArray = new JSONArray(result);

            for(int i = 0; i < jArray.length(); i++){
                JSONObject json = jArray.getJSONObject(i);
                ids.add(json.getString("id"));
                usernames.add(json.getString("username"));
                titles.add(json.getString("title"));
                descriptions.add(json.getString("description"));
                amounts.add(json.getString("amount"));
                freeOrNots.add(new Boolean(json.getString("freeOrNot")));
                xs.add(new Double(json.getString("x")));
                ys.add(new Double(json.getString("y")));
            }


        }
        catch(Exception e){
            Log.e("log_tag3", "Error Parsing Data: " + e.toString());
        }


        return data;
    }
}