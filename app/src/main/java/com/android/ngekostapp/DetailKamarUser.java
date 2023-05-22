package com.android.ngekostapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import android.os.Bundle;

import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import account.ngekostuser;

public class DetailKamarUser extends AppCompatActivity {
    //Widget
    private static final int GALLERY_CODE = 1;

    ImageView gambarKamar;
    TextView namakamar, harga, lamasewa, jumlahkamar, luasKamar, fasilitas;
    Button keSewa;
    ImageButton buttonKembali;

    //Mendapatkan Nama Kost dan Foto Kamar
    private String namaKost;
    private String namaKamar;
    private String fotoKamarpath;
    private String namaPemilik;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference DataKost = db.collection("Data Kost");
    private CollectionReference DataKamar = db.collection("Data Kamar");

    private Uri FotoKamar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_detail_kamar_user);

        namakamar = findViewById(R.id.tvJenisKamar);
        gambarKamar = findViewById(R.id.postImageKost);
        harga = findViewById(R.id.hasilHarga1);
        lamasewa = findViewById(R.id.hasilHarga3);
        jumlahkamar = findViewById(R.id.hasilKamar);
        luasKamar = findViewById(R.id.hasilLuas);
        fasilitas = findViewById(R.id.hasilFasilitas);

        buttonKembali = findViewById(R.id.tombolkembali);

        keSewa = findViewById(R.id.buttonSewa);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            namaKost = extra.getString("namakost");
            namaKamar = extra.getString("namaKamar");
            LoadDetailKamar(namaKamar);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user!=null){

                }else{

                }
            }
        };

        keSewa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keSewaKamar = new Intent(DetailKamarUser.this, SewaKamarUser.class);
                keSewaKamar.putExtra("namakost", namaKost);
                keSewaKamar.putExtra("namakamar", namaKamar);
                keSewaKamar.putExtra("namapemilik", namaPemilik);
                keSewaKamar.putExtra("kamar", 1);
                startActivity(keSewaKamar);
            }
        });

        buttonKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void LoadDetailKamar(String namaKamar){
        DataKamar.whereEqualTo("namaKamar", namaKamar).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot documenyquery = task.getResult();
                    DocumentSnapshot document = documenyquery.getDocuments().get(0);
                    namakamar.setText((String)document.get("namaKamar"));
                    harga.setText((String)document.get("hargaKonversi"));
                    lamasewa.setText((String)document.get("lamaSewa"));
                    jumlahkamar.setText((String)document.get("kamarTersedia"));
                    luasKamar.setText((String)document.get("luasKamar"));
                    fasilitas.setText((String)document.get("fasilitasKamar"));
                    namaPemilik = (String)document.get("namaPemilik");

                    //Gambar Kamar
                    fotoKamarpath = (String)document.get("gambarKamar");
                    FotoKamar = Uri.parse(fotoKamarpath);
                    Picasso.get().load(FotoKamar).into(gambarKamar);
                }
            }
        });
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