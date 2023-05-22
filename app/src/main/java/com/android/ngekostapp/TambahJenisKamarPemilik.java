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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import account.ngekostuser;
import model.ListKost;
import model.JenisKamarKost;

public class TambahJenisKamarPemilik extends AppCompatActivity {
    private static final int GALLERY_CODE = 1;

    Dialog popupKamarberhasil;
    Dialog popupKamarGagal;

    //Widget
    EditText namakamar, hargakamar, lamasewakamar, kamartersedia, luaskamar, fasilitas;
    ImageView imageKamar;
    Button choosephoto, addKamar;
    ImageButton tombolKembali;

    //Ambil Data untuk simpan ke Class
    private String namaKost;
    private String DocumentKamar;
    private String namaPemilik;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;


    //FireStore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference DataKamarKost = database.collection("Data Kamar");
    private CollectionReference datakost = database.collection("Data Kost");
    private CollectionReference pemilik = database.collection("Pemilik");

    //Gambar
    private Uri gambarkamar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_tambah_jenis_kamar_pemilik);

        final LoadingAlert loadingAlert = new LoadingAlert(TambahJenisKamarPemilik.this);

        namakamar = findViewById(R.id.NamaKamar);
        hargakamar = findViewById(R.id.HargaKamar);
        lamasewakamar = findViewById(R.id.LamaSewaKamar);
        kamartersedia = findViewById(R.id.JumlahKamarTersedia);
        luaskamar = findViewById(R.id.LuasKamar);
        fasilitas = findViewById(R.id.FasilitasKamar);

        tombolKembali = findViewById(R.id.tombolkembali);

        //Dialog
        popupKamarberhasil = new Dialog(this);
        popupKamarGagal = new Dialog(this);

        imageKamar = findViewById(R.id.postImageView);

        choosephoto = findViewById(R.id.buttonambilfoto);
        addKamar = findViewById(R.id.TambahKamar);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            namaKost = extra.getString("namakost");
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

        addKamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingAlert.StartAlertDialog();
                SimpanKamar(namaKost);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingAlert.closeAlertDialog();
                    }
                },4000);
            }
        });

        tombolKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void SimpanKamar(String namaKost){
        final String inputNamaKamar = namakamar.getText().toString().trim();
        final String inputHarga = hargakamar.getText().toString().trim();
        final String LamaSewa = lamasewakamar.getText().toString().trim();
        final String JumlahKamar = kamartersedia.getText().toString().trim();
        final String LuasKamar = luaskamar.getText().toString().trim();
        final String FasilitasKamar = fasilitas.getText().toString().trim();

        user = firebaseAuth.getCurrentUser();
        assert user != null;
        final String userID = user.getUid();

        pemilik.whereEqualTo("userID", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot documentQuery = task.getResult();
                    DocumentSnapshot document = documentQuery.getDocuments().get(0);
                    namaPemilik = (String)document.get("nama");

                    if(!TextUtils.isEmpty(inputNamaKamar) && !TextUtils.isEmpty(inputHarga) && !TextUtils.isEmpty(LamaSewa) && !TextUtils.isEmpty(JumlahKamar) && !TextUtils.isEmpty(LuasKamar) && !TextUtils.isEmpty(FasilitasKamar) && gambarkamar != null){
                        final StorageReference filepath = storageReference.child("gambar-kamar").child("gambarkamar_" + namaKost + "_" + Timestamp.now().getNanoseconds());

                        //Upload
                        filepath.putFile(gambarkamar).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String gambarkamar = uri.toString();

                                        Locale localID = new Locale("IND", "ID");
                                        NumberFormat numberFormat = NumberFormat.getNumberInstance(localID);
                                        double konversi = Double.parseDouble(hargakamar.getText().toString());

                                        String hargaKonversi = "Rp" + numberFormat.format(konversi);

                                        //Objek Kamar
                                        JenisKamarKost tambahJenisKamar = new JenisKamarKost();
                                        tambahJenisKamar.setNamaKamar(inputNamaKamar);
                                        tambahJenisKamar.setHarga(inputHarga);
                                        tambahJenisKamar.setHargaKonversi(hargaKonversi);
                                        tambahJenisKamar.setLamaSewa(LamaSewa);
                                        tambahJenisKamar.setKamarTersedia(JumlahKamar);
                                        tambahJenisKamar.setLuasKamar(LuasKamar);
                                        tambahJenisKamar.setFasilitasKamar(FasilitasKamar);
                                        tambahJenisKamar.setNamakost(namaKost);
                                        tambahJenisKamar.setGambarKamar(gambarkamar);
                                        tambahJenisKamar.setNamaPemilik(namaPemilik);
                                        tambahJenisKamar.setTimeadded(new Timestamp(new Date()));

                                        //Invoking Collection Reference
                                        DataKamarKost.add(tambahJenisKamar).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                popupKamarberhasil.setContentView(R.layout.popupkamarberhasil);
                                                popupKamarberhasil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                popupKamarberhasil.show();

                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        SimpanDokumenKamar(inputNamaKamar);
                                                        Intent kembaliKeDetailKost = new Intent(TambahJenisKamarPemilik.this, DetailKamarPemilik.class);
                                                        kembaliKeDetailKost.putExtra("namakost", namaKost);
                                                        kembaliKeDetailKost.putExtra("namaKamar", inputNamaKamar);
                                                        popupKamarberhasil.dismiss();
                                                        finish();
                                                    }
                                                },2000);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                popupKamarGagal.setContentView(R.layout.popupkamargagal);
                                                popupKamarGagal.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                popupKamarGagal.show();
                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        popupKamarGagal.dismiss();
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
            }
        });
    }

    private void SimpanDokumenKamar(String namaKamar){
        DataKamarKost.whereEqualTo("namaKamar", namaKamar).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    String DocumentID = documentSnapshot.getId();
                    Map<String, Object> TambahDokumen = new HashMap<>();
                    TambahDokumen.put("DocID", DocumentID);
                    DataKamarKost.document(DocumentID).update(TambahDokumen);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TambahJenisKamarPemilik.this, "Menyimpan Dokumen Kamar Gagal!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if (data !=null){
                gambarkamar = data.getData(); //Mendapatkan Path Aktual
                imageKamar.setImageURI(gambarkamar); //Menampilkan Gambar
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