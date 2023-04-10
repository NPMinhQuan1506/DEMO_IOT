package com.example.iot_security_remote;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {
    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-z]{2,}";
    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnLogin;
    private TextView txtValidate;
    //Validation
    private Boolean isValidEmail = false;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Map properties in .xml file
        txtEmail = (EditText) findViewById(R.id.editTextTextEmailAddress2);
        txtPassword = (EditText) findViewById(R.id.edPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("Account").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // do some stuff once
                        Iterable<DataSnapshot> children = snapshot.getChildren();

                        // shake hands with each of them.'
                        for (DataSnapshot child : children) {
                            String pass = child.getValue(String.class);
                            String email = child.getKey().toString();
                            String MyUS = txtEmail.getText().toString();
                            String MyPass = txtPassword.getText().toString();
                            if(email.equals(MyUS) && pass.equals(MyPass)) {
                                Intent intent = new Intent(MainActivity.this, Remote.class);
                                intent.putExtra("email", txtEmail.getText().toString().trim());
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Sai thong tin dang nhap",
                                        Toast.LENGTH_LONG).show();
                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }
}