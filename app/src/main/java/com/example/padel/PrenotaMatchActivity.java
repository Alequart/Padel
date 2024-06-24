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
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import java.util.Objects;

public class PrenotaMatchActivity extends AppCompatActivity {

    private ArrayList<String> arrayListGiocatori;
    private AlertDialog.Builder builder;

    private String nome, cognome, livello;
    private DatabaseReference data;

    private ProgressBar progressBar;

    private DatePickerDialog datePickerDialog;
    private EditText editTextDate;
    boolean flagConfronto = true;
    boolean flagDouble = true;
    private Spinner spinnerTime;
    protected EditText editTextCampo;

    private FirebaseAuth firebaseAuth;

    private String orario;
    private final String slot1 = "09:00 - 10:30";
    private final String slot2 = "10:30 - 12:00";
    private final String slot3 = "12:00 - 13:30";
    private final String slot4 = "16:00 - 17:30";
    private final String slot5 = "17:30 - 19:00";
    private final String slot6 = "19:00 - 20:30";
    private final String slot7 = "20:30 - 22:00";
    private final String slot8 = "22:00 - 23:30";

    private String player1, player2, player3, player4;
    private ArrayList<String> concatConfronto;

    protected static String color;

    boolean fullPlayers;
    private String IdPrenotazione;

    private FirebaseUser firebaseUser;

