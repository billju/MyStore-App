package com.example.chuboy.mystore;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

/**
 * Created by ChuBoy on 2017/8/20.
 */

public class SettingsActivity extends AppCompatActivity{
    private Switch mSwitch;
    private EditText editHome, editServer;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isOn = pref.getBoolean("notification_on",false);
        mSwitch = (Switch) findViewById(R.id.notification_switch);
        if(isOn){
            mSwitch.setChecked(true);
        }else{
            mSwitch.setChecked(false);
        }
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(mSwitch.isChecked()){
                    editor = pref.edit();
                    editor.putBoolean("notification_on",true).commit();

                    NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(SettingsActivity.this)
                            .setSmallIcon(R.drawable.mystore_shape)
                            .setContentTitle("MyStore")
                            .setContentText("Welcome to MyStore.");
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(SettingsActivity.this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
                    mBuilder.setContentIntent(pendingIntent);
                    manager.notify(1, mBuilder.build());
                }else{
                    editor = pref.edit();
                    editor.putBoolean("notification_on",false).commit();

                    NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel(1);
                }
            }
        });
        editHome = (EditText)findViewById(R.id.editHome);
        editServer = (EditText)findViewById(R.id.editServer);
        String home_url = pref.getString("home_url","");
        editHome.setText(home_url);
        String server_url = pref.getString("server_url","");
        editServer.setText(server_url);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        String home_url = editHome.getText().toString();
        String server_url = editServer.getText().toString();
        editor = pref.edit();
        editor.putString("home_url",home_url);
        editor.putString("server_url", server_url);
        editor.commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
