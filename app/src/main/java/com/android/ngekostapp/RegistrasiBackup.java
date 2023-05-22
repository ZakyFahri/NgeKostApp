package com.android.ngekostapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import account.ngekostuser;
import item.AdapterJenisLogin;
import item.ItemJenisLogin;

public class RegistrasiBackup extends AppCompatActivity {
    private static final int GALLERY_CODE = 1;

    EditText nama, alamat, nomortelpon, email, password, jenisKelamin;
    Button registrasi, choosephoto;
    //Firebase Authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Koneksi Firebase Bahan Login
    private StorageReference storageReference;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference akunuser = database.collection("Pengguna");
    private CollectionReference akunpemilik = database.collection("Pemilik");

    private Uri fotoprofil;

//    Spinner jenisKelamin; BUG
    Spinner jenislogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registrasi);

        //Firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        //Foto Profil
        choosephoto = findViewById(R.id.buttonchoosefoto);

        //Widget Lain
        registrasi = findViewById(R.id.buttonRegistrasi);
        nama = findViewById(R.id.inputNama);
        alamat = findViewById(R.id.inputAlamat);
        nomortelpon = findViewById(R.id.inputnoTelp);
        email = findViewById(R.id.EmailRegistrasi);
        password = findViewById(R.id.passwordRegistrasi);
        jenisKelamin = findViewById(R.id.inputjeniskelamin);

        //Spinner
//        jenisKelamin = findViewById(R.id.inputjenisKelamin); BUG
        jenislogin = findViewById(R.id.jenisLoginRegistrasi);

//
//                                 BUG SPINNER
//        // ITEM KELAMIN //
//        //Buat Spinner Item
//        ArrayList<ItemJenisKelamin> ListKelamin = new ArrayList<>();
//        ListKelamin.add(new ItemJenisKelamin("Pilih Jenis Kelamin", R.drawable.gender));
//        ListKelamin.add(new ItemJenisKelamin("Laki-Laki",R.drawable.man));
//        ListKelamin.add(new ItemJenisKelamin("Perempuan",R.drawable.woman));
//
//        //Adapater untuk Spinner
//        AdapterJenisKelamin AdapterKelamin = new AdapterJenisKelamin(this, ListKelamin);
//        if(jenisKelamin != null){
//            jenisKelamin.setAdapter(AdapterKelamin);
//        }
//        // END ITEM KELAMIN //



        // ITEM LOGIN //
        //Buat Spinner Item
        ArrayList<ItemJenisLogin> ListLogin = new ArrayList<>();
        ListLogin.add(new ItemJenisLogin("Pengguna",R.drawable.user));
        ListLogin.add(new ItemJenisLogin("Pemilik Kost",R.drawable.pemilikkost));

        //Adapater untuk Spinner
        AdapterJenisLogin customAdapter = new AdapterJenisLogin(this, ListLogin);
        if(jenislogin != null){
            jenislogin.setAdapter(customAdapter);
        }

        choosephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mendapat Gambar
                Intent galleryIntent =new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //User telah Login
                }else {
                    //Belum Menjadi User
                }
            }
        };

        jenislogin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterlogin, View view, int i, long l) {
                ItemJenisLogin items = (ItemJenisLogin) adapterlogin.getSelectedItem();
                String jenisloginpilih = items.getJenisLoginText();

                registrasi.setOnClickListener(new View.OnClickListener() {
                    @Override
                        public void onClick(View view) {
                            if(!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())){
                                String namainput= nama.getText().toString().trim();
                                String alamatinput = alamat.getText().toString().trim();
                                String emailinput = email.getText().toString().trim();
                                String passwordinput = password.getText().toString().trim();
                                String genderpilih = jenisKelamin.getText().toString();
                                String jenislogininput = jenisloginpilih;

                                if (jenisloginpilih == "Pengguna"){
                                    if (genderpilih.equals("L")){
                                        jenisKelamin.setText("Laki-Laki");
                                    }else if (genderpilih.equals("P")){
                                        jenisKelamin.setText("Perempuan");
                                    }
                                    String gender = jenisKelamin.getText().toString();
                                    CreateUserEmailAccount(namainput, alamatinput, emailinput, passwordinput, jenislogininput, gender);
                                }else if (jenisloginpilih == "Pemilik Kost") {
                                    if (genderpilih.equals("L")){
                                        jenisKelamin.setText("Laki-Laki");
                                    }else if (genderpilih.equals("P")){
                                        jenisKelamin.setText("Perempuan");
                                    }
                                    String gender = jenisKelamin.getText().toString();
                                    CreatePemilikEmailAccount(namainput, alamatinput, emailinput, passwordinput, jenislogininput, gender);
                                }
                            }else{
                                Toast.makeText(RegistrasiBackup.this, "Kosong", Toast.LENGTH_SHORT).show();
                            }
                    }
                });

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        int jenisloginpilih = jenislogin.getSelectedItemPosition();

