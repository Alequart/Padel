package com.example.padel;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TrainingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.training);

        bottomNavigationView.setOnItemSelectedListener(item ->{

            if(item.getItemId() == R.id.training){
                getApplicationContext();
            }
            else if(item.getItemId() == R.id.play){
                startActivity(new Intent(getApplicationContext(), PrenotaMatchActivity.class));
                finish();
                overridePendingTransition(0,0);
            }
            else if(item.getItemId() == R.id.home){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                overridePendingTransition(0,0);
            }
            else if(item.getItemId() == R.id.profile){
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
                overridePendingTransition(0,0);
            }
            return true;
        });
    }
}