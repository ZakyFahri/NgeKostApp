package com.android.ngekostapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class LupaPasswordAkun extends AppCompatActivity {
    EditText Email;
    Button lupaPasswordAction;

    //Firebase
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_lupa_password_akun);

        Email = findViewById(R.id.EmailLogin);
        lupaPasswordAction = findViewById(R.id.buttonUbahPass);

        firebaseAuth = FirebaseAuth.getInstance();

        lupaPasswordAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Email.getText().toString().trim();

                if (TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(LupaPasswordAkun.this, "Masukkan Email yang Anda Lupa Passwordnya!", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(LupaPasswordAkun.this, "Link Reset Password berhasil dikirim ke email anda!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LupaPasswordAkun.this, "Ada sesuatu yang salah!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}