package com.example.padel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(item ->{

            if(item.getItemId() == R.id.home){
                getApplicationContext();
            }
            else if(item.getItemId() == R.id.play){
                startActivity(new Intent(getApplicationContext(), PrenotaMatchActivity.class));
                finish();
                overridePendingTransition(0,0);
            }
            else if(item.getItemId() == R.id.training){
                startActivity(new Intent(getApplicationContext(), TrainingActivity.class));
                finish();
                overridePendingTransition(0,0);
            }
            else if(item.getItemId() == R.id.profile){
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
                overridePendingTransition(0,0);
            }
            else if(item.getItemId() == R.id.notification){
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                finish();
                overridePendingTransition(0,0);
            }
            return true;
        });
    }

    public void playAct(View view){
        startActivity(new Intent(MainActivity.this, PrenotaMatchActivity.class));
        overridePendingTransition(0,0);
    }
    public void trainingAct(View view){
        startActivity(new Intent(MainActivity.this, TrainingActivity.class));
        overridePendingTransition(0,0);
    }
}