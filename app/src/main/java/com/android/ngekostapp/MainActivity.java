package com.android.ngekostapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button tombollogin;
    Button tombolregistrasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        tombollogin = findViewById(R.id.buttonLoginMain);
        tombolregistrasi = findViewById(R.id.buttonRegistrasiMain);


        tombollogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keLogin = new Intent(MainActivity.this, Login.class);
                startActivity(keLogin);
            }
        });

        tombolregistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keRegistrasi = new Intent(MainActivity.this, Registrasi.class);
                startActivity(keRegistrasi);
            }
        });
    }
}