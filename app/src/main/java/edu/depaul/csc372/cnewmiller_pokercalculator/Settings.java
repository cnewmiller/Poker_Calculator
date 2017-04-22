package edu.depaul.csc372.cnewmiller_pokercalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final SharedPreferences globalSettings = this.getSharedPreferences(
                getString(R.string.global_settings), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = globalSettings.edit();


        Switch loadSaved = (Switch) findViewById(R.id.loadSwitch);
        loadSaved.setChecked(globalSettings.getBoolean(getString(R.string.settings_loadTable),false));
        loadSaved.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(getString(R.string.settings_loadTable),b);
                editor.commit();
            }
        });


    }



}
