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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterName, editTextRegisterSurname, editTextRegisterDateOfBirth, editTextRegisterEmail, editTextRegisterPassword, editTextRegisterConfirmPassword;

    private RadioGroup radioGroupLivello;
    private RadioButton radioButtonSelected;

    private ProgressBar progressBar;

    private static final String TAG = "RegisterActivity";
    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextRegisterName = findViewById(R.id.nome);
        editTextRegisterSurname = findViewById(R.id.cognome);
        editTextRegisterDateOfBirth = findViewById(R.id.editTextDate);
        editTextRegisterEmail = findViewById(R.id.register_email);
        editTextRegisterPassword = findViewById(R.id.register_password);
        editTextRegisterConfirmPassword = findViewById(R.id.confirmPassword);

        radioGroupLivello = findViewById(R.id.radioGroup);
        radioGroupLivello.clearCheck();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

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

                int selectLivelloId = radioGroupLivello.getCheckedRadioButtonId();
                radioButtonSelected = findViewById(selectLivelloId);

                String textName = editTextRegisterName.getText().toString();
                String textSurname = editTextRegisterSurname.getText().toString();
                String textDateOfBirth = editTextRegisterDateOfBirth.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textPassword = editTextRegisterPassword.getText().toString();
                String textConfirmPassword = editTextRegisterConfirmPassword.getText().toString();
                String textLivello;

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
                } else if(radioGroupLivello.getCheckedRadioButtonId() == -1){
                    Toast.makeText(RegisterActivity.this, "Seleziona il livello di abilità", Toast.LENGTH_LONG).show();
                    radioButtonSelected.setError("Necessario selezionare un livello di abilità");
                    radioButtonSelected.requestFocus();
                }
                else if(TextUtils.isEmpty(textEmail)){
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
                    editTextRegisterPassword.setError("Password troppo corta");
                    editTextRegisterPassword.requestFocus();
                }
                else if(!textPassword.equals(textConfirmPassword)){
                    Toast.makeText(RegisterActivity.this, "La password di conferma non coincide", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPassword.setError("Password non coincide");
                    editTextRegisterConfirmPassword.requestFocus();
                }
                else{
                    textLivello = radioButtonSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textName, textSurname, textDateOfBirth, textLivello, textEmail, textPassword, textConfirmPassword);
                }
            }
        });
    }

    private void registerUser(String textName, String textSurname, String textDateOfBirth, String textLivello, String textEmail, String textPassword, String textConfirmPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    ReadWriteUserDetails readWriteUserDetails = new ReadWriteUserDetails(textName, textSurname, textDateOfBirth, textLivello);

                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Utenti registrati");
                    DatabaseReference referenceNumberProfiles = FirebaseDatabase.getInstance().getReference("Numero utenti");


                    referenceProfile.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int number_profiles = (int) snapshot.getChildrenCount();
                            referenceNumberProfiles.setValue(number_profiles);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(RegisterActivity.this, "Qualcosa è andato storto", Toast.LENGTH_LONG).show();
                        }
                    });

                    referenceProfile.child(firebaseUser.getUid()).setValue(readWriteUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "Utente registrato con successo", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else{
                                progressBar.setVisibility(View.GONE);
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