package com.android.ngekostapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.text.SymbolTable;
import android.net.Uri;
import android.os.Handler;
import android.os.strictmode.CleartextNetworkViolation;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.sql.Time;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import account.ngekostuser;
import kotlin.collections.UCollectionsKt;
import model.ListKost;
import model.JenisKamarKost;
import model.ListTransaksiKost;


public class SewaKamarUser extends AppCompatActivity {
    //Widget
    TextView NamaUser, AlamatUser, JenisKelaminUser, NamaKost, JenisKamarKost, LamaSewaKamar, TipeSewaKamar, HargaKost;
    Button SewaKost, TambahSewa, KurangSewa, HitungHarga;
    ImageButton tombolKembali;

    //Ambil Data User, Kamar dan Kost
    private String Namapengguna, NamaKamar, namaKost, NamaPemilik;
    private  String HargaKamar;
    private String gambarKost;
    private double harga;
    private int lamaSewa = 1;
    private int tambahinput = 1;
    private int kuranginput = 1;

    //Dialog
    Dialog popupberhasil;
    Dialog popupgagal;

    //Date
    private Date date = Calendar.getInstance().getTime();
    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private String tanggaltambah = dateFormat.format(date);

    //FireStore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference DataKamarKost = database.collection("Data Kamar");
    private CollectionReference datakost = database.collection("Data Kost");
    private CollectionReference pengguna = database.collection("Pengguna");
    private CollectionReference DataTransaksi = database.collection("Data Transaksi");

