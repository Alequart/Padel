package com.example.padel;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView listPrenotazioni;
    private ProgressBar progressBar;

    DatabaseReference databaseReference;
    DatabaseReference database;
    DatabaseReference reference;


    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String concatGiocatore;

    private static final String TAG = "DeleteAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.notification);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.notification) {
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
            } else if(item.getItemId() == R.id.play){
                startActivity(new Intent(getApplicationContext(), PrenotaMatchActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
            return true;
        });

        listPrenotazioni = findViewById(R.id.listPrenotazioni);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        getNomeCognomeLivelloUser();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(NotificationActivity.this, MainActivity.class));
        finish();
    }

    public void getNomeCognomeLivelloUser(){

        databaseReference = FirebaseDatabase.getInstance().getReference("Utenti registrati");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if((dataSnapshot.getKey()).equals(firebaseUser.getUid())){
                        concatGiocatore = dataSnapshot.child("name").getValue(String.class) +
                                " " + dataSnapshot.child("surname").getValue(String.class) +
                                " (Livello abilità: " + dataSnapshot.child("livello").getValue(String.class)
                                + ")";
                        break;
                    }
                }
                setPrenotazioni();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setPrenotazioni(){
        List<Item> items = new ArrayList<Item>();

        database = FirebaseDatabase.getInstance().getReference("Prenotazioni");


        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.child("Giocatore 1").getValue(String.class).equals(concatGiocatore)){
                        if(dataSnapshot.child("Giocatore 2").exists()){
                            if(dataSnapshot.child("Giocatore 3").exists()){
                                if(dataSnapshot.child("Giocatore 4").exists()){
                                    items.add(new Item(
                                            dataSnapshot.getKey(),
                                            concatGiocatore,
                                            dataSnapshot.child("Giocatore 2").getValue(String.class),
                                            dataSnapshot.child("Giocatore 3").getValue(String.class),
                                            dataSnapshot.child("Giocatore 4").getValue(String.class),
                                            dataSnapshot.child("Data").getValue(String.class),
                                            dataSnapshot.child("Orario").getValue(String.class),
                                            dataSnapshot.child("Campo").getValue(String.class)));
                                }
                                else{
                                    items.add(new Item(
                                            dataSnapshot.getKey(),
                                            concatGiocatore,
                                            dataSnapshot.child("Giocatore 2").getValue(String.class),
                                            dataSnapshot.child("Giocatore 3").getValue(String.class),
                                            dataSnapshot.child("Data").getValue(String.class),
                                            dataSnapshot.child("Orario").getValue(String.class),
                                            dataSnapshot.child("Campo").getValue(String.class)));
                                }
                            }
                            else{
                                items.add(new Item(
                                        dataSnapshot.getKey(),
                                        concatGiocatore,
                                        dataSnapshot.child("Giocatore 2").getValue(String.class),
                                        dataSnapshot.child("Data").getValue(String.class),
                                        dataSnapshot.child("Orario").getValue(String.class),
                                        dataSnapshot.child("Campo").getValue(String.class)));
                            }
                        }
                        else{
                            items.add(new Item(
                                    dataSnapshot.getKey(),
                                    concatGiocatore,
                                    dataSnapshot.child("Data").getValue(String.class),
                                    dataSnapshot.child("Orario").getValue(String.class),
                                    dataSnapshot.child("Campo").getValue(String.class)));
                        }
                    }
                    else if(dataSnapshot.child("Giocatore 2").exists() && dataSnapshot.child("Giocatore 2").getValue(String.class).equals(concatGiocatore)){
                        if(dataSnapshot.child("Giocatore 3").exists()){
                            if(dataSnapshot.child("Giocatore 4").exists()){
                                items.add(new Item(
                                        dataSnapshot.getKey(),
                                        dataSnapshot.child("Giocatore 1").getValue(String.class),
                                        concatGiocatore,
                                        dataSnapshot.child("Giocatore 3").getValue(String.class),
                                        dataSnapshot.child("Giocatore 4").getValue(String.class),
                                        dataSnapshot.child("Data").getValue(String.class),
                                        dataSnapshot.child("Orario").getValue(String.class),
                                        dataSnapshot.child("Campo").getValue(String.class)));
                            }
                            else{
                                items.add(new Item(
                                        dataSnapshot.getKey(),
                                        dataSnapshot.child("Giocatore 1").getValue(String.class),
                                        concatGiocatore,
                                        dataSnapshot.child("Giocatore 3").getValue(String.class),
                                        dataSnapshot.child("Data").getValue(String.class),
                                        dataSnapshot.child("Orario").getValue(String.class),
                                        dataSnapshot.child("Campo").getValue(String.class)));
                            }
                        }
                        else{
                                items.add(new Item(
                                        dataSnapshot.getKey(),
                                        dataSnapshot.child("Giocatore 1").getValue(String.class),
                                        concatGiocatore,
                                        dataSnapshot.child("Data").getValue(String.class),
                                        dataSnapshot.child("Orario").getValue(String.class),
                                        dataSnapshot.child("Campo").getValue(String.class)));
                            }
                    }
                    else if(dataSnapshot.child("Giocatore 3").exists() && dataSnapshot.child("Giocatore 3").getValue(String.class).equals(concatGiocatore)){
                        if(dataSnapshot.child("Giocatore 4").exists()){
                            items.add(new Item(
                                    dataSnapshot.getKey(),
                                    dataSnapshot.child("Giocatore 1").getValue(String.class),
                                    dataSnapshot.child("Giocatore 2").getValue(String.class),
                                    concatGiocatore,
                                    dataSnapshot.child("Giocatore 4").getValue(String.class),
                                    dataSnapshot.child("Data").getValue(String.class),
                                    dataSnapshot.child("Orario").getValue(String.class),
                                    dataSnapshot.child("Campo").getValue(String.class)));
                        }
                        else{
                            items.add(new Item(
                                    dataSnapshot.getKey(),
                                    dataSnapshot.child("Giocatore 1").getValue(String.class),
                                    dataSnapshot.child("Giocatore 2").getValue(String.class),
                                    concatGiocatore,
                                    dataSnapshot.child("Data").getValue(String.class),
                                    dataSnapshot.child("Orario").getValue(String.class),
                                    dataSnapshot.child("Campo").getValue(String.class)));
                        }
                    }
                    else if(dataSnapshot.child("Giocatore 4").exists() && dataSnapshot.child("Giocatore 4").getValue(String.class).equals(concatGiocatore)){

                        items.add(new Item(
                                dataSnapshot.getKey(),
                                dataSnapshot.child("Giocatore 1").getValue(String.class),
                                dataSnapshot.child("Giocatore 2").getValue(String.class),
                                dataSnapshot.child("Giocatore 3").getValue(String.class),
                                concatGiocatore,
                                dataSnapshot.child("Data").getValue(String.class),
                                dataSnapshot.child("Orario").getValue(String.class),
                                dataSnapshot.child("Campo").getValue(String.class)));
                    }
                }
