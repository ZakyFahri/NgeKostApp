package com.android.ngekostapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import android.os.Bundle;

public class DetailKamarPemilik extends AppCompatActivity {
    //Widget
    private static final int GALLERY_CODE = 1;

    ImageView gambarKamar;
    TextView namakamar, harga, lamasewa, jumlahkamar, luasKamar, fasilitas;
    Button keEditKamar, HapusKamar;
    ImageButton tombolKembali;

    //Dialog PopUp
    Dialog popupHapusBerhasil;
    Dialog popupHapusGagal;

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
    private CollectionReference Pemilik = db.collection("Pemilik");

    private Uri FotoKamar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_detail_kamar_pemilik);

        namakamar = findViewById(R.id.tvJenisKamar);
        gambarKamar = findViewById(R.id.postImageKost);
        harga = findViewById(R.id.hasilHarga1);
        lamasewa = findViewById(R.id.hasilHarga3);
        jumlahkamar = findViewById(R.id.hasilKamar);
        luasKamar = findViewById(R.id.hasilLuas);
        fasilitas = findViewById(R.id.hasilFasilitas);

        popupHapusBerhasil = new Dialog(this);
        popupHapusGagal = new Dialog(this);

        tombolKembali = findViewById(R.id.tombolkembali);

        keEditKamar = findViewById(R.id.buttonEdit);
        HapusKamar = findViewById(R.id.buttonHapus);

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

        keEditKamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keEditKamar = new Intent(DetailKamarPemilik.this, EditJenisKamarPemilik.class);
                keEditKamar.putExtra("namakamar", namaKamar);
                keEditKamar.putExtra("namakost", namaKost);
                startActivity(keEditKamar);
            }
        });

        HapusKamar.setOnClickListener(new View.OnClickListener() {
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

    //Dialog Alert
    private void showWarningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailKamarPemilik.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailKamarPemilik.this).inflate(
                R.layout.dialogcontainer,(ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView)view.findViewById(R.id.textTitle)).setText("Konfirmasi Hapus Kamar");
        ((TextView)view.findViewById(R.id.textMessage)).setText("Apakah Anda yakin Ingin Hapus Kamar?");
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

                final LoadingAlert loadingAlert = new LoadingAlert(DetailKamarPemilik.this);
                loadingAlert.StartAlertDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingAlert.closeAlertDialog();
                        hapusKamar(namaKamar, namaKost);
                    }
                }, 3000);
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

                    //Gambar Kamar
                    fotoKamarpath = (String)document.get("gambarKamar");
                    FotoKamar = Uri.parse(fotoKamarpath);
                    Picasso.get().load(FotoKamar).into(gambarKamar);
                }
            }
        });
    }

    private void hapusKamar(String namaKamar, String namaKost){
        user = firebaseAuth.getCurrentUser();
        assert user != null;
        final String userID = user.getUid();

        Pemilik.whereEqualTo("userID", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot documentquery = task.getResult();
                DocumentSnapshot document = documentquery.getDocuments().get(0);
                namaPemilik = (String)document.get("nama");

                DataKamar.whereEqualTo("namaKamar", namaKamar).whereEqualTo("namaPemilik", namaPemilik).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot documentquery = task.getResult();
                        DocumentSnapshot document = documentquery.getDocuments().get(0);
                        String DocumentID = document.getId();

                        DataKamar.document(DocumentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                popupHapusBerhasil.setContentView(R.layout.popuphapuskamarberhasil);
                                popupHapusBerhasil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popupHapusBerhasil.show();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent balikkeDetailKost = new Intent(DetailKamarPemilik.this, DetailKostPemilik.class);
                                        balikkeDetailKost.putExtra("namakost", namaKost);
//                                startActivity(balikkeDetailKost);
                                        finish();
                                    }
                                }, 2000);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                popupHapusGagal.setContentView(R.layout.popuphapuskamargagal);
                                popupHapusGagal.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popupHapusGagal.show();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        popupHapusGagal.dismiss();
                                    }
                                }, 2000);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        finish();
        startActivity(getIntent());
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