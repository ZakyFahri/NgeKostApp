package com.android.ngekostapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import item.AdapterJenisLogin;
import item.ItemJenisLogin;

import java.util.ArrayList;
import java.util.Arrays;

import account.ngekostuser;

public class Login extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner jenislogin;

    private EditText Email, Password;
    Button login, LupaPassword;

    //Dialog PopUp
    Dialog popupLogin;
    Dialog popupLoginSalah;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth firebasepemilik;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseUser pemilik;

    //Connection Firebase
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference penggunalist = db.collection("Pengguna");
    private CollectionReference pemiliklist = db.collection("Pemilik");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.EmailLogin);
        Password = findViewById(R.id.passwordLogin);

        //Dialog
        popupLogin = new Dialog(this);
        popupLoginSalah = new Dialog(this);

        LupaPassword = findViewById(R.id.buttonLupaPass);

        login = findViewById(R.id.buttonLogin);
        jenislogin = findViewById(R.id.jenisLogin);

        firebaseAuth = FirebaseAuth.getInstance();
        firebasepemilik = FirebaseAuth.getInstance();

        //Buat Spinner Item
        ArrayList<ItemJenisLogin> ListLogin = new ArrayList<>();
        ListLogin.add(new ItemJenisLogin("Pengguna",R.drawable.user));
        ListLogin.add(new ItemJenisLogin("Pemilik Kost",R.drawable.pemilikkost));

        //Adapater untuk Spinner
        AdapterJenisLogin customAdapter = new AdapterJenisLogin(this, ListLogin);
        if(jenislogin != null){
            jenislogin.setAdapter(customAdapter);
            jenislogin.setOnItemSelectedListener(this);
        }

        LupaPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kelupaPass = new Intent(Login.this, LupaPasswordAkun.class);
                startActivity(kelupaPass);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ItemJenisLogin items = (ItemJenisLogin) adapterView.getSelectedItem();
        String jenisloginpilih = items.getJenisLoginText();
//        Toast.makeText(this, items.getJenisLoginText(), Toast.LENGTH_SHORT).show();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LoadingAlert loadingAlert = new LoadingAlert(Login.this);

                if (jenisloginpilih == "Pengguna"){
                    loadingAlert.StartAlertDialog();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadingAlert.closeAlertDialog();
                            LoginEmailPasswordUser(Email.getText().toString().trim(), Password.getText().toString().trim());
                        }
                    },3000);
                }else if (jenisloginpilih == "Pemilik Kost") {
                    loadingAlert.StartAlertDialog();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadingAlert.closeAlertDialog();
                            LoginEmailPasswordPemilik(Email.getText().toString().trim(), Password.getText().toString().trim());
                        }
                    },3000);
                }
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void LoginEmailPasswordUser(String email, String password){
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        popupLogin.setContentView(R.layout.popuploginberhasil);
                        popupLogin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        popupLogin.show();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                assert user != null;
                                final String userID = user.getUid();
                                penggunalist.whereEqualTo("userID", userID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if(error != null){

                                        }
                                        assert value != null;
                                        if(!value.isEmpty()){
                                            //Mendapatkan Data
                                            for (QueryDocumentSnapshot snapshot : value){
                                                ngekostuser userkost =ngekostuser.getInstance();
                                                userkost.setEmail(snapshot.getString("email"));
                                                userkost.setUserID(snapshot.getString("userID"));
                                                userkost.setNama(snapshot.getString("nama"));
                                                userkost.setFotoprofil(snapshot.getString("fotoprofil"));
                                                userkost.setAlamat(snapshot.getString("alamat"));
                                                userkost.setJeniskelamin(snapshot.getString("jeniskelamin"));
                                                userkost.setNomortelp(snapshot.getString("notelp"));

                                                //Menuju Dashboard User
                                                startActivity(new Intent(Login.this, DashUser.class));
                                                popupLogin.dismiss();
                                                finish();
                                            }
                                        }
                                    }
                                });
                            }
                        }, 2000);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Gagal Login
//                    Toast.makeText(Login.this, "Username dan Password salah!", Toast.LENGTH_SHORT).show();
                    popupLoginSalah.setContentView(R.layout.popuplogingagal);
                    popupLoginSalah.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popupLoginSalah.show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            popupLoginSalah.dismiss();
                        }
                    }, 2000);
                }
            });
        }else {
            if (TextUtils.isEmpty(email)){
                Email.setError("Mohon Isi Email Anda!");
                return;
            }else if (TextUtils.isEmpty(password)){
                Password.setError("Mohon Isi Password Anda");
                Toast.makeText(this, "Mohon Isi Password Anda!", Toast.LENGTH_SHORT).show();
            }}
    }

    private void LoginEmailPasswordPemilik(String email, String password){
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            firebasepemilik.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        popupLogin.setContentView(R.layout.popuploginberhasil);
                        popupLogin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        popupLogin.show();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FirebaseUser user = firebasepemilik.getCurrentUser();
                                assert user != null;
                                final String userID = user.getUid();
                                pemiliklist.whereEqualTo("userID", userID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if(error != null){

                                        }
                                        assert value != null;
                                        if(!value.isEmpty()){
                                            //Mendapatkan Data
                                            for (QueryDocumentSnapshot snapshot : value){
                                                ngekostuser pemilikkost = ngekostuser.getInstance();
                                                pemilikkost.setEmail(snapshot.getString("email"));
                                                pemilikkost.setUserID(snapshot.getString("userID"));
                                                pemilikkost.setNama(snapshot.getString("nama"));
                                                pemilikkost.setFotoprofil(snapshot.getString("fotoprofil"));
                                                pemilikkost.setAlamat(snapshot.getString("alamat"));
                                                pemilikkost.setJeniskelamin(snapshot.getString("jeniskelamin"));
                                                pemilikkost.setNomortelp(snapshot.getString("notelp"));

                                                //Menuju Dashboard User
                                                startActivity(new Intent(Login.this, DashPemilik.class));
                                                popupLogin.dismiss();
                                                finish();
                                            }
                                        }
                                    }
                                });
                            }
                        },2000);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Gagal Login
//                    Toast.makeText(Login.this, "Username dan Password salah!", Toast.LENGTH_SHORT).show();
                    popupLoginSalah.setContentView(R.layout.popuplogingagal);
                    popupLoginSalah.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popupLoginSalah.show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            popupLoginSalah.dismiss();
                        }
                    }, 1000);
                }
            });
        }else {
            if (TextUtils.isEmpty(email)){
                Email.setError("Mohon Isi Email Anda!");
                return;
            }else if (TextUtils.isEmpty(password)){
                Password.setError("Mohon Isi Password Anda!");
                Toast.makeText(this, "Mohon Isi Password Anda!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}