    private boolean sudahdiklik;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sewa_kamar_user);

        popupberhasil = new Dialog(this);
        popupgagal = new Dialog(this);

        NamaUser = findViewById(R.id.tampilNamaUser);
        AlamatUser = findViewById(R.id.tampilAlamatUser);
        JenisKelaminUser = findViewById(R.id.tampilJenisKelaminUser);
        NamaKost = findViewById(R.id.tampilNamaKostPemilik);
        JenisKamarKost = findViewById(R.id.tampilNamaKamarPemilik);
        LamaSewaKamar = findViewById(R.id.angkalamaSewa);
        TipeSewaKamar = findViewById(R.id.tampilLamaSewaKamar);
        HargaKost = findViewById(R.id.tampilHargaKamarUser);
        LamaSewaKamar.setText("1");
        HitungHarga = findViewById(R.id.buttonHitungHarga);

        tombolKembali = findViewById(R.id.tombolkembali);

        SewaKost = findViewById(R.id.buttonSewa);
        TambahSewa = findViewById(R.id.buttontambahLamaSewa);
        KurangSewa = findViewById(R.id.buttonKurangLamaSewa);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            user = firebaseAuth.getCurrentUser();
            assert user != null;
            final String userID = user.getUid();

            pengguna.whereEqualTo("userID", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        QuerySnapshot documentquery = task.getResult();
                        DocumentSnapshot document = documentquery.getDocuments().get(0);
                        Namapengguna = (String)document.get("nama");
                        NamaUser.setText(Namapengguna);
                        AlamatUser.setText((String)document.get("alamat"));
                        JenisKelaminUser.setText((String)document.get("jeniskelamin"));
                    }
                }
            });
            namaKost = extra.getString("namakost");
            loadDataKost(namaKost);
            NamaKamar = extra.getString("namakamar");
            loadDataKamar(NamaKamar);
            NamaPemilik = extra.getString("namapemilik");
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

        TambahSewa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sudahdiklik == true){
                    harga = Double.parseDouble(HargaKamar);
                }
                lamaSewa = lamaSewa + tambahinput;
                LamaSewaKamar.setText((Integer.toString(lamaSewa)));
            }
        });

        KurangSewa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sudahdiklik == true){
                    harga = Double.parseDouble((HargaKamar));
                }
                lamaSewa = lamaSewa - kuranginput;
                LamaSewaKamar.setText((Integer.toString(lamaSewa)));
            }
        });

        HitungHarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                harga = lamaSewa * harga;

                //Konversi
                Locale localID = new Locale("IND", "ID");
                NumberFormat numberFormat = NumberFormat.getNumberInstance(localID);
                String hargaKonversi = "Rp" + numberFormat.format(harga);

                HargaKost.setText(hargaKonversi);

                sudahdiklik = true;
            }
        });

        SewaKost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWarningDialog();
            }
        });

        tombolKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showWarningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SewaKamarUser.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(SewaKamarUser.this).inflate(
                R.layout.dialogcontainer,(ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView)view.findViewById(R.id.textTitle)).setText("Konfirmasi Sewa");
        ((TextView)view.findViewById(R.id.textMessage)).setText("Apakah Data Anda Sudah Benar ?");
        ((Button)view.findViewById(R.id.buttonYes)).setText("Ya");
        ((Button)view.findViewById(R.id.buttonNo)).setText("Tidak");
        ((ImageView)view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.warning);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                final LoadingAlert loadingAlert = new LoadingAlert(SewaKamarUser.this);

                loadingAlert.StartAlertDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingAlert.closeAlertDialog();
                        SimpanTransaksi(NamaKamar, NamaPemilik, gambarKost, tanggaltambah);
                    }
                }, 5000);
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void SimpanTransaksi(String NamaKamar, String namaPemilik, String gambarKost, String tanggaltambah){
        final String namaPembeli = NamaUser.getText().toString().trim();
        final String AlamatPembeli = AlamatUser.getText().toString().trim();
        final String JenisKelaminPembeli = JenisKelaminUser.getText().toString().trim();
        final String namaKost = NamaKost.getText().toString().trim();
        final String namaKamar = NamaKamar;
        final String HargaKamar = HargaKost.getText().toString().trim();
        final String LamaSewa = LamaSewaKamar.getText().toString().trim();
        final String TipeSewa = TipeSewaKamar.getText().toString().trim();
        final String statusTransaksi = "MENUNGGU KONFIRMASI";

        //Objek Transaksi
        ListTransaksiKost tambahTransaksi = new ListTransaksiKost();
        tambahTransaksi.setNamaPembeli(namaPembeli);
        tambahTransaksi.setAlamatPembeli(AlamatPembeli);
        tambahTransaksi.setJenisKelaminPembeli(JenisKelaminPembeli);
        tambahTransaksi.setNamaKost(namaKost);
        tambahTransaksi.setNamaKamar(namaKamar);
        tambahTransaksi.setNamaPemilik(namaPemilik);
        tambahTransaksi.setTotalHarga(HargaKamar);
        tambahTransaksi.setLamaSewa(LamaSewa + " " + TipeSewa);
        tambahTransaksi.setStatusTransaksi(statusTransaksi);
        tambahTransaksi.setWaktuDitambahkan(tanggaltambah);
        tambahTransaksi.setGambarkost(gambarKost);

        //Invoking Collection Reference
        DataTransaksi.add(tambahTransaksi).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                popupberhasil.setContentView(R.layout.popupsewaberhasil);
                popupberhasil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupberhasil.show();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        popupberhasil.dismiss();
                        SimpanIDTransaksi(namaPembeli, namaPemilik, namaKost, namaKamar);
                        Intent kembalikeDashBoard = new Intent(SewaKamarUser.this, DashUser.class);
                        startActivity(kembalikeDashBoard);
                        finish();
                    }
                }, 3000);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                popupgagal.setContentView(R.layout.popupsewagagal);
                popupgagal.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupgagal.show();

                final  Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        popupgagal.dismiss();
                    }
                }, 3000);
            }
        });
    }

    private void SimpanIDTransaksi(String namaPembeli, String namaPemilik, String namaKost, String namaKamar){
        DataTransaksi.whereEqualTo("NamaPembeli", namaPembeli).whereEqualTo("NamaPemilik", namaPemilik).whereEqualTo("NamaKost", namaKost).whereEqualTo("NamaKamar", namaKamar).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    String DocumentID = documentSnapshot.getId();
                    Map<String, Object> TambahID =new HashMap<>();
                    TambahID.put("DocID", DocumentID);
                    DataTransaksi.document(DocumentID).update(TambahID);
                }
            }
        });
    }

    private void loadDataKost(String namaKost){
        datakost.whereEqualTo("namakost", namaKost).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot documentQuery = task.getResult();
                    DocumentSnapshot document = documentQuery.getDocuments().get(0);
                    NamaKost.setText((String)document.get("namakost"));
                    gambarKost = (String) document.get("gambarkost");
                }
            }
        });
    }

    private void loadDataKamar(String namaKamar) {
        DataKamarKost.whereEqualTo("namaKamar", namaKamar).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot documentKost = task.getResult();
                    DocumentSnapshot document = documentKost.getDocuments().get(0);
                    JenisKamarKost.setText((String) document.get("namaKamar"));
                    TipeSewaKamar.setText((String) document.get("lamaSewa"));


                    //Konversi
                    double konversi = Double.parseDouble((String) document.get("harga"));
                    Locale localID = new Locale("IND", "ID");
                    NumberFormat numberFormat = NumberFormat.getNumberInstance(localID);
                    String hargaKonversi = "Rp" + numberFormat.format(konversi);

                    HargaKost.setText(hargaKonversi);
                    HargaKamar = (String) document.get("harga");
                    harga = Integer.parseInt(HargaKamar);
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