package com.android.ngekostapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Bundle;
import android.widget.Spinner;

import item.AdapterJenisKelamin;
import item.ItemJenisKelaminKost;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import account.ngekostuser;
import kotlin.collections.UCollectionsKt;
import model.ListKost;

public class TambahKostPemilik extends AppCompatActivity {
    private static final int GALLERY_CODE = 1;
    //Widget
    EditText namakost, latitude, longitude, rangeharga, alamatkost, notelp, deskripsikost;
    ImageView imageKost;
    ImageButton tombolKembali;
    Button choosephoto, addkost;
    Spinner jenisKelaminKost;

    //
    Dialog popupBerhasil;
    Dialog popupGagal;

    //UserID dan Nama Pemilik Kost
    private String userID;
    private String nama;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //FireStore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference pemilik = database.collection("Pemilik");
    private CollectionReference datakost = database.collection("Data Kost");

    //Gambar
    private Uri gambarkost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_tambah_kost_pemilik);

        namakost = findViewById(R.id.inputNamaKost);
        latitude = findViewById(R.id.inputLatitude);
        longitude = findViewById(R.id.inputLongitude);
        rangeharga = findViewById(R.id.inputHarga);
        alamatkost = findViewById(R.id.inputAlamatKost);
        notelp = findViewById(R.id.InputNomorTelepon);
        deskripsikost = findViewById(R.id.InputDeskripsi);
        imageKost = findViewById(R.id.postImageView);

        tombolKembali = findViewById(R.id.tombolkembali);

        popupBerhasil = new Dialog(this);
        popupGagal = new Dialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        choosephoto = findViewById(R.id.buttonambilfoto);
        addkost = findViewById(R.id.buttonTambahKost);

        jenisKelaminKost = findViewById(R.id.JenisKelaminKost);

        notelp.setText("+62");

        ArrayList<ItemJenisKelaminKost> ListKelamin = new ArrayList<>();
        ListKelamin.add(new ItemJenisKelaminKost("Laki-Laki",R.drawable.man));
        ListKelamin.add(new ItemJenisKelaminKost("Perempuan",R.drawable.woman));
        ListKelamin.add(new ItemJenisKelaminKost("Campur",R.drawable.campur));

        //Adapater untuk Spinner
        AdapterJenisKelamin AdapterKelamin = new AdapterJenisKelamin(this, ListKelamin);
        if(jenisKelaminKost != null){
            jenisKelaminKost.setAdapter(AdapterKelamin);
        }

        if (ngekostuser.getInstance()!=null){
            nama = ngekostuser.getInstance().getNama();
            userID = ngekostuser.getInstance().getUserID();
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user!=null){

                }else{

                }
            }
        };

        choosephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mendapat Gambar
                Intent galleryIntent =new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
            }
        });

        jenisKelaminKost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ItemJenisKelaminKost items = (ItemJenisKelaminKost) adapterView.getSelectedItem();
                String jeniskelaminkost = items.getJenisKelaminText();

                addkost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final LoadingAlert loadingAlert = new LoadingAlert(TambahKostPemilik.this);
                        loadingAlert.StartAlertDialog();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadingAlert.closeAlertDialog();
                                simpanKost(jeniskelaminkost);

                            }
                        }, 5000);
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tombolKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void simpanKost(String jeniskelaminkost){
        final String inputkost = namakost.getText().toString().trim();
        final String inputlatitude = latitude.getText().toString().trim();
        final String inputlongitude = longitude.getText().toString().trim();
        final String inputrange = rangeharga.getText().toString().trim();
        final String inputalamat = alamatkost.getText().toString().trim();
        final String inputNotelp = notelp.getText().toString().trim();
        final String inputdeskripsi = deskripsikost.getText().toString().trim();
        final String inputjeniskelaminkost =  jeniskelaminkost;

        if (!TextUtils.isEmpty(inputkost) && !TextUtils.isEmpty(inputlatitude) && !TextUtils.isEmpty(inputlongitude) && !TextUtils.isEmpty(inputrange) && !TextUtils.isEmpty(inputalamat) && !TextUtils.isEmpty(inputNotelp) && !TextUtils.isEmpty(inputdeskripsi) && gambarkost!=null){
            //Path gambar kost tersimpan dari gambar ke storage path gambar_kost/gambarkost_anda.png
            final StorageReference filepath = storageReference.child("gambar_kost").child("gambarkost" + Timestamp.now().getNanoseconds());

            //Upload Gambar
            filepath.putFile(gambarkost).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String gambarkost = uri.toString();

                            //Buat Objek Kost
                            ListKost listkost = new ListKost();
                            listkost.setNamakost(inputkost);
                            listkost.setSearch(namakost.getText().toString().trim().toLowerCase());
                            listkost.setLatitude(inputlatitude);
                            listkost.setLongitude(inputlongitude);
                            listkost.setJeniskelaminkost(inputjeniskelaminkost);
                            listkost.setRangeharga(inputrange);
                            listkost.setAlamat(inputalamat);
                            listkost.setNomortelpon(inputNotelp);
                            listkost.setDeksripsikost(inputdeskripsi);
                            listkost.setPemilik(nama);
                            listkost.setUserID(userID);
                            listkost.setGambarkost(gambarkost);
                            listkost.setTimeAdded(new Timestamp(new Date()));

                            //Invoking Collection Reference
                            datakost.add(listkost).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    popupBerhasil.setContentView(R.layout.popuptambahkostberhasil);
                                    popupBerhasil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    popupBerhasil.show();

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            popupBerhasil.dismiss();
                                            finish();
                                        }
                                    }, 2000);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    popupGagal.setContentView(R.layout.popuptambahkostgagal);
                                    popupGagal.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    popupGagal.show();

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            popupGagal.dismiss();
                                            finish();
                                        }
                                    }, 2000);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if (data !=null){
                gambarkost = data.getData(); //Mendapatkan Path Aktual
                imageKost.setImageURI(gambarkost); //Menampilkan Gambar
            }
        }
    }

    @Override
    protected void onStart(){
        super.onStart();

        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop(){
        super.onStop();

        if(firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}