    private String textDate;
    private String textTime;
    private String textCampo;

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
            } else if(item.getItemId() == R.id.notification){
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
            return true;
        });

        ListView listView = findViewById(R.id.list_view);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

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
                        arrayList.add(nome + " " + cognome + " (Livello abilità: " + livello + ")");
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
        spinnerTime = findViewById(R.id.spinnerTime);

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
                        editTextDate.setText(dayOfMonth + "-" + + (month + 1) + "-" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orario = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayList<String> listOrari = new ArrayList<>();
        listOrari.add("--Seleziona un orario--");
        listOrari.add(slot1);
        listOrari.add(slot2);
        listOrari.add(slot3);
        listOrari.add(slot4);
        listOrari.add(slot5);
        listOrari.add(slot6);
        listOrari.add(slot7);
        listOrari.add(slot8);
        ArrayAdapter<String> adapterOrari = new ArrayAdapter<>(PrenotaMatchActivity.this, android.R.layout.simple_spinner_item, listOrari);
        adapterOrari.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerTime.setAdapter(adapterOrari);


        editTextCampo = findViewById(R.id.editTextCampo);
        editTextCampo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment();
            }
        });
        

        // Completa prenotazione
        Button buttonPrenota = findViewById(R.id.buttonPrenotaMatch);

        buttonPrenota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDate = editTextDate.getText().toString();
                textTime = orario;
                textCampo = editTextCampo.getText().toString();

                if(TextUtils.isEmpty(textDate)){
                    Toast.makeText(PrenotaMatchActivity.this,"Inserisci la data della prenotazione" , Toast.LENGTH_SHORT).show();
                    editTextDate.setError("Necessario inserire la data");
                } else if(TextUtils.isEmpty(textTime)){
                    Toast.makeText(PrenotaMatchActivity.this,"Inserisci l'orario della prenotazione" , Toast.LENGTH_SHORT).show();
                    editTextDate.setError("Necessario inserire l'orario");
                }else if(textTime.equals("--Seleziona un orario--")){
                    Toast.makeText(PrenotaMatchActivity.this,"Inserisci l'orario della prenotazione" , Toast.LENGTH_SHORT).show();
                    editTextDate.setError("Necessario inserire l'orario");
                }
                else if(TextUtils.isEmpty(textCampo)) {
                    Toast.makeText(PrenotaMatchActivity.this, "Inserisci il campo in cui giocare", Toast.LENGTH_SHORT).show();
                    editTextDate.setError("Necessario inserire il campo");
                }
                else{
                    fullPlayers = arrayListGiocatori.size() >= 3;
                    IdPrenotazione = textDate + textTime + textCampo;

                    confrontaPrenotazione(IdPrenotazione);
                }
            }
        });
    }

    private void confrontaPrenotazione(String idPrenotazione) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Prenotazioni");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(("Prenotazione " + idPrenotazione).equals(dataSnapshot.getKey())){
                        //System.out.println("PRENOTAZIONE UGUALE\n" + fullPlayers);
                        flagConfronto = false;
                        //System.out.println(dataSnapshot.child("Giocatore 4").getValue());
                        if(fullPlayers && dataSnapshot.child("Giocatore 4").getValue() == null){
                            flagConfronto = true;
                        }
                        break;
                    }
                }
                //System.out.println("CONFRONTO");
                concretizzaPrenotazione(flagConfronto);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PrenotaMatchActivity.this, "Qualcosa è andato storto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void concretizzaPrenotazione(boolean flagConfronto){
        if (flagConfronto) {
            //System.out.println("CONCRETIZZA");
            confermaPrenotazione(firebaseUser, arrayListGiocatori, textDate, textTime);
        } else {
            Toast.makeText(PrenotaMatchActivity.this, "Impossibile registrare prenotazione. Il campo potrebbe essere già prenotato", Toast.LENGTH_LONG).show();
            startActivity(getIntent());
            finish();
        }
    }

    private void completaPrenoatazione(boolean flagDouble){
        //System.out.println("COMPLETA: "+ flagDouble);
        if(flagDouble){
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Prenotazione " + IdPrenotazione).child("Giocatore 1");
            databaseReference1.setValue(player1);

            if(player2 != null){
                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Prenotazione " + IdPrenotazione).child("Giocatore 2");
                databaseReference2.setValue(player2);
            }

            if(player3 != null) {
                DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Prenotazione " + IdPrenotazione).child("Giocatore 3");
                databaseReference3.setValue(player3);
            }

            if(player4 != null) {
                DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Prenotazione " + IdPrenotazione).child("Giocatore 4");
                databaseReference4.setValue(player4);
            }


            DatabaseReference databaseReference6 = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Prenotazione " + IdPrenotazione).child("Data");
            databaseReference6.setValue(textDate);

            DatabaseReference databaseReference7 = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Prenotazione " + IdPrenotazione).child("Orario");
            databaseReference7.setValue(textTime);

            DatabaseReference databaseReference8 = FirebaseDatabase.getInstance().getReference("Prenotazioni").child("Prenotazione " + IdPrenotazione).child("Campo");
            databaseReference8.setValue(textCampo);


            Toast.makeText(PrenotaMatchActivity.this, "Prenotazione registrata con successo", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PrenotaMatchActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }
        else{
            Toast.makeText(PrenotaMatchActivity.this, "Impossibile registrare prenotazione. Giocatore già registrato in un'altra partita", Toast.LENGTH_LONG).show();
            startActivity(getIntent());
            finish();
        }
    }

    private void confermaPrenotazione(FirebaseUser firebaseUser, ArrayList<String> arrayList , String textDate, String textTime){
        String user = firebaseUser.getUid();
        //System.out.println("CONFERMA");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Utenti registrati");
        databaseReference.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readNameSurnameLevel = snapshot.getValue(ReadWriteUserDetails.class);
                if(readNameSurnameLevel != null){
                    player1 = readNameSurnameLevel.name + " " + readNameSurnameLevel.surname  + " (Livello abilità: " + readNameSurnameLevel.livello + ")";
                }
                if(arrayList.size() >= 3){
                    if(arrayList.get(0) != null){
                        player2 = arrayList.get(0);
                    }
                    if(arrayList.get(1) != null){
                        player3 = arrayList.get(1);
                    }
                    if(arrayList.get(2) != null){
                        player4 = arrayList.get(2);
                    }
                }
                else if(arrayList.size() == 2){
                    if(arrayList.get(0) != null){
                        player2 = arrayList.get(0);
                    }
                    if(arrayList.get(1) != null){
                        player3 = arrayList.get(1);
                    }
                }
                else if(arrayList.size() == 1){
                    if(arrayList.get(0) != null){
                        player2 = arrayList.get(0);
                    }
                }


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Prenotazioni");

                ArrayList<String> arrayGiocatori = new ArrayList<>();
                concatConfronto = new ArrayList<>();
                String concat1 = player1 + textDate + textTime;
                arrayGiocatori.add(concat1);

                if(player2 != null){
                    String concat2 = player2 + textDate + textTime;
                    arrayGiocatori.add(concat2);
                }
                if(player3 != null){
                    String concat3 = player3 + textDate + textTime;
                    arrayGiocatori.add(concat3);
                }
                if(player4 != null){
                    String concat4 = player4 + textDate + textTime;
                    arrayGiocatori.add(concat4);
                }

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            concatConfronto.add(dataSnapshot.child("Giocatore 1").getValue(String.class)
                                    + dataSnapshot.child("Data").getValue(String.class)
                                    + dataSnapshot.child("Orario").getValue(String.class));
                            concatConfronto.add(dataSnapshot.child("Giocatore 2").getValue(String.class)
                                    +dataSnapshot.child("Data").getValue(String.class)
                                    + dataSnapshot.child("Orario").getValue(String.class));
                            concatConfronto.add(dataSnapshot.child("Giocatore 3").getValue(String.class)
                                    + dataSnapshot.child("Data").getValue(String.class)
                                    + dataSnapshot.child("Orario").getValue(String.class));
                            concatConfronto.add(dataSnapshot.child("Giocatore 4").getValue(String.class)
                                    + dataSnapshot.child("Data").getValue(String.class)
                                    + dataSnapshot.child("Orario").getValue(String.class));
                        }

                        for(String s : arrayGiocatori){
                            for (String p : concatConfronto){
                                if(Objects.equals(s, p)){
                                    flagDouble = false;
                                    break;
                                }
                            }
                        }
                        completaPrenoatazione(flagDouble);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PrenotaMatchActivity.this, "Qualcosa è andato storto", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public String getColor(){
        return color;
    }
    

    private void openFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, new CourtFragment(), "CourtFragmentTag")
                .addToBackStack(null)
                .commit();
    }

}

