package com.projects.dl_kim.safeappnew;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_SMS= 0;
    Button  smsSender, settingsButton;
    ImageButton button;
    TextView textView;
    SmsManager smsManager;
    LocationManager locationManager;
    String lattitude, longitude;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRefs = database.getReference("Latitude");
    DatabaseReference myRef = database.getReference("Longitude");
    String phoneNo="0727222889";
    String message=""+ longitude+ "hi" +lattitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, REQUEST_SMS);


        textView = (TextView) findViewById(R.id.text_location);
        button = (ImageButton) findViewById(R.id.button_location);
        smsSender=(Button)findViewById(R.id.sms);
        settingsButton=(Button)findViewById(R.id.setInfo);


        button.setOnClickListener(this);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.setInfo){

                    Intent set = new Intent(MainActivity.this, com.projects.dl_kim.safeappnew.Settings.class);
                    startActivity(set);
                    finish();

                }
            }
        });
        smsSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSms(phoneNo,message);

            }
        });
    }

    private Boolean isSMSPermissionGranted() {

                if (ActivityCompat.checkSelfPermission(MainActivity.this,android.Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG,"Permission is granted");
                    return true;
                }else{

                    Log.v(TAG,"Permission is revoked");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS},REQUEST_SMS);
                    return false;
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 0: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getBaseContext(), "Permissions granted", Toast.LENGTH_SHORT).show();
                    //send sms here call your method
                    sendSms(phoneNo, message);
                } else {
                    Toast.makeText(getBaseContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void sendSms(String phoneNo, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            isSMSPermissionGranted();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
    }




    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {

                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                textView.setText("Your current location is" + "\n" + "Lattitude = " + lattitude + "\n" + "Longitude = " + longitude);
                myRefs.setValue("Lattitude = " + lattitude);
                myRef.setValue("Lattitude = " + longitude);
                String googleUrl = "https://maps.google.com/?q=" + lattitude + "," + longitude;
                message = "Please Help i am in need of an emergency at this location, respond fast!! " +googleUrl;

            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                textView.setText("Your current location is" + "\n" + "Lattitude = " + lattitude
                        + "\n" + "Longitude = " + longitude);
                String googleUrl = "https://maps.google.com/?q=" + lattitude + "," + longitude;
                message = "Please Help i am in need of an emergency at this location, respond fast!! " +googleUrl;



            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                textView.setText("Your current location is" + "\n" + "Lattitude = " + lattitude
                        + "\n" + "Longitude = " + longitude);
                String googleUrl = "https://maps.google.com/?q=" + lattitude + "," + longitude;
                message = "Please Help i am in need of an emergency at this location, respond fast!! " +googleUrl;


            } else {

                Toast.makeText(this, "Unble to Trace your location", Toast.LENGTH_SHORT).show();

            }
            sendSms(phoneNo, message);
        }
    }


    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


}