//        jenisKelamin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                ItemJenisKelamin itemsKelamin = (ItemJenisKelamin) adapterView.getSelectedItem();
//                String genderpilih = itemsKelamin.getJenisKelaminText();
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

    }

//    CreateUserEmailAccount(namaPengguna, alamatPengguna, emailPengguna, passwordPengguna, jeniskelaminPengguna);
    private void CreateUserEmailAccount(String namaPengguna, String alamatPengguna, String emailPengguna, String passwordPengguna ,String jenisloginPengguna, String jeniskelaminPengguna){
        if (!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString()) && fotoprofil!=null){
            firebaseAuth.createUserWithEmailAndPassword(emailPengguna, passwordPengguna).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //Pindah Ke Activity Selanjutnya
                        user = firebaseAuth.getCurrentUser();
                        assert user != null;
                        final String userID =user.getUid();

                        //Usermap untuk membuat user collection
                        Map<String, String> userObj = new HashMap<>();
                        userObj.put("userID", userID);
                        userObj.put("nama", namaPengguna);
                        userObj.put("alamat", alamatPengguna);
                        userObj.put("jeniskelamin", jeniskelaminPengguna);
                        userObj.put("email", emailPengguna);
                        userObj.put("jenislogin", jenisloginPengguna);
//                        userObj.put("password", passwordPengguna);

                        //Menambah Pengguna ke Firestore
                        akunuser.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(Objects.requireNonNull(task.getResult().exists())){
                                            String email = task.getResult().getString("email");

                                            //Jika berhasil registras, data-data registrasi ditampilkan ke activity selanjutnya
                                            ngekostuser pengguna = ngekostuser.getInstance();
                                            pengguna.setUserID(userID);
                                            pengguna.setNama(namaPengguna);
                                            pengguna.setAlamat(alamatPengguna);
                                            pengguna.setJeniskelamin(jeniskelaminPengguna);
                                            pengguna.setEmail(emailPengguna);
//                                            pengguna.setPassword(passwordPengguna);

                                            Intent kedash = new Intent(RegistrasiBackup.this, DashUser.class);
                                            startActivity(kedash);
                                        }else{

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegistrasiBackup.this, "Error :" + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    private void CreatePemilikEmailAccount(String namaPemilik, String alamatPemilik, String emailPemilik, String passwordPemilik ,String jenisloginPemilik, String jeniskelaminPemilik){
        if (!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())){
            firebaseAuth.createUserWithEmailAndPassword(emailPemilik, passwordPemilik).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //Pindah Ke Activity Selanjutnya
                        user = firebaseAuth.getCurrentUser();
                        assert user != null;
                        final String userID =user.getUid();

                        //Usermap untuk membuat user collection
                        Map<String, String> userObj = new HashMap<>();
                        userObj.put("userID", userID);
                        userObj.put("nama", namaPemilik);
                        userObj.put("alamat", alamatPemilik);
                        userObj.put("jeniskelamin", jeniskelaminPemilik);
                        userObj.put("email", emailPemilik);
//                        userObj.put("password", passwordPengguna);

                        //Menambah Pengguna ke Firestore
                        akunpemilik.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(Objects.requireNonNull(task.getResult().exists())){
                                            String email = task.getResult().getString("email");

                                            //Jika berhasil registras, data-data registrasi ditampilkan ke activity selanjutnya
                                            ngekostuser pengguna = ngekostuser.getInstance();
                                            pengguna.setUserID(userID);
                                            pengguna.setNama(namaPemilik);
                                            pengguna.setAlamat(alamatPemilik);
                                            pengguna.setJeniskelamin(jeniskelaminPemilik);
                                            pengguna.setEmail(emailPemilik);
//                                            pengguna.setPassword(passwordPemilik);

                                            Intent kedash = new Intent(RegistrasiBackup.this, DashPemilik.class);
                                            kedash.putExtra("nama", namaPemilik);
                                            startActivity(kedash);
                                        }else{

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegistrasiBackup.this, "Error :" + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        user =firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}