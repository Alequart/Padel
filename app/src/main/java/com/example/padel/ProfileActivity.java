package com.example.padel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewWelcome, textViewName, textViewSurname, textViewDateOfBirth, textViewLivello, textViewEmail;
    private ProgressBar progressBar;

    private ImageView profilePic;
    private Uri imageUri;

    private String name, surname, dateOfBirth, livello, email;

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;

    private FirebaseStorage storage;
    private StorageReference storageReference;


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

        profilePic = findViewById(R.id.profile_pic);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        if(firebaseUser == null){
            Toast.makeText(ProfileActivity.this, "Qualcosa è andato storto", Toast.LENGTH_SHORT).show();
        } else{
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        storageReference.child("images/" + firebaseUser.getUid()).getDownloadUrl().addOnSuccessListener(uri -> {
            new DownloadImageTask().execute(uri.toString());
        }).addOnFailureListener(exception ->{
            exception.printStackTrace();
        });

    }

    private void choosePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture(){

        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("Caricamento immagine...");
        progressDialog.show();

        StorageReference riverRef = storageReference.child("images/" + firebaseUser.getUid());

        riverRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "Immagine caricata", Snackbar.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Errore. Impossibile caricare l'immagine", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Percentuale caricamento: " + (int) progressPercent + "%");
            }
        });


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
            Intent intent = new Intent(ProfileActivity.this, CircoloActivity.class);
            startActivity(intent);
            finish();
        } else if(id == R.id.menu_logOut){
            authProfile.signOut();
            Toast.makeText(ProfileActivity.this, "Effettuo logout", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if(id == R.id.menu_delete){
            Intent intent = new Intent(ProfileActivity.this, DeleteAccountActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                profilePic.setImageBitmap(result);
            } else {
                Toast.makeText(ProfileActivity.this, "Errore. Impossibile caricare immagine", Toast.LENGTH_SHORT).show();
            }
        }
    }
}