package com.example.padel;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAccountActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private EditText editTextPassword;
    private TextView textViewAuth;
    private String userPass;
    private Button buttonAuthUser;
    private Button buttonDeleteUser;

    private static final String TAG = "DeleteAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        editTextPassword = findViewById(R.id.editDeletePassword);
        textViewAuth = findViewById(R.id.textViewAutenticato);
        buttonAuthUser = findViewById(R.id.checkPassword);
        buttonDeleteUser = findViewById(R.id.deleteProfile);

        buttonDeleteUser.setEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser.equals("")){
            Toast.makeText(DeleteAccountActivity.this, "Errore, account non trovato", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteAccountActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        }
        authenticateUser(firebaseUser);

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(DeleteAccountActivity.this, ProfileActivity.class));
        finish();
    }

    public void authenticateUser(FirebaseUser firebaseUser){
        buttonAuthUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPass = editTextPassword.getText().toString();
                    if(TextUtils.isEmpty(userPass)){
                        Toast.makeText(DeleteAccountActivity.this, "Necessario inserire la password", Toast.LENGTH_SHORT).show();
                        editTextPassword.setError("Inserisci la password");
                        editTextPassword.requestFocus();
                } else{
                        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPass);

                        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    editTextPassword.setEnabled(false);

                                    buttonAuthUser.setEnabled(false);
                                    buttonDeleteUser.setEnabled(true);

                                    textViewAuth.setText("Utente riconosciuto. Puoi eliminare l'account");

                                    buttonDeleteUser.setBackgroundTintList(ContextCompat.getColorStateList(DeleteAccountActivity.this, R.color.red));

                                    buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showAlertDialog();
                                        }
                                    });
                                } else{
                                    try{
                                        throw task.getException();
                                    } catch (Exception e){
                                        Toast.makeText(DeleteAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteAccountActivity.this);
        builder.setTitle("Elimina account");
        builder.setMessage("Stai per eliminare l'account. Una volta confermato, tutti i dati relativi a questo account andranno persi");

        builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUser(firebaseUser);
            }
        });


        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(getIntent());
                finish();
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

    private void deleteUser(FirebaseUser firebaseUser){
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    deleteUserData();
                    firebaseAuth.signOut();
                    Toast.makeText(DeleteAccountActivity.this, "Utente eliminato", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DeleteAccountActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    try{
                        throw task.getException();
                    } catch (Exception e){
                        Toast.makeText(DeleteAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void deleteUserData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Utenti registrati");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "OnSucccess: Dati utente eliminati");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(DeleteAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}