package edu.depaul.csc372.cnewmiller_pokercalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button goToTable = (Button) findViewById(R.id.goToTableButton);
        Button.OnClickListener listener = new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, TableActivity.class);
                startActivity(intent);
            }

        };
        goToTable.setOnClickListener(listener);

        (findViewById(R.id.goToSettings)).setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, Settings.class);
                startActivity(intent);
            }
        });


    }
}
