package com.example.padel;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PrenotaMatchActivity extends AppCompatActivity {

    private ArrayList<String> arrayListGiocatori;
    private AlertDialog.Builder builder;

    private String nome, cognome, livello;
    private DatabaseReference data;

    private ProgressBar progressBar;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private EditText editTextDate;
    private EditText editTextTime;

    private FirebaseAuth firebaseAuth;

    int hour, minutes;

    String player1, player2, player3, player4;
    private static int num_prenotazione = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prenota_match);

        // Navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.play);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.play) {
                getApplicationContext();
            } else if (item.getItemId() == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                overridePendingTransition(0, 0);
            } else if (item.getItemId() == R.id.training) {
                startActivity(new Intent(getApplicationContext(), TrainingActivity.class));
                finish();
                overridePendingTransition(0, 0);
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
            return true;
        });

        ListView listView = findViewById(R.id.list_view);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // Visualizza giocatori nella schermata
        data = FirebaseDatabase.getInstance().getReference();
        data = data.child("Utenti registrati");

        data.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> arrayList = new ArrayList<>();
                String currentUserId = firebaseUser.getUid();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    nome = dataSnapshot.child("name").getValue(String.class);
                    cognome = dataSnapshot.child("surname").getValue(String.class);
                    livello = dataSnapshot.child("livello").getValue(String.class);
                    if (nome != null && cognome != null && !dataSnapshot.getKey().equals(currentUserId)) {
                        arrayList.add(nome + " " + cognome + "\n(Livello abilità: " + livello + ")");
                    }
                }
                progressBar.setVisibility(View.GONE);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(PrenotaMatchActivity.this, android.R.layout.simple_list_item_multiple_choice, arrayList);

                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PrenotaMatchActivity.this, "Errore", Toast.LENGTH_SHORT).show();
            }
        });

        arrayListGiocatori = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String player = (String) parent.getItemAtPosition(position);
                if(listView.isItemChecked(position)){
                    arrayListGiocatori.add(player);
                    if(arrayListGiocatori.size() > 3){
                        builder = new AlertDialog.Builder(PrenotaMatchActivity.this);
                        builder.setTitle("Attenzione!")
                                .setMessage("Nella prenotazione, verranno registrati solo i primi 3 giocatori selezionati.\nConsigliato rimuovere giocatori in eccesso")
                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).show();
                    }
                    Toast.makeText(PrenotaMatchActivity.this, player + " aggiunto" , Toast.LENGTH_SHORT).show();
                }
                else {
                    arrayListGiocatori.remove(player);
                    Toast.makeText(PrenotaMatchActivity.this, player + " rimosso" , Toast.LENGTH_SHORT).show();
                }


            }
        });

        // Imposta giorno e orario
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(PrenotaMatchActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextDate.setText(dayOfMonth + "/" + + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(PrenotaMatchActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        hour = hourOfDay;
                        minutes = minute;

                        editTextTime.setText((String.format(Locale.ITALIAN, "%d.%d", hourOfDay, minute)));
                    }
                } ,hour , minutes, true);
                timePickerDialog.show();
            }
        });



        // Completa prenotazione
        Button buttonPrenota = findViewById(R.id.buttonPrenotaMatch);

        buttonPrenota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textDate = editTextDate.getText().toString();
                String textTime = editTextTime.getText().toString();

                if(TextUtils.isEmpty(textDate)){
                    Toast.makeText(PrenotaMatchActivity.this,"Inserisci la data della prenotazione" , Toast.LENGTH_SHORT).show();
                    editTextDate.setError("Necessario inserire la data");
                } else if(TextUtils.isEmpty(textTime)){
                    Toast.makeText(PrenotaMatchActivity.this,"Inserisci l'orario della prenotazione" , Toast.LENGTH_SHORT).show();
                    editTextDate.setError("Necessario inserire l'orario");
                }
                else{
                    confermaPrenotazione(firebaseUser, arrayListGiocatori, textDate, textTime);
                }

            }
        });


    }

    private void confermaPrenotazione(FirebaseUser firebaseUser, ArrayList<String> arrayList , String textDate, String textTime){
        num_prenotazione++;

        String user = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Utenti registrati");
        databaseReference.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readNameSurnameLevel = snapshot.getValue(ReadWriteUserDetails.class);
                if(readNameSurnameLevel != null){
                    player1 = readNameSurnameLevel.name + " " + readNameSurnameLevel.surname  + "\n(Livello abilità: " + readNameSurnameLevel.livello + ")";
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Prenotazione n:" + num_prenotazione).child("Giocatore 1");
                    databaseReference1.setValue(player1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PrenotaMatchActivity.this, "Qualcosa è andato storto", Toast.LENGTH_SHORT).show();
            }
        });

        player2 = arrayList.get(0);
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Prenotazione n:" + num_prenotazione).child("Giocatore 2");
        databaseReference2.setValue(player2);


        player3 = arrayList.get(1);
        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Prenotazione n:" + num_prenotazione).child("Giocatore 3");
        databaseReference3.setValue(player3);


        player4 = arrayList.get(2);
        DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Prenotazione n:" + num_prenotazione).child("Giocatore 4");
        databaseReference4.setValue(player4);


        DatabaseReference databaseReference6 = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Prenotazione n:" + num_prenotazione).child("Data");
        databaseReference6.setValue(textDate);

        DatabaseReference databaseReference7 = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Prenotazione n:" + num_prenotazione).child("Orario");
        databaseReference7.setValue(textTime);


        Toast.makeText(PrenotaMatchActivity.this, "Prenotazione registrata con successo", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(PrenotaMatchActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        finish();

    }
}

