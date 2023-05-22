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
import java.util.List;

public class DetailTransaksiUser extends AppCompatActivity {
    //Widget
    ImageView gambarkostdetail, gambarkamar;
    TextView jeniskost, lamakost, alamatkost, rangekost, notelponkost, namapemilikkost, namakostdetail;
    ImageButton tombolKembali;

    ImageButton buttontelpon;
    Button DetailKost, Pembayaran, keUlasan, keDetailKamar;

    LinearLayout jeniskamar;

    //String untuk Ambil Data
    private String namaKost;
    private String namaPembeli;
    private String namaPemilik;
    private String NamaKamar;
    private String hargatotal;
    private String status;
    private String notelpPemilik;

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

    //Path Gambar
    private String gambarKostPath, gambarKamarpath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_detail_transaksi_user);
        //KOST
        gambarkostdetail = findViewById(R.id.gambarkostdetail);
        gambarkamar = findViewById(R.id.imageKamar);
        namapemilikkost = findViewById(R.id.namapemilikkostDetail);
        notelponkost = findViewById(R.id.notelppemilikkostdetail);
        jeniskost = findViewById(R.id.jeniskostdetail);
        rangekost = findViewById(R.id.rangeharga);
        lamakost = findViewById(R.id.lamaSewa);
        alamatkost = findViewById(R.id.alamatkostdetail);
        namakostdetail = findViewById(R.id.NamaKostDetail);

        buttontelpon = findViewById(R.id.pintelponkost);
        tombolKembali = findViewById(R.id.tombolkembali);

        //Transaksi & Kamar
        jeniskamar = (LinearLayout) findViewById(R.id.fieldjeniskamar);

        StatusTransaksi = findViewById(R.id.TampilStatus);
        luaskamar = findViewById(R.id.hasilLuas);
        namaKamarView = findViewById(R.id.tampilnamaKamar);
        hargaKamar = findViewById(R.id.hasilHarga1);
        TipeKamar = findViewById(R.id.hasilHarga3);

        //Button
        buttontelpon = findViewById(R.id.pintelponkost);
        DetailKost = findViewById(R.id.buttonKeDetailKost);
        Pembayaran = findViewById(R.id.buttonBayarKost);
        keUlasan = findViewById(R.id.buttonReview);
        keDetailKamar = findViewById(R.id.buttonkeKamar);

        //Button Visibility
        Pembayaran.setVisibility(View.GONE);
        keUlasan.setVisibility(View.GONE);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        //Bundle Intent
        Bundle extra = getIntent().getExtras();
        if(extra != null){
            namaKost = extra.getString("namakost");
            namaPembeli = extra.getString("namaPembeli");
            namaPemilik = extra.getString("namaPemilik");
            NamaKamar = extra.getString("namaKamar");
            hargatotal = extra.getString("harga");
            status = extra.getString("status");

            LoadDataKost(namaKost, namaPemilik);
            loadDataTransaksi(namaKost, namaPembeli, namaPemilik, hargatotal, NamaKamar);
            loadDataKamar(namaPemilik, NamaKamar);
            loadDataReview(namaKost, namaPembeli);
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

        DetailKost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keDetailKostUser = new Intent(DetailTransaksiUser.this, DetailKostUser.class);
                keDetailKostUser.putExtra("namakost", namaKost);
                keDetailKostUser.putExtra("daripeta", "Tidak");
                startActivity(keDetailKostUser);
            }
        });

        Pembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kePembayaran = new Intent(DetailTransaksiUser.this, PembayaranKostUser.class);
                kePembayaran.putExtra("namakost", namaKost);
                kePembayaran.putExtra("namaKamar", NamaKamar);
                kePembayaran.putExtra("harga", hargatotal);
                kePembayaran.putExtra("namaPembeli", namaPembeli);
                kePembayaran.putExtra("Pemilik", namaPemilik);
                startActivity(kePembayaran);
            }
        });

        keDetailKamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keDetailkamar = new Intent(DetailTransaksiUser.this, DetailKamarUser.class);
                keDetailkamar.putExtra("namakost", namaKost);
                keDetailkamar.putExtra("namaKamar", NamaKamar);
                startActivity(keDetailkamar);
            }
        });

        keUlasan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keReview = new Intent(DetailTransaksiUser.this, BeriReviewUser.class);
                keReview.putExtra("namakost", namaKost);
                keReview.putExtra("namaKamar", NamaKamar);
                keReview.putExtra("harga", hargatotal);
                keReview.putExtra("namaPembeli", namaPembeli);
                keReview.putExtra("Pemilik", namaPemilik);
                startActivity(keReview);
            }
        });

        tombolKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttontelpon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keWA = new Intent(Intent.ACTION_VIEW);
                keWA.setData(Uri.parse("https://wa.me/" + notelpPemilik));
                startActivity(keWA);
            }
        });
    }

    private void LoadDataKost(String namaKost, String namaPemilik){
        dataKost.whereEqualTo("namakost", namaKost).whereEqualTo("pemilik", namaPemilik).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    QuerySnapshot documentquery = task.getResult();
                    DocumentSnapshot document = documentquery.getDocuments().get(0);
                    jeniskost.setText((String)document.get("jeniskelaminkost"));
                    namakostdetail.setText((String)document.get("namakost"));
                    notelponkost.setText((String)document.get("nomortelpon"));
                    namapemilikkost.setText((String)document.get("pemilik"));
                    alamatkost.setText((String)document.get("alamat"));

                    notelpPemilik = (String) document.get("nomortelpon");

                    //Gambar Kost
                    gambarKostPath = (String) document.get("gambarkost");
                    fotokost = Uri.parse(gambarKostPath);
                    Picasso.get().load(fotokost).into(gambarkostdetail);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailTransaksiUser.this, "Terjadi Kesalahan!" + e, Toast.LENGTH_SHORT).show();
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
                    StatusTransaksi.setText((String) document.get("statusTransaksi"));

                    if (StatusTransaksi.getText().toString().contains("MENUNGGU KONFIRMASI")){
                        StatusTransaksi.setTextColor(Color.RED);
                    }else if (StatusTransaksi.getText().toString().contains("TELAH DIKONFIRMASI")){
                        Pembayaran.setVisibility(View.VISIBLE);
                        StatusTransaksi.setTextColor(Color.parseColor("#DA9A1F"));
                    }else if (StatusTransaksi.getText().toString().contains("TRANSAKSI SELESAI")){
                        Pembayaran.setVisibility(View.INVISIBLE);
                        keUlasan.setVisibility(View.VISIBLE);
                        StatusTransaksi.setTextColor(Color.parseColor("#009606"));
                    }else if (StatusTransaksi.getText().toString().contains("TRANSAKSI DITOLAK")){
                        StatusTransaksi.setTextColor(Color.RED);
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
                        Toast.makeText(DetailTransaksiUser.this, "Data Kamar yang Dipesan Sudah Dihapus Oleh Pemilik Kost", Toast.LENGTH_SHORT).show();
                        jeniskamar.setVisibility(View.GONE);
                    }

                }
            }
        });
    }

    private void loadDataReview(String namaKost, String namaPembeli){
        db.collection("Data Reviewer").whereEqualTo("namaReviewer", namaPembeli).whereEqualTo("namaKost", namaKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot documentquery = task.getResult();
                    if (!documentquery.isEmpty()){
                        keUlasan.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}