package com.example.padel;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class TrainingActivity extends AppCompatActivity {

    boolean check  = false;

    private DatePickerDialog datePickerDialog;
    private EditText editTextDate;
    private Spinner spinnerTime;

    protected EditText editTextCampo;

    private String orario;
    private final String slot1 = "09:00 - 10:30";
    private final String slot2 = "10:30 - 12:00";
    private final String slot3 = "12:00 - 13:30";
    private final String slot4 = "16:00 - 17:30";
    private final String slot5 = "17:30 - 19:00";
    private final String slot6 = "19:00 - 20:30";
    private final String slot7 = "20:30 - 22:00";
    private final String slot8 = "22:00 - 23:30";


    private String textDate;
    private String textTime;
    private String textCampo;

    protected static String color;

    private AlertDialog.Builder builder;

    private String IdPrenotazione;

    boolean flagConfrontoPartite = true;
    boolean flagConfrontoAllenamenti = true;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private String player;
    private String allenatore;

    private ArrayList<String> concatConfronto;

    private boolean flagDoublePartite = true;

    private boolean flagDoubleGiocatore = true;
    private boolean flagDoubleAllenatore = true;
    private boolean flagDoubleAllenamenti = true;

    private String concatAllenamento;

    private DatabaseReference data1;
    private DatabaseReference data2;

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        data1 = FirebaseDatabase.getInstance().getReference("Allenatori");
        data1.child("Allenatore 1").setValue("Filippo");

        data2 = FirebaseDatabase.getInstance().getReference("Allenatori");
        data2.child("Allenatore 2").setValue("Roberto");


        ImageView instructor1, instructor2;
        instructor1 = (ImageView) findViewById(R.id.instructor1);
        instructor2 = (ImageView) findViewById(R.id.instructor2);

        instructor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = true;
                instructor1.setBackgroundColor(Color.GREEN);
                instructor2.setBackgroundColor(Color.TRANSPARENT);
                allenatore = "Filippo";
            }
        });

        instructor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = true;
                instructor2.setBackgroundColor(Color.GREEN);
                instructor1.setBackgroundColor(Color.TRANSPARENT);
                allenatore = "Roberto";
            }
        });


        editTextDate = findViewById(R.id.editTextDate);
        spinnerTime = findViewById(R.id.spinnerTime);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(TrainingActivity.this, new DatePickerDialog.OnDateSetListener() {
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
        ArrayAdapter<String> adapterOrari = new ArrayAdapter<>(TrainingActivity.this, android.R.layout.simple_spinner_item, listOrari);
        adapterOrari.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerTime.setAdapter(adapterOrari);


        editTextCampo = findViewById(R.id.editTextCampo);
        editTextCampo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment();
            }
        });


        Button buttonPrenotaAllenamento = (Button) findViewById(R.id.buttonPrenotaAllenamento);

        buttonPrenotaAllenamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDate = editTextDate.getText().toString();
                textTime = orario;
                textCampo = editTextCampo.getText().toString();


                if(allenatore == null){
                    Toast.makeText(TrainingActivity.this,"Scegli l'allenatore" , Toast.LENGTH_SHORT).show();
                    editTextDate.setError("Necessario scegliere l'allenatore");
                } else if(TextUtils.isEmpty(textDate)){
                    Toast.makeText(TrainingActivity.this,"Inserisci la data della prenotazione" , Toast.LENGTH_SHORT).show();
                    editTextDate.setError("Necessario inserire la data");
                } else if(TextUtils.isEmpty(textTime)){
                    Toast.makeText(TrainingActivity.this,"Inserisci l'orario della prenotazione" , Toast.LENGTH_SHORT).show();
                    editTextDate.setError("Necessario inserire l'orario");
                }else if(textTime.equals("--Seleziona un orario--")){
                    Toast.makeText(TrainingActivity.this,"Inserisci l'orario della prenotazione" , Toast.LENGTH_SHORT).show();
                    editTextDate.setError("Necessario inserire l'orario");
                }
                else if(TextUtils.isEmpty(textCampo)) {
                    Toast.makeText(TrainingActivity.this, "Inserisci il campo in cui giocare", Toast.LENGTH_SHORT).show();
                    editTextDate.setError("Necessario inserire il campo");
                }
                else{
                    IdPrenotazione = textDate + textTime + textCampo;
                    confrontaPrenotazionePartita(IdPrenotazione);
                }
            }
        });
    }

    //CONTROLLO SE QUEL GIORNO E PER QUELL'ORA IL CAMPO È GIÁ PRENOTATO PER UNA PARTITA
    private void confrontaPrenotazionePartita(String idPrenotazione){
        DatabaseReference referencePartite = FirebaseDatabase.getInstance().getReference("Prenotazioni");
        referencePartite.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    System.out.println(dataSnapshot.getKey());
                    if(("Prenotazione " + idPrenotazione).equals(dataSnapshot.getKey())){
                        flagConfrontoPartite = false;
                        break;
                    }
                }
                //System.out.println(flagConfrontoPartite);
                confrontaPrenotazioneAllenamento(idPrenotazione);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //CONTROLLO SE QUEL GIORNO E PER QUELL'ORA IL CAMPO È GIÁ PRENOTATO PER UN'ALLENAMENTO
    private void confrontaPrenotazioneAllenamento(String idPrenotazione){

        DatabaseReference referenceAllenamenti = FirebaseDatabase.getInstance().getReference("Allenamenti");
        referenceAllenamenti.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(("Prenotazione " + idPrenotazione).equals(dataSnapshot.getKey())){
                        flagConfrontoAllenamenti = false;
                        break;
                    }
                }
                concretizzaPrenotazione(flagConfrontoPartite, flagConfrontoAllenamenti);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //METTO INSIEME ENTRAMBE LE CONDIZIONI PRECEDENTI
    private void concretizzaPrenotazione(boolean flagConfrontoPartite, boolean flagConfrontoAllenamenti){
        //System.out.println(flagConfrontoPartite);
        if(flagConfrontoPartite && flagConfrontoAllenamenti){
            confermaPrenotazione(firebaseUser, textDate, textTime);
        } else{
            Toast.makeText(TrainingActivity.this, "Impossibile registrare prenotazione. Il campo potrebbe essere già prenotato", Toast.LENGTH_LONG).show();
            startActivity(getIntent());
            finish();
        }
    }

