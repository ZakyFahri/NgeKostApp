package com.android.ngekostapp;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import model.JenisKamarKost;
import model.ListBookmark;
import model.ListKost;
import account.ngekostuser;
import ui.ListJenisKamarPemilikAdapter;
import ui.ListKostPemilikAdapter;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailTransaksiPemilik extends AppCompatActivity {

    //Widget
    ImageView gambarkostdetail, gambarkamar, gambarTransaksi, gambarPembeli;
    TextView jeniskelaminPembeli, tanggalbeli, lamakost, rangekost, notelponpembeli, namapembeli, namakostdetail;
    ImageButton tombolKembali;

    Button telponPelanggan, TerimaTransaksi, TolakTransaksi;
    LinearLayout fieldkamar;

    //String untuk Ambil Data
    private String namaKost;
    private String namaPembeli;
    private String namaPemilik;
    private String NamaKamar;
    private String hargatotal;
    private String status;
    private String notelpPembeli;
    private String fotoPembelipath;

    //Widget Transaksi  & Kamar
    TextView StatusTransaksi, namaKamarView, luaskamar, hargaKamar,TipeKamar;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference dataKost = db.collection("Data Kost");
    private CollectionReference dataKamar = db.collection("Data Kamar");
    private CollectionReference Pengguna = db.collection("Pengguna");
    private CollectionReference dataTransaksi = db.collection("Data Transaksi");

    //Gambar
    private Uri fotokost;
    private Uri fotoKamar;
    private Uri fotoProfilPembeli;
    private Uri fotoTransaksi;

    //Path Gambar
    private String gambarKostPath, gambarKamarpath, gambarprofilPembelipath, gambarTransaksipath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_detail_transaksi_pemilik);

        //KOST
        gambarPembeli = findViewById(R.id.FotoPembeli);
        gambarkostdetail = findViewById(R.id.gambarkostdetail);
        gambarkamar = findViewById(R.id.imageKamar);
        namapembeli = findViewById(R.id.namapemilikkostDetail);
        notelponpembeli = findViewById(R.id.notelppemilikkostdetail);
        jeniskelaminPembeli = findViewById(R.id.jeniskostdetail);
        rangekost = findViewById(R.id.rangeharga1);
        lamakost = findViewById(R.id.rangeharga2);
        tanggalbeli = findViewById(R.id.lamaSewa);
        namakostdetail = findViewById(R.id.NamaKostDetail);

        fieldkamar = (LinearLayout) findViewById(R.id.fieldjeniskamar);
        tombolKembali = findViewById(R.id.tombolkembali);

        //Transaksi & Kamar
        StatusTransaksi = findViewById(R.id.TampilStatus);
        luaskamar = findViewById(R.id.hasilLuas);
        gambarTransaksi = findViewById(R.id.postImageView);
        namaKamarView = findViewById(R.id.tampilnamaKamar);
        hargaKamar = findViewById(R.id.hasilHarga1);
        TipeKamar = findViewById(R.id.hasilHarga3);

