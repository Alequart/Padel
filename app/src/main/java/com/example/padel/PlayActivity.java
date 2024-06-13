package com.example.padel;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.play);

        bottomNavigationView.setOnItemSelectedListener(item ->{

            if(item.getItemId() == R.id.play){
                getApplicationContext();
            }
            else if(item.getItemId() == R.id.home){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
            else if(item.getItemId() == R.id.training){
                startActivity(new Intent(getApplicationContext(), TrainingActivity.class));
                finish();
            }
            else if(item.getItemId() == R.id.profile){
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
            }
            return true;
        });
    }
}