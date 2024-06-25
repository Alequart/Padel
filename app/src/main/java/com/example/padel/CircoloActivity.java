package com.example.padel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CircoloActivity extends AppCompatActivity {

    TextView titleHead, descriptionhead, description, spogliatoi, spogliatoiDescrizione, bar, barDescrizione, parcheggio, parcheggioDescrizione, noleggio, noleggioDescrizione, prenotazione, prenotazioneDescrizione, padelInfo, descriptionPadel, invito;

    ImageView imagePadel, greenCourt, blueCourt, orangeCourt, redCourt, appendino, drink, carSpot, racchetta, calendar, logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circolo);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnItemSelectedListener(item ->{

            if(item.getItemId() == R.id.profile){
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
                overridePendingTransition(0,0);
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
            else if(item.getItemId() == R.id.home){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                overridePendingTransition(0,0);
            }
            else if(item.getItemId() == R.id.notification) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
            return true;
        });

        titleHead = findViewById(R.id.titleHead);
        descriptionhead = findViewById(R.id.descriptionHead);
        description = findViewById(R.id.description);
        spogliatoi = findViewById(R.id.spogliatoi);
        spogliatoiDescrizione = findViewById(R.id.spogliatoiDescrizione);
        bar = findViewById(R.id.bar);
        barDescrizione = findViewById(R.id.barDescrizione);
        parcheggio = findViewById(R.id.parcheggio);
        parcheggioDescrizione = findViewById(R.id.parcheggioDescrizione);
        noleggio = findViewById(R.id.noleggio);
        noleggioDescrizione = findViewById(R.id.noleggioDescrizione);
        prenotazione = findViewById(R.id.prenotazione);
        prenotazioneDescrizione = findViewById(R.id.prenotazioneDescrizione);
        padelInfo = findViewById(R.id.padelInfo);
        descriptionPadel = findViewById(R.id.descriptionPadel);
        invito = findViewById(R.id.invito);

        imagePadel = findViewById(R.id.imagePadel);
        greenCourt = findViewById(R.id.greenCourt);
        blueCourt = findViewById(R.id.blueCourt);
        orangeCourt = findViewById(R.id.orangeCourt);
        redCourt = findViewById(R.id.redCourt);
        appendino = findViewById(R.id.appendino);
        drink = findViewById(R.id.drink);
        carSpot = findViewById(R.id.carSpot);
        racchetta = findViewById(R.id.racchetta);
        calendar = findViewById(R.id.calendar);
        logo = findViewById(R.id.logo);

    }
}