//  CONTROLLO SE LO STESSO GIOCATORE È GIÁ PRENOTATO LO STESSO GIORNO ALLA STESSA ORA IN UN ALTRO CAMPO
    private void confermaPrenotazione(FirebaseUser firebaseUser, String textDate, String textTime){
        String user = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Utenti registrati");

        databaseReference.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readNameSurnameLevel = snapshot.getValue(ReadWriteUserDetails.class);

                if(readNameSurnameLevel != null){
                    player = readNameSurnameLevel.name + " " + readNameSurnameLevel.surname  + "\n(Livello abilità: " + readNameSurnameLevel.livello + ")";
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Prenotazioni");
                DatabaseReference database = FirebaseDatabase.getInstance().getReference("Allenamenti");

                concatConfronto = new ArrayList<>();
                String concat = player + textDate + textTime;

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            concatConfronto.add(dataSnapshot.child("Giocatore 1").getValue(String.class)
                                    + dataSnapshot.child("Data").getValue(String.class)
                                    + dataSnapshot.child("Orario").getValue(String.class));
                            concatConfronto.add(dataSnapshot.child("Giocatore 2").getValue(String.class)
                                    + dataSnapshot.child("Data").getValue(String.class)
                                    + dataSnapshot.child("Orario").getValue(String.class));
                            concatConfronto.add(dataSnapshot.child("Giocatore 3").getValue(String.class)
                                    + dataSnapshot.child("Data").getValue(String.class)
                                    + dataSnapshot.child("Orario").getValue(String.class));
                            concatConfronto.add(dataSnapshot.child("Giocatore 4").getValue(String.class)
                                    + dataSnapshot.child("Data").getValue(String.class)
                                    + dataSnapshot.child("Orario").getValue(String.class));
                        }

                        for(String s: concatConfronto){
                            if(Objects.equals(concat, s)){
                                flagDoublePartite = false;
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                String concatAllenatore = allenatore + textDate + textTime;
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            concatAllenamento = dataSnapshot.child("Giocatore").getValue(String.class)
                                    + dataSnapshot.child("Data").getValue(String.class)
                                    + dataSnapshot.child("Orario").getValue(String.class);
                            if(concat.equals(concatAllenamento)){
                                flagDoubleGiocatore = false;
                            }
                        }
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            concatAllenamento = dataSnapshot.child("Allenatore").getValue(String.class)
                                    + dataSnapshot.child("Data").getValue(String.class)
                                    + dataSnapshot.child("Orario").getValue(String.class);
                            if(concatAllenatore.equals(concatAllenamento)){
                                flagDoubleAllenatore = false;
                            }
                        }
                        if(!flagDoubleGiocatore || !flagDoubleAllenatore){
                            flagDoubleAllenamenti = false;
                        }
                        completaPrenotazione(flagDoublePartite, flagDoubleAllenamenti);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TrainingActivity.this, "Qualcosa è andato storto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completaPrenotazione(boolean flagDoublePartite, boolean flagDoubleAllenamenti){
        if(flagDoublePartite && flagDoubleAllenamenti){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Allenamenti").child("Prenotazione " + IdPrenotazione).child("Giocatore");
            databaseReference.setValue(player);

            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Allenamenti").child("Prenotazione " + IdPrenotazione).child("Allenatore");
            databaseReference2.setValue(allenatore);

            DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("Allenamenti").child("Prenotazione " + IdPrenotazione).child("Data");
            databaseReference3.setValue(textDate);

            DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference("Allenamenti").child("Prenotazione " + IdPrenotazione).child("Orario");
            databaseReference4.setValue(textTime);

            DatabaseReference databaseReference5 = FirebaseDatabase.getInstance().getReference("Allenamenti").child("Prenotazione " + IdPrenotazione).child("Campo");
            databaseReference5.setValue(textCampo);


            Toast.makeText(TrainingActivity.this, "Prenotazione registrata con successo", Toast.LENGTH_LONG).show();
            callAlert();
        }
        else{
            Toast.makeText(TrainingActivity.this, "Impossibile registrare prenotazione. Giocatore/i e/o allenatore non disponibili", Toast.LENGTH_LONG).show();
            startActivity(getIntent());
            finish();
        }
    }

    private void openFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.training, new CourtTrainingFragment(), "CourtTrainingFragmentTag")
                .addToBackStack(null)
                .commit();
    }

    public String getColor(){return color;}

    private void callAlert(){
        builder = new AlertDialog.Builder(TrainingActivity.this);
        builder.setTitle("Attenzione!")
        .setMessage("La prenotazione dell'allenamento può essere annullata in caso di prenotazione di una partita")
        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(TrainingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).show();
    }

}