//                listPrenotazioni.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
//                listPrenotazioni.setAdapter(new MyAdapter(getApplicationContext(), items));

                reference = FirebaseDatabase.getInstance().getReference("Allenamenti");

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(dataSnapshot.child("Giocatore").getValue(String.class).equals(concatGiocatore)){
                                items.add(new Item(
                                        dataSnapshot.getKey(),
                                        concatGiocatore,
                                        dataSnapshot.child("Allenatore").getValue(String.class),
                                        dataSnapshot.child("Data").getValue(String.class),
                                        dataSnapshot.child("Orario").getValue(String.class),
                                        dataSnapshot.child("Campo").getValue(String.class)));
                            }
                        }
                        selezionaElementi(items);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void selezionaElementi(List<Item> items){
        listPrenotazioni.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
        listPrenotazioni.setAdapter(new MyAdapter(getApplicationContext(), items, new MyAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                if(item.getGiocatore1().equals(concatGiocatore)){
                    showAlertDialog(item);
                }
            }
        }));
        progressBar.setVisibility(View.GONE);

    }

    private void showAlertDialog(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NotificationActivity.this);
        builder.setTitle("Elimina prenotazione");
        builder.setMessage("Vuoi eliminare la prenotazione? Se sei sicuro, clicca Conferma");

        builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUserdata(item);
                Toast.makeText(NotificationActivity.this, "Prenotazione eliminata", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });

        alertDialog.show();
    }

    private void deleteUserdata(Item item){
        database.child(item.getPrenotazione()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "OnSucccess: Dati utente eliminati");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(NotificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        reference.child(item.getPrenotazione()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "OnSucccess: Dati utente eliminati");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(NotificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}