//        Button telponPelanggan, TerimaTransaksi, TolakTransaksi;
        telponPelanggan = findViewById(R.id.buttonTelponPelanggan);
        TerimaTransaksi = findViewById(R.id.buttonTerimaTransaksi);
        TolakTransaksi = findViewById(R.id.buttonTolakTransaksi);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        //Bundle Intent
        Bundle extra = getIntent().getExtras();
        if(extra != null) {
            namaKost = extra.getString("namakost");
            namaPembeli = extra.getString("namaPembeli");
            namaPemilik = extra.getString("namaPemilik");
            NamaKamar = extra.getString("namaKamar");
            hargatotal = extra.getString("harga");
            status = extra.getString("status");

            LoadDataPengguna(namaPembeli);
            LoadDataKost(namaKost, namaPemilik);
            loadDataTransaksi(namaKost, namaPembeli, namaPemilik, hargatotal, NamaKamar);
            loadDataKamar(namaPemilik, NamaKamar);
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

        tombolKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TerimaTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = "TELAH DIKONFIRMASI";
                dataTransaksi.whereEqualTo("namaPembeli", namaPembeli).whereEqualTo("namaKost", namaKost).whereEqualTo("namaPemilik", namaPemilik).whereEqualTo("totalHarga", hargatotal).whereEqualTo("namaKamar", NamaKamar).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot documentquery = task.getResult();
                            DocumentSnapshot document = documentquery.getDocuments().get(0);
                            String documentID = document.getId();
                            Map<String, Object> UpdateStatus = new HashMap<>();
                            UpdateStatus.put("statusTransaksi", status);


                            dataTransaksi.document(documentID).update(UpdateStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(DetailTransaksiPemilik.this, "Transaski Telah Diterima, Mohon Menunggu hingga Pembeli Kost Mengirim Bukti Pembayaran", Toast.LENGTH_LONG).show();
                                    finish();
                                    startActivity(getIntent());
                                }
                            });
                        }
                    }
                });
            }
        });

        TolakTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = "TRANSAKSI DITOLAK";
                dataTransaksi.whereEqualTo("namaPembeli", namaPembeli).whereEqualTo("namaKost", namaKost).whereEqualTo("namaPemilik", namaPemilik).whereEqualTo("totalHarga", hargatotal).whereEqualTo("namaKamar", NamaKamar).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot documentquery = task.getResult();
                            DocumentSnapshot document = documentquery.getDocuments().get(0);
                            String documentID = document.getId();
                            Map<String, Object> UpdateStatus = new HashMap<>();
                            UpdateStatus.put("statusTransaksi", status);

                            dataTransaksi.document(documentID).update(UpdateStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(DetailTransaksiPemilik.this, "Transaski Telah Ditolak", Toast.LENGTH_LONG).show();
                                    finish();
                                    startActivity(getIntent());
                                }
                            });
                        }
                    }
                });
            }
        });

        telponPelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keWA = new Intent(Intent.ACTION_VIEW);
                keWA.setData(Uri.parse("https://wa.me/" + notelpPembeli));
                startActivity(keWA);
            }
        });

    }

    private void LoadDataKost(String namaKost, String namaPemilik){
        dataKost.whereEqualTo("namakost", namaKost).whereEqualTo("pemilik", namaPemilik).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot documentquery = task.getResult();
                    DocumentSnapshot document = documentquery.getDocuments().get(0);
                    namakostdetail.setText((String)document.get("namakost"));

                    //Gambar Kost
                    gambarKostPath = (String) document.get("gambarkost");
                    fotokost = Uri.parse(gambarKostPath);
                    Picasso.get().load(fotokost).into(gambarkostdetail);
                }
            }
        });
    }

    private void LoadDataPengguna(String namaPembeli){
        Pengguna.whereEqualTo("nama", namaPembeli).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot documentquery = task.getResult();
                    DocumentSnapshot document = documentquery.getDocuments().get(0);
                    namapembeli.setText((String) document.get("nama"));
                    notelponpembeli.setText((String) document.get("notelp"));
                    jeniskelaminPembeli.setText((String) document.get("jeniskelamin"));

                    notelpPembeli = (String) document.get("notelp");

                    //Load Profil
                    gambarprofilPembelipath = (String) document.get("fotoprofil");
                    fotoProfilPembeli = Uri.parse(gambarprofilPembelipath);
                    Picasso.get().load(fotoProfilPembeli).into(gambarPembeli);
                }
            }
        });
    }

    private void loadDataTransaksi(String namaKost, String namaPembeli, String namaPemilik, String hargatotal, String NamaKamar){
        dataTransaksi.whereEqualTo("namaPembeli", namaPembeli).whereEqualTo("namaKost", namaKost).whereEqualTo("namaPemilik", namaPemilik).whereEqualTo("totalHarga", hargatotal).whereEqualTo("namaKamar", NamaKamar).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot documentquery = task.getResult();
                    DocumentSnapshot document = documentquery.getDocuments().get(0);
                    rangekost.setText((String) document.get("totalHarga"));
                    lamakost.setText((String) document.get("lamaSewa"));
                    tanggalbeli.setText((String) document.get("waktuDitambahkan"));
                    StatusTransaksi.setText((String) document.get("statusTransaksi"));

                    if (StatusTransaksi.getText().toString().contains("MENUNGGU KONFIRMASI")){
                        StatusTransaksi.setTextColor(Color.RED);
                        gambarTransaksi.setVisibility(View.GONE);
                    }else if (StatusTransaksi.getText().toString().contains("TELAH DIKONFIRMASI")){
                        TerimaTransaksi.setVisibility(View.GONE);
                        TolakTransaksi.setVisibility(View.GONE);
                        gambarTransaksi.setVisibility(View.GONE);
                        StatusTransaksi.setTextColor(Color.parseColor("#DA9A1F"));
                    }else if (StatusTransaksi.getText().toString().contains("TRANSAKSI SELESAI")){
                        TerimaTransaksi.setVisibility(View.GONE);
                        TolakTransaksi.setVisibility(View.GONE);
                        telponPelanggan.setVisibility(View.GONE);

                        //Ambil Gambar
                        gambarTransaksipath = (String) document.get("fotoTransaksi");
                        fotoTransaksi = Uri.parse(gambarTransaksipath);
                        Picasso.get().load(fotoTransaksi).into(gambarTransaksi);

                        StatusTransaksi.setTextColor(Color.parseColor("#009606"));
                    }else if (StatusTransaksi.getText().toString().contains("TRANSAKSI DITOLAK")){
                        StatusTransaksi.setTextColor(Color.RED);
                        TerimaTransaksi.setVisibility(View.GONE);
                        TolakTransaksi.setVisibility(View.GONE);
                        telponPelanggan.setVisibility(View.GONE);
                        gambarTransaksi.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void loadDataKamar(String namaPemilik,String namaKamar){
        dataKamar.whereEqualTo("namaPemilik", namaPemilik).whereEqualTo("namaKamar", namaKamar).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot documentquery = task.getResult();
                    if (!documentquery.isEmpty()){
                        DocumentSnapshot document = documentquery.getDocuments().get(0);
                        namaKamarView.setText((String) document.get("namaKamar"));
                        hargaKamar.setText((String) document.get("hargaKonversi"));
                        TipeKamar.setText((String) document.get("lamaSewa"));
                        luaskamar.setText((String) document.get("luasKamar"));

                        //Gambar Kamar
                        gambarKamarpath = (String) document.get("gambarKamar");
                        fotoKamar = Uri.parse(gambarKamarpath);
                        Picasso.get().load(fotoKamar).into(gambarkamar);
                    }else if (documentquery.isEmpty()){
                        Toast.makeText(DetailTransaksiPemilik.this, "Data Kamar telah Anda Hapus", Toast.LENGTH_SHORT).show();
                        fieldkamar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}