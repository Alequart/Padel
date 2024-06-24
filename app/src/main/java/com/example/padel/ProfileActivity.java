package com.example.padel;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewWelcome, textViewName, textViewSurname, textViewDateOfBirth, textViewLivello, textViewEmail;
    private ProgressBar progressBar;

    private String name, surname, dateOfBirth, livello, email;

    private FirebaseAuth authProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnItemSelectedListener(item ->{

            if(item.getItemId() == R.id.profile){
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

        textViewWelcome = findViewById(R.id.welcome);
        textViewName = findViewById(R.id.profile_name);
        textViewSurname = findViewById(R.id.profile_surname);
        textViewDateOfBirth = findViewById(R.id.profile_birthday);
        textViewLivello = findViewById(R.id.profile_livello);
        textViewEmail = findViewById(R.id.profile_email);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        if(firebaseUser == null){
            Toast.makeText(ProfileActivity.this, "Qualcosa è andato storto", Toast.LENGTH_SHORT).show();
        } else{
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    private void showUserProfile(FirebaseUser firebaseUser){
        String userID = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Utenti registrati");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    name = readUserDetails.name;
                    surname = readUserDetails.surname;
                    dateOfBirth = readUserDetails.dateOfBirth;
                    livello = readUserDetails.livello;
                    email = firebaseUser.getEmail();

                    textViewWelcome.setText("Benvenuto " + name + "!");

                    textViewName.setText(name);
                    textViewSurname.setText(surname);
                    textViewDateOfBirth.setText(dateOfBirth);
                    textViewLivello.setText("Livello abilità: " + livello);
                    textViewEmail.setText(email);

                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Qualcosa è andato storto", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        } else if(id == R.id.menu_club){
            Toast.makeText(ProfileActivity.this, "DA AGGIUNGERE", Toast.LENGTH_SHORT).show();
        } else if(id == R.id.menu_logOut){
            authProfile.signOut();
            Toast.makeText(ProfileActivity.this, "Effettuo logout", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if(id == R.id.menu_delete){
            Toast.makeText(ProfileActivity.this, "DA AGGIUNGERE", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}