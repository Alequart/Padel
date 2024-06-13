package com.example.padel;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterName, editTextRegisterSurname, editTextRegisterDateOfBirth, editTextRegisterEmail, editTextRegisterPassword, editTextRegisterConfirmPassword;

    private static final String TAG = "RegisterActivity";

    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextRegisterName = findViewById(R.id.nome);
        editTextRegisterSurname = findViewById(R.id.cognome);
        editTextRegisterDateOfBirth = findViewById(R.id.editTextDate);
        editTextRegisterEmail = findViewById(R.id.email);
        editTextRegisterPassword = findViewById(R.id.password);
        editTextRegisterConfirmPassword = findViewById(R.id.confirmPassword);

        editTextRegisterDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDateOfBirth.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button buttonRegister = findViewById(R.id.btn_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textName = editTextRegisterName.getText().toString();
                String textSurname = editTextRegisterSurname.getText().toString();
                String textDateOfBirth = editTextRegisterDateOfBirth.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textPassword = editTextRegisterPassword.getText().toString();
                String textConfirmPassword = editTextRegisterConfirmPassword.toString();

                if(TextUtils.isEmpty(textName)){
                    Toast.makeText(RegisterActivity.this, "Inserisci il nome", Toast.LENGTH_LONG).show();
                    editTextRegisterName.setError("Necessario inserire il nome");
                    editTextRegisterName.requestFocus();
                } else if(TextUtils.isEmpty(textSurname)){
                    Toast.makeText(RegisterActivity.this, "Inserisci il cognome", Toast.LENGTH_LONG).show();
                    editTextRegisterSurname.setError("Necessario inserire il cognome");
                    editTextRegisterSurname.requestFocus();
                } else if(TextUtils.isEmpty(textDateOfBirth)){
                    Toast.makeText(RegisterActivity.this, "Inserisci la data di nascita", Toast.LENGTH_LONG).show();
                    editTextRegisterDateOfBirth.setError("Necessario inserire la data di nascita");
                    editTextRegisterDateOfBirth.requestFocus();
                } else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(RegisterActivity.this, "Inserisci l'email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Necessario inserire l'email");
                    editTextRegisterEmail.requestFocus();
                } else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(RegisterActivity.this, "Inserisci la password", Toast.LENGTH_LONG).show();
                    editTextRegisterPassword.setError("Necessario inserire la password");
                    editTextRegisterPassword.requestFocus();
                } else if(TextUtils.isEmpty(textConfirmPassword)){
                    Toast.makeText(RegisterActivity.this, "Inserisci nuovamente la password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPassword.setError("Necessario inserire nuovmente la password");
                    editTextRegisterConfirmPassword.requestFocus();
                }
                else if(textPassword.length() < 6){
                    Toast.makeText(RegisterActivity.this, "La password deve contenere almeno 6 caratteri", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPassword.setError("Password troppo corta");
                    editTextRegisterConfirmPassword.requestFocus();
                }
                else if(textConfirmPassword.equals(textPassword)){
                    Toast.makeText(RegisterActivity.this, "La password di conferma non coincide", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPassword.setError("Password non coincide");
                    editTextRegisterConfirmPassword.requestFocus();
                }
                else{
                    registerUser(textName, textSurname, textDateOfBirth, textEmail, textPassword, textConfirmPassword);
                }
            }
        });

    }

    private void registerUser(String textName, String textSurname, String textDateOfBirth, String textEmail, String textPassword, String textConfirmPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    ReadWriteUserDetails readWriteUserDetails = new ReadWriteUserDetails(textName, textSurname, textDateOfBirth);

                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Utenti registrati");

                    referenceProfile.child(firebaseUser.getUid()).setValue(readWriteUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Utente registrato con successo", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();
                            } else{
                                Toast.makeText(RegisterActivity.this, "Errore. Utente non registrato", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else{
                    try{
                        throw task.getException();
                    } catch(FirebaseAuthUserCollisionException e){
                        editTextRegisterEmail.setError("Email già in utilizzo");
                        editTextRegisterEmail.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void btn_login(View v){
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

}