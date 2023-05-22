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
import android.telephony.TelephonyCallback;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.core.utilities.TreeNode;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import account.ngekostuser;
import kotlin.collections.UCollectionsKt;
import model.ListKost;
import model.JenisKamarKost;
import model.ListTransaksiKost;

public class PembayaranKostUser extends AppCompatActivity {
    private static final int GALLERY_CODE = 1;
    //Widget
    TextView NamaKost, JenisKamar, HargaKost;
    ImageView fotoTransaksi;
    Button choosephoto, Bayarkost;
    private Uri TransaksiGambar;

    //Dialog
    Dialog popupberhasil, popupgagal;

    //Ambil Data
    private String NamaKamar, namaKost, harga, namaPembeli, namaPemilik, pathFotoTransaksi;

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

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pembayaran_kost_user);

        HargaKost = findViewById(R.id.tampilHargaKamarUser);
        NamaKost = findViewById(R.id.tampilNamaKostPemilik);
        JenisKamar= findViewById(R.id.tampilNamaKamarPemilik);
        Bayarkost = findViewById(R.id.buttonBayar);
        choosephoto = findViewById(R.id.buttonambilfoto);

        popupberhasil = new Dialog(this);
        popupgagal = new Dialog(this);

        fotoTransaksi = findViewById(R.id.postImageView);


        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            NamaKamar = extra.getString("namaKamar");
            namaKost = extra.getString("namakost");
            harga = extra.getString("harga");
            namaPembeli = extra.getString("namaPembeli");
            namaPemilik = extra.getString("Pemilik");


            loadDataTransaksi(NamaKamar, namaKost, harga, namaPembeli, namaPemilik);
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

        Bayarkost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWarningDialog();
            }
        });
    }

    //Dialog Alert
    private void showWarningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PembayaranKostUser.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(PembayaranKostUser.this).inflate(
                R.layout.dialogcontainer,(ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView)view.findViewById(R.id.textTitle)).setText("Konfirmasi Pembayaran");
        ((TextView)view.findViewById(R.id.textMessage)).setText("Apakah Data Transaksi Anda Sudah Benar?");
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
                final LoadingAlert loadingAlert = new LoadingAlert(PembayaranKostUser.this);

                loadingAlert.StartAlertDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingAlert.closeAlertDialog();
                        simpanFotoTransaksi(NamaKamar, namaKost, harga, namaPembeli, namaPemilik);

                    }
                }, 5000);
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    protected void loadDataTransaksi(String NamaKamar, String namaKost, String harga, String namaPembeli, String namaPemilik){
        DataTransaksi.whereEqualTo("namaKost", namaKost).whereEqualTo("namaPemilik", namaPemilik).whereEqualTo("totalHarga", harga).whereEqualTo("namaKamar", NamaKamar).whereEqualTo("namaPembeli", namaPembeli).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot documentQuery = task.getResult();
                    DocumentSnapshot document = documentQuery.getDocuments().get(0);
                    NamaKost.setText((String) document.get("namaKost"));
                    JenisKamar.setText((String) document.get("namaKamar"));
                    HargaKost.setText((String) document.get("totalHarga"));
                }
            }
        });
    }

    protected void simpanFotoTransaksi(String NamaKamar, String namaKost, String harga, String namaPembeli, String namaPemilik){
        if (TransaksiGambar != null){
            final StorageReference filepath = storageReference.child("Transaksi_User").child("Transaksi_"+namaPembeli+"_"+namaKost+"_Pemilik:"+namaPemilik+"_"+ tanggaltambah);
            filepath.putFile(TransaksiGambar).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            pathFotoTransaksi = uri.toString();
                            String status = "TRANSAKSI SELESAI";

                            Map<String, Object> TambahFotoTransaksi = new HashMap<>();
                            TambahFotoTransaksi.put("fotoTransaksi", pathFotoTransaksi);
                            TambahFotoTransaksi.put("statusTransaksi", status);

                            DataTransaksi.whereEqualTo("namaKost", namaKost).whereEqualTo("namaPemilik", namaPemilik).whereEqualTo("totalHarga", harga).whereEqualTo("namaKamar", NamaKamar).whereEqualTo("namaPembeli", namaPembeli).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()){
                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                        String DocumentID = documentSnapshot.getId();
                                        DataTransaksi.document(DocumentID).update(TambahFotoTransaksi).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                popupberhasil.setContentView(R.layout.popuppembayaranberhasil);
                                                popupberhasil.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                popupberhasil.show();

                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        popupberhasil.dismiss();
                                                        Intent keDetailTransaksi = new Intent(PembayaranKostUser.this, DetailTransaksiUser.class);
                                                        keDetailTransaksi.putExtra("namakost", namaKost);
                                                        keDetailTransaksi.putExtra("namaPembeli", namaPembeli);
                                                        keDetailTransaksi.putExtra("namaKamar", NamaKamar);
                                                        keDetailTransaksi.putExtra("harga", harga);
                                                        keDetailTransaksi.putExtra("namaPemilik", namaPemilik);
                                                        finish();
                                                    }
                                                }, 3000);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                popupgagal.setContentView(R.layout.popuppembyarangagal);
                                                popupgagal.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                popupgagal.show();

                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        popupgagal.dismiss();
                                                    }
                                                }, 3000);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }else{
            Toast.makeText(this, "Mohon Tambahkan Foto Transaksi Anda!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if (data !=null){
                TransaksiGambar = data.getData(); //Mendapatkan Path Aktual
                fotoTransaksi.setImageURI(TransaksiGambar); //Menampilkan Gambar
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