package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText et1, et2, et3;
    Button but1, but2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        but1 = findViewById(R.id.but1);
        but2 = findViewById(R.id.but2);
        mAuth = FirebaseAuth.getInstance();

        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });
    }


    public void signup() {
        mAuth.createUserWithEmailAndPassword(et1.getText().toString(), et2.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "SUccesful", Toast.LENGTH_LONG).show();
                    FirebaseDatabase.getInstance().getReference().child("my_users").child(task.getResult().getUser().getUid()).child("username").setValue(et3.getText().toString());

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(et3.getText().toString())
                            .build();

                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this,"Usernam eupdated",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                    mainpage();
                } else {
                    Toast.makeText(MainActivity.this,"Unsussesful", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void signin() {
        mAuth.signInWithEmailAndPassword(et1.getText().toString(), et2.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Succesfully loggedin", Toast.LENGTH_LONG).show();
                    mainpage();


                } else {
                    Toast.makeText(MainActivity.this, "Not Suceesfull login", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void mainpage() {
        Intent intent = new Intent(MainActivity.this, social.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser current = mAuth.getCurrentUser();
        if (current != null) {
            Toast.makeText(MainActivity.this, "AUTOMATIC", Toast.LENGTH_LONG).show();
            mainpage();
        }
    }
}

